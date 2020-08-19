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

public class HomomorphismLengthAtMostData implements TemporalTestData {
  @Override
  public Collection<String[]> getData() {
    ArrayList<String[]> data = new ArrayList<>();

    //1.[(Broadway & E14) -> (S 5 Pl & S 5 St)]
    //2.[(Stanton St & Chrystie) -> (Hancock St & Bedford Ave)]
    //3.[(Lispenard St & Broadway) -> (Broadway & W 51)]
    //4.[(W 37 St & 5 Ave) -> (Hicks St & Montague St)]
    //5.[(Hicks St & Montague St) -> (Hicks St & Montague St)]
    data.add(new String[] {
      "LengthAtMost_HOM_1_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE Interval(1970-01-01T00:00:00, 1970-01-01T00:30:00)" +
          ".lengthAtMost(val)"
      ),
      "expected1,expected2,expected3,expected4,expected5",
      "expected1[(s8)-[e6]->(s9)], expected2[(s12)-[e8]->(s13)], " +
        "expected3[(s28)-[e18]->(s29)], expected4[(s7)-[e5]->(s2)], " +
        "expected5[(s2)-[e2]->(s2)]"
    });

    //1.[(Murray St & West St) -> (Shevchenko Pl & E 7 St)]
    //2.[(Murray St & West St) -> (Greenwich St & W Houston St)]
    //3.[(DeKalb Ave & S Portland Ave) -> (Fulton St & Grand Ave)]
    //4.[(E 33 St & 2 Ave) -> (Division St & Bowery)
    data.add(new String[] {
      "LengthAtMost_HOM_2_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE Interval(2013-05-10, 2013-07-24)" +
          ".lengthAtMost(a.val.join(b.val))"
      ),
      "expected1,expected2,expected3,expected4",
      "expected1[(s24)-[e15]->(s25)], expected2[(s24)-[e16]->(s26)], expected3[(s19)-[e12]->(s20)]," +
        "expected4[(s22)-[e14]->(s23)]"
    });

    //empty
    data.add(new String[] {
      "LengthAtMost_HOM_3_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE Interval(1970-01-01, 1970-01-01T01:00:00)" +
          ".lengthAtMost(e.tx)"
      ),
      "",
      ""
    });

    //1.[(E15 St) -> (Washington Park)]
    data.add(new String[] {
      "LengthAtMost_HOM_4_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE " +
          "val.lengthAtMost(Minutes(5))"
      ),
      "expected1",
      "expected1[(s3)-[e3]->(s4)]"
    });

    //1.[(Broadway & E14) -> (S 5 Pl & S 5 St)]
    //2.[(Stanton St & Chrystie) -> (Hancock St & Bedford Ave)]
    //3.[(Lispenard St & Broadway) -> (Broadway & W 51)]
    //4.[(W 37 St & 5 Ave) -> (Hicks St & Montague St)]
    //5.[(Hicks St & Montague St) -> (Hicks St & Montague St)]
    data.add(new String[] {
      "LengthAtMost_HOM_5_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE " +
          "Interval(1970-01-01, 1970-01-01T00:30:00).lengthAtMost(val)"
      ),
      "expected1,expected2,expected3,expected4,expected5",
      "expected1[(s8)-[e6]->(s9)], expected2[(s12)-[e8]->(s13)], " +
        "expected3[(s28)-[e18]->(s29)], expected4[(s7)-[e5]->(s2)], " +
        "expected5[(s2)-[e2]->(s2)]"
    });

    //empty
    data.add(new String[] {
      "LengthAtMost_HOM_6_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE NOT val.lengthAtMost(b.tx)"
      ),
      "",
      ""
    });

    //1.[(Henry St & Grand St) (Murray St & West St)]
    //2.[(Henry St & Grand St) (Shevchenko Pl)]
    //3.[(Henry St & Grand St) (Henry St & Grand St)]
    data.add(new String[] {
      "LengthAtMost_HOM_7_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a) (b) WHERE a.vertexId=18 AND a.val.lengthAtMost(b.val)"
      ),
      "expected1,expected2,expected3",
      "expected1[(s18)(s24)], expected2[(s18)(s25)], expected3[(s18)(s18)]"
    });

    // 1.[(9 Ave & W18)<-[e1]-(Broadway & W24)-[e0]->(9 Ave & W18)]
    // 2.[(Greenwich St)<-(Murray St & West St)->(Shevchenko Pl)]
    // 3.[(8 Ave & W31)<-[e19]-(Broadway & W29)-[e13]->(8 Ave & W31)]
    data.add(new String[] {
      "LengthAtMost_HOM_8_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)<-[e1]-(b)-[e2]->(c) WHERE NOT e2.tx.lengthAtMost(e1.tx)"
      ),
      "expected1,expected2,expected3",
      "expected1[(s1)<-[e1]-(s0)-[e0]->(s1)], expected2[(s26)<-[e16]-(s24)-[e15]->(s25)], " +
        "expected3[(s11)<-[e19]-(s21)-[e13]->(s11)]"
    });

    // 1.[(Murray St & West St) -> (Shevchenko Pl)
    data.add(new String[] {
      "LengthAtMost_HOM_9_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE NOT a.val.merge(b.val).lengthAtMost(Days(65))"
      ),
      "expected1",
      "expected1[(s24)-[e15]->(s25)]"
    });

    //1.[(E15 St) -> (Washington Park)]
    data.add(new String[] {
      "LengthAtMost_HOM_10_default_citibike",
      CBCypherTemporalPatternMatchingTest.defaultData,
      CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
        "MATCH (a)-[e]->(b) WHERE " +
          "Interval(MAX(a.tx_from, e.tx_from), " +
          "MIN(e.val_to, b.tx_to))" +
          ".lengthAtMost(Minutes(5))"
      ),
      "expected1",
      "expected1[(s3)-[e3]->(s4)]"
    });

    return data;
  }
}