package org.gradoop.temporal.model.impl.operators.matching.single.cypher.testdata.citibike.isomorphism;

import org.gradoop.temporal.model.impl.operators.matching.TemporalTestData;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.CBCypherTemporalPatternMatchingTest;

import java.util.ArrayList;
import java.util.Collection;

public class IsomorphismLengthAtMostData implements TemporalTestData {
    @Override
    public Collection<String[]> getData() {
        ArrayList<String[]> data = new ArrayList<>();

        //1.[(Broadway & E14) -> (S 5 Pl & S 5 St)]
        //2.[(Stanton St & Chrystie) -> (Hancock St & Bedford Ave)]
        //3.[(Lispenard St & Broadway) -> (Broadway & W 51)]
        //4.[(W 37 St & 5 Ave) -> (Hicks St & Montague St)]
        data.add(new String[]{
                "LengthAtMost_ISO_1_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE Interval(1970-01-01T00:00:00, 1970-01-01T00:30:00)" +
                                ".lengthAtMost(val)"
                ),
                "expected1,expected2,expected3,expected4",
                "expected1[(s8)-[e6]->(s9)], expected2[(s12)-[e8]->(s13)], " +
                        "expected3[(s28)-[e18]->(s29)], expected4[(s7)-[e5]->(s2)]"
        });

        //1.[(Murray St & West St) -> (Shevchenko Pl & E 7 St)]
        //2.[(Murray St & West St) -> (Greenwich St & W Houston St)]
        //3.[(DeKalb Ave & S Portland Ave) -> (Fulton St & Grand Ave)]
        //4.[(E 33 St & 2 Ave) -> (Division St & Bowery)
        data.add(new String[]{
                "LengthAtMost_ISO_2_default_citibike",
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
        data.add(new String[]{
                "LengthAtMost_ISO_3_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE Interval(1970-01-01, 1970-01-01T01:00:00)" +
                                ".lengthAtMost(e.tx)"
                ),
                "",
                ""
        });

        //1.[(E15 St) -> (Washington Park)]
        data.add(new String[]{
                "LengthAtMost_ISO_4_default_citibike",
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
        data.add(new String[]{
                "LengthAtMost_ISO_5_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE " +
                                "Interval(1970-01-01, 1970-01-01T00:30:00).lengthAtMost(val)"
                ),
                "expected1,expected2,expected3,expected4",
                "expected1[(s8)-[e6]->(s9)], expected2[(s12)-[e8]->(s13)], " +
                        "expected3[(s28)-[e18]->(s29)], expected4[(s7)-[e5]->(s2)]"
        });

        //empty
        data.add(new String[]{
                "LengthAtMost_ISO_6_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE NOT val.lengthAtMost(b.tx)"
                ),
                "",
                ""
        });


        //1.[(Henry St & Grand St) (Murray St & West St)]
        //2.[(Henry St & Grand St) (Shevchenko Pl)]
        data.add(new String[]{
                "LengthAtMost_ISO_7_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a) (b) WHERE a.vertexId=18 AND a.val.lengthAtMost(b.val)"
                ),
                "expected1,expected2",
                "expected1[(s18)(s24)], expected2[(s18)(s25)]"
        });

        // 1.[(Greenwich St)<-(Murray St & West St)->(Shevchenko Pl)]
        data.add(new String[]{
                "LengthAtMost_ISO_8_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)<-[e1]-(b)-[e2]->(c) WHERE NOT e2.tx.lengthAtMost(e1.tx)"
                ),
                "expected1",
                "expected1[(s26)<-[e16]-(s24)-[e15]->(s25)]"
        });

        // 1.[(Murray St & West St) -> (Shevchenko Pl)
        data.add(new String[]{
                "LengthAtMost_ISO_9_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE NOT a.val.merge(b.val).lengthAtMost(Days(65))"
                ),
                "expected1",
                "expected1[(s24)-[e15]->(s25)]"
        });

        //1.[(E15 St) -> (Washington Park)]
        data.add(new String[]{
                "LengthAtMost_ISO_10_default_citibike",
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
