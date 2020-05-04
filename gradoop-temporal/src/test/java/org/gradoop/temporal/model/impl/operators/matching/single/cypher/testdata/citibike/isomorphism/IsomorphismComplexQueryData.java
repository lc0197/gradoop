package org.gradoop.temporal.model.impl.operators.matching.single.cypher.testdata.citibike.isomorphism;

import org.gradoop.temporal.model.impl.operators.matching.TemporalTestData;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.CBCypherTemporalPatternMatchingTest;

import java.util.ArrayList;
import java.util.Collection;

public class IsomorphismComplexQueryData implements TemporalTestData {
    @Override
    public Collection<String[]> getData() {
        ArrayList<String[]> data = new ArrayList<>();

        /*
         * 1.[Broadway & W24) -[edgeId:0]-> (9 Ave & W18)
         * 2.[Broadway & W24) -[edgeId:1]-> (9 Ave & W18)
         */
        data.add(new String[]{
                "Complex_ISO_1_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE 2013-06-01T00:01:00.after(e.tx_from) " +
                                "AND e.asOf(2013-06-01T00:01:00)"),
                "expected1,expected2",
                "expected1[(s0)-[e0]->(s1)], expected2[(s0)-[e1]->(s1)]"
        });

        /*
         * 1.[(Broadway & W29) -[edgeId:19]->(8 Ave & W31)]
         * 2.[(Lispenard St) -> (Broadway & W 51 St)]
         * 3.1.[(E15 St & Irving Pl) -> (Washington Park)]
         */
        data.add(new String[]{
                "Precedes_ISO_2_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE Interval(2013-06-01T00:00:00, 2013-06-01T00:07:00)" +
                                ".precedes(e.tx) OR (NOT "+
                                "e.val.between(2013-06-01T00:04:00, 2013-06-01T00:08:00) AND " +
                                " b.val.between(2013-07-22T00:00:00, 2013-07-30))"),
                "expected1,expected2,expected3",
                "expected1[(s21)-[e19]->(s11)]," +
                        "expected2[(s28)-[e18]->(s29)],expected3[(s3)-[e3]->(s4)]"
        });
        /*
         * 1.[(Lispenard St) -> (Broadway & W 51 St)]
         */
        data.add(new String[]{
                "Succeeds_ISO_6_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE 2013-06-01T00:07:00.precedes(e.tx)" +
                                " AND 2013-05-12.succeeds(" +
                                "Interval(1970-01-01,a.val_from)) AND e.val.fromTo(" +
                                " 2013-06-01T00:35:00, 2013-06-01T00:40:00)"),
                "expected1",
                "expected1[(s28)-[e18]->(s29)]"
        });
        return data;
    }
}