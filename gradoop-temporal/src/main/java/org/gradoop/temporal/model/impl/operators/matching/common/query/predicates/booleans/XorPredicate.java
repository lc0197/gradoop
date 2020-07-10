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
package org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.booleans;

import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.QueryPredicateTPGM;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.TemporalCNF;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.util.QueryPredicateFactory;
import org.s1ck.gdl.model.predicates.Predicate;
import org.s1ck.gdl.model.predicates.booleans.And;
import org.s1ck.gdl.model.predicates.booleans.Not;
import org.s1ck.gdl.model.predicates.booleans.Or;
import org.s1ck.gdl.model.predicates.booleans.Xor;

import java.util.Objects;

/**
 * Wraps a {@link org.s1ck.gdl.model.predicates.booleans.Xor} predicate
 */
public class XorPredicate extends QueryPredicateTPGM {
  /**
   * Holds the wrapped predicate
   */
  private final Xor xor;

  /**
   * Creates a new Wrapper
   *
   * @param xor The wrapped xor predicate
   */
  public XorPredicate(Xor xor) {
    this.xor = xor;
  }

  /**
   * Converts the predicate into conjunctive normal form
   *
   * @return predicate in cnf
   */
  public TemporalCNF asCNF() {
    Predicate lhs = xor.getArguments()[0];
    Predicate rhs = xor.getArguments()[1];

    QueryPredicateTPGM wrapper = QueryPredicateFactory.createFrom(
      new Or(new And(lhs, new Not(rhs)), new And(new Not(lhs), rhs))
    );

    return wrapper.asCNF();
  }

  /**
   * Returns the wrapped left hand side predicate
   *
   * @return wrapped left hand side predicate
   */
  public QueryPredicateTPGM getLhs() {
    return QueryPredicateFactory.createFrom(xor.getArguments()[0]);
  }

  /**
   * Returns the wrapped right hand side predicate
   *
   * @return wrapped right hand side predicate
   */
  public QueryPredicateTPGM getRhs() {
    return QueryPredicateFactory.createFrom(xor.getArguments()[1]);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    XorPredicate that = (XorPredicate) o;

    return Objects.equals(xor, that.xor);
  }

  @Override
  public int hashCode() {
    return xor != null ? xor.hashCode() : 0;
  }
}
