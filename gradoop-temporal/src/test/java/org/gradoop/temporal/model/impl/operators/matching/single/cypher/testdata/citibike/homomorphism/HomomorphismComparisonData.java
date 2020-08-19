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
package org.gradoop.temporal.model.impl.operators.matching.single.cypher.testdata.citibike.homomorphism;

import org.gradoop.temporal.model.impl.operators.matching.TemporalTestData;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.CBCypherTemporalPatternMatchingTest;

import java.util.ArrayList;
import java.util.Collection;

public class HomomorphismComparisonData implements TemporalTestData {
  @Override
  public Collection<String[]> getData() {
    ArrayList<String[]> data = new ArrayList<>();

    // 1. [(E15 St) -> (Washington P.)      (Broadway & W24) -[edgeId:1]-> (9 Ave & W18)]
    // 2. [(E15 St) -> (Washington P.)      (Broadway & W24) -[edgeId:0]-> (9 Ave & W18)]
    // 3. [(E15 St) -> (Washington P.)      (Hicks St) -> (Hicks St)]
    data.add(new String[] {
      "Comparison_HOM_1_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b) (c)-[e2]->(d) " +
          "WHERE e1.val_from>e2.val_from AND a.id=475"),
      "expected1,expected2,expected3",
      "expected1[(s3)-[e3]->(s4) (s0)-[e0]->(s1)], " +
        "expected2[(s3)-[e3]->(s4) (s0)-[e1]->(s1)], " +
        "expected3[(s3)-[e3]->(s4) (s2)-[e2]->(s2)]"
    });


