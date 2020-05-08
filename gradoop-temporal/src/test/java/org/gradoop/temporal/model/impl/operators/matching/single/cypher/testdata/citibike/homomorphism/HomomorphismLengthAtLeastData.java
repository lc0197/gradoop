package org.gradoop.temporal.model.impl.operators.matching.single.cypher.testdata.citibike.homomorphism;

import org.gradoop.temporal.model.impl.operators.matching.TemporalTestData;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.CBCypherTemporalPatternMatchingTest;

import java.util.ArrayList;
import java.util.Collection;

public class HomomorphismLengthAtLeastData implements TemporalTestData {
    @Override
    public Collection<String[]> getData() {
        ArrayList<String[]> data = new ArrayList<>();

        //1.[(Broadway & E14) -> (S 5 Pl & S 5 St)]
        //2.[(Stanton St & Chrystie) -> (Hancock St & Bedford Ave)]
        //3.[(Lispenard St & Broadway) -> (Broadway & W 51)]
        //4.[(W 37 St & 5 Ave) -> (Hicks St & Montague St)]
        //5.[(Hicks St & Montague St) -> (Hicks St & Montague St)]
        data.add(new String[]{
                "LengthAtLeast_HOM_1_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE val.lengthAtLeast(Minutes(30))"
                ),
                "expected1,expected2,expected3,expected4,expected5",
                "expected1[(s8)-[e6]->(s9)], expected2[(s12)-[e8]->(s13)], " +
                        "expected3[(s28)-[e18]->(s29)], expected4[(s7)-[e5]->(s2)], " +
                        "expected5[(s2)-[e2]->(s2)]"
        });

        //1.[(Murray St & West St) -> (Shevchenko Pl & E 7 St)]
        //2.[(Murray St & West St) -> (Greenwich St & W Houston St)]
        //3.[(DeKalb Ave & S Portland Ave) -> (Fulton St & Grand Ave)]
        //4.[(E33 St & 2 Ave) -> (Division St & Bowery)] (!!! other than in longerThan)
        data.add(new String[]{
                "LengthAtLeast_HOM_2_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE a.val.join(b.val).lengthAtLeast(Days(75))"
                ),
                "expected1,expected2,expected3,expected4",
                "expected1[(s24)-[e15]->(s25)], expected2[(s24)-[e16]->(s26)], expected3[(s19)-[e12]->(s20)], " +
                        "expected4[(s22)-[e14]->(s23)]"
        });

        //empty
        data.add(new String[]{
                "LengthAtLeast_HOM_3_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE e.tx.lengthAtLeast(Hours(1))"
                ),
                "",
                ""
        });

        //1.[(E15 St) -> (Washington Park)]
        data.add(new String[]{
                "LengthAtLeast_HOM_4_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE Interval(1970-01-01T00:00:00, 1970-01-01T00:05:00)" +
                                ".lengthAtLeast(val)"
                ),
                "expected1",
                "expected1[(s3)-[e3]->(s4)]"
        });

        //1.[(Broadway & E14) -> (S 5 Pl & S 5 St)]
        //2.[(Stanton St & Chrystie) -> (Hancock St & Bedford Ave)]
        //3.[(Lispenard St & Broadway) -> (Broadway & W 51)]
        //4.[(W 37 St & 5 Ave) -> (Hicks St & Montague St)]
        //5.[(Hicks St & Montague St) -> (Hicks St & Montague St)]
        data.add(new String[]{
                "LengthAtLeast_HOM_5_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE val.lengthAtLeast(Interval(" +
                                "1970-01-01, 1970-01-01T00:30:00))"
                ),
                "expected1,expected2,expected3,expected4,expected5",
                "expected1[(s8)-[e6]->(s9)], expected2[(s12)-[e8]->(s13)], " +
                        "expected3[(s28)-[e18]->(s29)], expected4[(s7)-[e5]->(s2)], " +
                        "expected5[(s2)-[e2]->(s2)]"
        });

        //empty
        data.add(new String[]{
                "LengthAtLeast_HOM_6_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE NOT b.tx.lengthAtLeast(val)"
                ),
                "",
                ""
        });

        //1.[(Henry St & Grand St) (Murray St & West St)]
        //2.[(Henry St & Grand St) (Shevchenko Pl)]
        //3.[(Henry St & Grand St) (Henry St & Grand St)
        data.add(new String[]{
                "LengthAtLeast_HOM_7_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a) (b) WHERE a.vertexId=18 AND b.val.lengthAtLeast(a.val)"
                ),
                "expected1,expected2,expected3",
                "expected1[(s18)(s24)], expected2[(s18)(s25)], expected3[(s18)(s18)]"
        });

        // 1.[(W 37 St & 5 Ave)->(Hicks St & Montague St)]
        // 2.[(Broadway & E14 St) -> (S 5 Pl & S 5 St)]
        // 3.[(Hicks St & Montague St) -> (Hicks St & Montague St)]
        data.add(new String[]{
                "LengthAtLeast_HOM_8_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE e.tx.lengthAtLeast(" +
                                "Interval(2013-06-01T00:01:47, 2013-06-01T00:35:35))"
                ),
                "expected1,expected2,expected3",
                "expected1[(s7)-[e5]->(s2)], expected2[(s8)-[e6]->(s9)], expected3[(s2)-[e2]->(s2)]"
        });

        // 1.[(W 37 St & 5 Ave)->(Hicks St & Montague St)]
        // 2.[(Broadway & E14 St) -> (S 5 Pl & S 5 St)]
        // 3.[(Hicks St & Montague St) -> (Hicks St & Montague St)]
        data.add(new String[]{
                "LengthAtLeast_HOM_9_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE e.tx.lengthAtLeast(" +
                                "Interval(MAX(a.val_from, 2013-06-01T00:01:47), 2013-06-01T00:35:35))"
                ),
                "expected1,expected2,expected3",
                "expected1[(s7)-[e5]->(s2)], expected2[(s8)-[e6]->(s9)], expected3[(s2)-[e2]->(s2)]"
        });

        return data;
    }
}
