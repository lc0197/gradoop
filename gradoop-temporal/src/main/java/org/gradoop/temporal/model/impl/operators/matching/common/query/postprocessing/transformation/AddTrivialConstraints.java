/*
 * Copyright © 2014 - 2020 Leipzig University (Database Research Group)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradoop.temporal.model.impl.operators.matching.common.query.postprocessing.transformation;

import org.gradoop.temporal.model.impl.operators.matching.common.query.postprocessing.QueryTransformation;
import org.gradoop.temporal.model.impl.operators.matching.common.query.postprocessing.exceptions.QueryContradictoryException;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.CNFElementTPGM;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.QueryComparableTPGM;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.TemporalCNF;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.TimeLiteralComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.TimeSelectorComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.expressions.ComparisonExpressionTPGM;
import org.s1ck.gdl.model.comparables.time.TimeLiteral;
import org.s1ck.gdl.model.comparables.time.TimeSelector;
import org.s1ck.gdl.model.predicates.expressions.Comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.s1ck.gdl.utils.Comparator.LTE;

/**
 * Adds constraints a.tx_from<=a.tx_to and a.valid_from<=a.valid_to
 * for every variable.
 * The constraint a.tx_from<=a.tx_to is only added, if a selector
 * a.tx_from or a.tx_to is found within the singleton clauses of the CNF
 */
public class AddTrivialConstraints implements QueryTransformation {
  @Override
  public TemporalCNF transformCNF(TemporalCNF cnf) throws QueryContradictoryException {
    List<Set<String>> variableSets = getNecessaryFields(cnf);
    Set<String> txVars = variableSets.get(0);
    Set<String> valVars = variableSets.get(1);
    for (String variable : txVars) {
      cnf = addSingletonClause(cnf, new Comparison(
        new TimeSelector(variable, TimeSelector.TimeField.TX_FROM),
        LTE, new TimeSelector(variable, TimeSelector.TimeField.TX_TO)
      ));
    }
    for (String variable : valVars) {
      cnf = addSingletonClause(cnf, new Comparison(
        new TimeSelector(variable, TimeSelector.TimeField.VAL_FROM),
        LTE, new TimeSelector(variable, TimeSelector.TimeField.VAL_TO)
      ));
    }

    ArrayList<TimeLiteral> literals = getNecessaryLiterals(cnf);
    literals.sort(new Comparator<TimeLiteral>() {
      @Override
      public int compare(TimeLiteral timeLiteral, TimeLiteral t1) {
        return Long.compare(timeLiteral.getMilliseconds(), t1.getMilliseconds());
      }
    });
    for (int i = 0; i < literals.size(); i++) {
      for (int j = i + 1; j < literals.size(); j++) {
        if (literals.get(i).getMilliseconds() == literals.get(j).getMilliseconds()) {
          continue;
        }
        cnf = addSingletonClause(cnf, new Comparison(
          literals.get(i), LTE, literals.get(j)
        ));
      }
    }
    return cnf;
  }

  /**
   * Adds a singleton clause to an existing CNF
   *
   * @param cnf        CNF
   * @param comparison comparison that makes up the single clause to add
   * @return CNF with singleton clause appended
   */
  private TemporalCNF addSingletonClause(TemporalCNF cnf, Comparison comparison) {
    return cnf.and(new TemporalCNF(Collections.singletonList(
      new CNFElementTPGM(Collections.singletonList(
        new ComparisonExpressionTPGM(comparison)))
    )));
  }

  /**
   * Generates a set of variables for transaction times and valid times.
   * A variable is included in the set for transaction times, iff
   * a selector a.tx_(from/to) occurs in the singleton CNF clauses.
   * (analogously for valid times)
   *
   * @param cnf CNF
   * @return list containing set of tx variables at index 0, set of
   * val variables at index 1
   */
  private List<Set<String>> getNecessaryFields(TemporalCNF cnf) {
    HashSet<String> txSet = new HashSet<>();
    HashSet<String> valSet = new HashSet<>();
    for (CNFElementTPGM clause : cnf.getPredicates()) {
      if (clause.size() != 1 || !clause.getPredicates().get(0).isTemporal()) {
        continue;
      } else {

        QueryComparableTPGM[] comparables = new QueryComparableTPGM[] {
          clause.getPredicates().get(0).getLhs(),
          clause.getPredicates().get(0).getRhs()
        };
        for (QueryComparableTPGM comparable : comparables) {
          if (comparable instanceof TimeSelectorComparable) {
            TimeSelector selector = (TimeSelector)
              comparable.getWrappedComparable();
            String variable = selector.getVariable();
            TimeSelector.TimeField field = selector.getTimeProp();
            if (field == TimeSelector.TimeField.TX_FROM ||
              field == TimeSelector.TimeField.TX_TO) {
              txSet.add(variable);
            } else {
              valSet.add(variable);
            }
          }
        }
      }
    }
    return new ArrayList<>(Arrays.asList(txSet, valSet));
  }

  /**
   * Finds all time literals that are included in necessary comparisons, i.e.
   * comparisons that make up a single cnf clause
   *
   * @param cnf CNF to search for necessary time literals
   * @return list of time literals that are included in necessary comparisons
   */
  private ArrayList<TimeLiteral> getNecessaryLiterals(TemporalCNF cnf) {
    HashSet<TimeLiteral> literals = new HashSet<>();
    for (CNFElementTPGM clause : cnf.getPredicates()) {
      if (clause.size() != 1 || !clause.getPredicates().get(0).isTemporal()) {
        continue;
      } else {
        ComparisonExpressionTPGM comparison = clause.getPredicates().get(0);
        if (comparison.getLhs() instanceof TimeLiteralComparable) {
          literals.add((TimeLiteral) comparison.getLhs().getWrappedComparable());
        }
        if (comparison.getRhs() instanceof TimeLiteralComparable) {
          literals.add((TimeLiteral) comparison.getRhs().getWrappedComparable());
        }
      }
    }
    return new ArrayList<>(literals);
  }
}