    // 1. [(Broadway & W29)-[edgeId:13]->(8 Ave & W31)      (Broadway & W29)-[edgeId:19]->(8 Ave & W31)]
    data.add(new String[] {
      "Comparison_HOM_2_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b) (c)-[e2]->(d) WHERE a.id=486 AND c.id=486 " +
          "AND e2.val_to>e1.val_to"),
      "expected1",
      "expected1[(s21)-[e13]->(s11) (s21)-[e19]->(s11)]"
    });


    // 1. [(Hicks St & Montague) -> (Hicks St & Montague) <- (W 37 St & 5 Ave)]
    data.add(new String[] {
      "Comparison_HOM_3_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b)<-[e2]-(c) WHERE b.id=406 AND " +
          "e2.tx_from > e1.tx_from"),
      "expected1",
      "expected1[(s2)-[e2]->(s2)<-[e5]-(s7)]"
    });


    // 1.[(Hicks St)->(Hicks St)]
    // 2.[Broadway & W24) -[edgeId:0]-> (9 Ave & W18)
    // 3.[Broadway & W24) -[edgeId:1]-> (9 Ave & W18)
    data.add(new String[] {
      "Comparison_HOM_4_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE 2013-06-01T00:01:00>=e.tx_from"),
      "expected1,expected2,expected3",
      "expected1[(s2)-[e2]->(s2)], expected2[(s0)-[e0]->(s1)], expected3[(s0)-[e1]->(s1)]"
    });

    // 1.[(Shevchenko Pl & E7 St)]
    // 2.[(Fulton St & Grand Ave)]
    data.add(new String[] {
      "Comparison_HOM_5_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a) WHERE a.tx_to >= 2013-07-28T00:59:59"
      ),
      "expected1,expected2",
      "expected1[(s25)], expected2[(s20)]"
    });

    // 1.[(Hicks St)->(Hicks St)]
    data.add(new String[] {
      "Comparison_HOM_6_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE 2013-06-01T00:01:00 > e.tx_from " +
          "AND NOT b.tx_from.after(a.tx_from)"),
      "expected1",
      "expected1[(s2)-[e2]->(s2)]"
    });

    // (empty)
    data.add(new String[] {
      "Comparison_HOM_7_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE e.tx_to > b.val_to"
      ),
      "",
      ""
    });


    // 1.[(Hicks St)->(Hicks St)]
    data.add(new String[] {
      "Comparison_HOM_8_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE 2013-06-01T00:01:00 >= e.tx_from " +
          "AND NOT 2013-06-01T00:30:00> tx_to"),
      "expected1",
      "expected1[(s2)-[e2]->(s2)]"
    });

    // (empty)
    data.add(new String[] {
      "Comparison_HOM9_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a) WHERE val_from > tx_to"
      ),
      "",
      ""
    });


    // 1. [(E15 St) -> (Washington P.)      (Broadway & W24) -[edgeId:1]-> (9 Ave & W18)]
    // 2. [(E15 St) -> (Washington P.)      (Broadway & W24) -[edgeId:0]-> (9 Ave & W18)]
    // 3. [(E15 St) -> (Washington P.)      (Hicks St) -> (Hicks St)]
    data.add(new String[] {
      "Comparison_HOM_10_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b) (c)-[e2]->(d) WHERE e2.val_from<e1.val_from " +
          "AND a.id=475"),
      "expected1,expected2,expected3",
      "expected1[(s3)-[e3]->(s4) (s0)-[e0]->(s1)], " +
        "expected2[(s3)-[e3]->(s4) (s0)-[e1]->(s1)], " +
        "expected3[(s3)-[e3]->(s4) (s2)-[e2]->(s2)]"
    });


    // 1. [(Broadway & W29)-[edgeId:13]->(8 Ave & W31)      (Broadway & W29)-[edgeId:19]->(8 Ave & W31)]
    data.add(new String[] {
      "Comparison_HOM_11_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b) (c)-[e2]->(d) WHERE a.id=486 " +
          "AND c.id=486 AND e1.val_to < e2.val_to"),
      "expected1",
      "expected1[(s21)-[e13]->(s11) (s21)-[e19]->(s11)]"
    });


    // 1. [(Hicks St & Montague) -> (Hicks St & Montague) <- (W 37 St & 5 Ave)]
    data.add(new String[] {
      "Comparison_HOM_12_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b)<-[e2]-(c) WHERE b.id=406 AND e1.tx_from < e2.tx_from"),
      "expected1",
      "expected1[(s2)-[e2]->(s2)<-[e5]-(s7)]"
    });


    // 1.[(Hicks St)->(Hicks St)]
    // 2.[Broadway & W24) -[edgeId:0]-> (9 Ave & W18)
    // 3.[Broadway & W24) -[edgeId:1]-> (9 Ave & W18)
    data.add(new String[] {
      "Comparison_HOM_13_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE e.tx_from <= 2013-06-01T00:01:00"),
      "expected1,expected2,expected3",
      "expected1[(s2)-[e2]->(s2)], expected2[(s0)-[e0]->(s1)], expected3[(s0)-[e1]->(s1)]"
    });

    // 1.[(Broadway & E14)]
    // 2.[(Hancock St & Bedford Ave)]
    // 3.[(Little West St & 1 Pl)]
    data.add(new String[] {
      "Comparison_HOM_14_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a) WHERE a.tx_to < 2013-07-11"
      ),
      "expected1,expected2,expected3",
      "expected1[(s8)], expected2[(s13)], expected3[(s5)]"

    });

    // 1.[(Hicks St)->(Hicks St)]
    data.add(new String[] {
      "Comparison_HOM_15_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE e.tx_from <= 2013-06-01T00:01:00" +
          " AND 2013-05-12 < a.val_from " +
          "AND a.val_from.before(e.tx_from)"),
      "expected1",
      "expected1[(s2)-[e2]->(s2)]"
    });

    // (empty)
    data.add(new String[] {
      "Comparison_HOM_16_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE e.val_from < a.val_from"
      ),
      "",
      ""
    });

    // 1.[(Broadway & E14)]
    data.add(new String[] {
      "Comparison_HOM_17_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a) WHERE a.tx_to < 2013-07-11 AND" +
          " 2013-05-28.before(val_from)"
      ),
      "expected1",
      "expected1[(s8)]"

    });

    // 1. [(E15 St) -> (Washington P.)      (Broadway & W24) -[edgeId:1]-> (9 Ave & W18)]
    // 2. [(E15 St) -> (Washington P.)      (Broadway & W24) -[edgeId:0]-> (9 Ave & W18)]
    // 3. [(E15 St) -> (Washington P.)      (Hicks St) -> (Hicks St)]
    data.add(new String[] {
      "Comparison_HOM_18_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e1]->(b) (c)-[e2]->(d) WHERE e2.val_from<e1.val_from " +
          "AND a.id=475 AND MIN(a.val_from, b.val_from, c.val_from, d.val_from) < " +
          "MAX(e1.val_to, e2.val_to)"),
      "expected1,expected2,expected3",
      "expected1[(s3)-[e3]->(s4) (s0)-[e0]->(s1)], " +
        "expected2[(s3)-[e3]->(s4) (s0)-[e1]->(s1)], " +
        "expected3[(s3)-[e3]->(s4) (s2)-[e2]->(s2)]"
    });

    // empty
    data.add(new String[] {
      "Comparison_HOM_19_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE MAX(e.val_to, a.val_to)=val_to"
      ),
      "",
      ""
    });


    return data;
  }
}