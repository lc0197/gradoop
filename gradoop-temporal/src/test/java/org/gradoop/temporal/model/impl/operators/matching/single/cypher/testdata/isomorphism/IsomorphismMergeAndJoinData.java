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
package org.gradoop.temporal.model.impl.operators.matching.single.cypher.testdata.isomorphism;

import org.gradoop.temporal.model.impl.operators.matching.TemporalTestData;

import java.util.ArrayList;
import java.util.Collection;

public class IsomorphismMergeAndJoinData implements TemporalTestData {
  @Override
  public Collection<String[]> getData() {
    ArrayList<String[]> data = new ArrayList<>();

    //1.[(Fulton St) (Shevchenko Pl)
    data.add(new String[] {
      "MergeJoin_ISO_1_default_citibike",
      "MATCH (a) (b) WHERE a.id>b.id AND a.val.merge(b.val).overlaps(" +
        "Interval(Timestamp(2013-07-28T12:00), Timestamp(2013-07-30)))",
      "expected1",
      "expected1[(s20)(s25)]"
    });

    // empty
    data.add(new String[] {
      "MergeJoin_ISO_2_default_citibike",
      "MATCH (a) WHERE NOT a.val.merge(a.val).contains(a.val.join(a.val))",
      "[]",
      "[]"
    });
    // 1.[(Murray St & West St) -> (Shevchenko Pl)]
    data.add(new String[] {
      "MergeJoin_ISO_3_default_citibike",
      "MATCH (a)-[e]->(b) WHERE a.tx.join(b.tx).contains(Interval(" +
        " Timestamp(2013-05-12),Timestamp(2013-07-28)))",
      "expected1",
      "expected1[(s24)-[e15]->(s25)]"
    });

    // do not merge/join when no overlap
    // 1. [(Broadway & W 24 St) -[e1]-> (9 Ave & W 18)]

    data.add(new String[] {
      "MergeJoin_ISO_4_default_citibike",
      "MATCH (a)-[e]->(b) WHERE a.id=444 AND Interval(Timestamp(1970-01-01),Timestamp(1970-01-02)).precedes(" +
        "e.val.join(Interval(Timestamp(2013-06-01T00:11:40),Timestamp(2017-01-01))))",
      "expected1",
      "expected1[(s0)-[e1]->(s1)]"
    });

    return data;
  }
}
