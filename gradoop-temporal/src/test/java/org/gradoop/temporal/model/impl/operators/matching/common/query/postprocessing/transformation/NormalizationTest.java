/*
 * Copyright © 2014 - 2021 Leipzig University (Database Research Group)
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

import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.CNF;
import org.junit.Test;
import org.gradoop.gdl.model.comparables.time.TimeSelector;
import org.gradoop.gdl.model.predicates.expressions.Comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.gradoop.gdl.utils.Comparator.EQ;
import static org.gradoop.gdl.utils.Comparator.GT;
import static org.gradoop.gdl.utils.Comparator.GTE;
import static org.gradoop.gdl.utils.Comparator.LT;
import static org.gradoop.gdl.utils.Comparator.LTE;
import static org.gradoop.gdl.utils.Comparator.NEQ;

public class NormalizationTest {

  TimeSelector ts1 = new TimeSelector("a", TimeSelector.TimeField.TX_FROM);
  TimeSelector ts2 = new TimeSelector("b", TimeSelector.TimeField.TX_TO);

  Comparison lte = new Comparison(ts1, LTE, ts2);
  Comparison lt = new Comparison(ts1, LT, ts2);
  Comparison eq = new Comparison(ts1, EQ, ts2);
  Comparison neq = new Comparison(ts1, NEQ, ts2);
  Comparison gte = new Comparison(ts1, GTE, ts2);
  Comparison gt = new Comparison(ts1, GT, ts2);

  Normalization normalization = new Normalization();

  @Test
  public void singleClauseTest() {
    // for GT or GTE, sides should be switched
    CNF gteCNF = Util.cnfFromLists(Collections.singletonList(gte));
    CNF gteExpected = Util.cnfFromLists(Collections.singletonList(gte.switchSides()));
    assertEquals(gteExpected, normalization.transformCNF(gteCNF));

    CNF gtCNF = Util.cnfFromLists(Collections.singletonList(gt));
    CNF gtExpected = Util.cnfFromLists(Collections.singletonList(gt.switchSides()));
    assertEquals(gtExpected, normalization.transformCNF(gtCNF));

    // all other comparisons should be left unchanged
    CNF eqCNF = Util.cnfFromLists(Collections.singletonList(eq));
    assertEquals(eqCNF, normalization.transformCNF(eqCNF));

    CNF neqCNF = Util.cnfFromLists(Collections.singletonList(neq));
    assertEquals(neqCNF, normalization.transformCNF(neqCNF));

    CNF ltCNF = Util.cnfFromLists(Collections.singletonList(lt));
    assertEquals(ltCNF, normalization.transformCNF(ltCNF));

    CNF lteCNF = Util.cnfFromLists(Collections.singletonList(lte));
    assertEquals(lteCNF, normalization.transformCNF(lteCNF));
  }

  @Test
  public void complexCNFTest() {
    // input
    ArrayList<Comparison> clause1 = new ArrayList<>(Arrays.asList(eq, gt, lte));
    ArrayList<Comparison> clause2 = new ArrayList<>(Arrays.asList(neq, lt));
    ArrayList<Comparison> clause3 = new ArrayList<>(Collections.singletonList(gte));
    CNF input = Util.cnfFromLists(clause1, clause2, clause3);

    // expected
    ArrayList<Comparison> expectedClause1 = new ArrayList<>(Arrays.asList(eq, gt.switchSides(), lte));
    ArrayList<Comparison> expectedClause2 = new ArrayList<>(Arrays.asList(neq, lt));
    ArrayList<Comparison> expectedClause3 = new ArrayList<>(Collections.singletonList(gte.switchSides()));
    CNF expected = Util.cnfFromLists(expectedClause1, expectedClause2, expectedClause3);

    assertEquals(expected, normalization.transformCNF(input));
  }
}
