package org.gradoop.temporal.model.impl.operators.matching.single.cypher.testdata.citibike.homomorphism;

import org.gradoop.temporal.model.impl.operators.matching.TemporalTestData;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.CBCypherTemporalPatternMatchingTest;

import java.util.ArrayList;
import java.util.Collection;

public class HomomorphismBetweenData implements TemporalTestData {

    @Override
    public Collection<String[]> getData() {
        ArrayList<String[]> data = new ArrayList<>();
        /*
         * 1.[(W37 St & 5 Ave) -> (Hicks St)]
         * 2.[(Hicks St) -> (Hicks St)]
         * 3.[(Broadway & E 14) -[edgeId:6]-> (S 5 Pl & S 5 St)]
         * 4.[(Lispenard St) -> (Broadway & W 51 St)]
         */
        data.add(new String[]{
                "Between_HOM_1_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                "MATCH (a)-[e]->(b) WHERE e.val.between(2013-06-01T00:35:00, " +
                        "2013-06-01T00:40:00)"),
                "expected1,expected2,expected3,expected4",
                "expected1[(s7)-[e5]->(s2)], expected2[(s2)-[e2]->(s2)]," +
                        "expected3[(s8)-[e6]->(s9)], expected4[(s28)-[e18]->(s29)]"
        });
        /*
         * 1.[(Broadway & W 29) -[e19]-> (8 Ave & W 31) <-[e19]- (Broadway & W29)]
         * 2.[(Broadway & W 29) -[e19]-> (8 Ave & W 31) <-[e13]- (Broadway & W29)]
         * 3.[(Broadway & W 29) -[e13]-> (8 Ave & W 31) <-[e19]- (Broadway & W29)]
         * 4.[(Broadway & W 29) -[e13]-> (8 Ave & W 31) <-[e13]- (Broadway & W29)]
         */
        data.add(new String[]{
                "Between_HOM_2_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                "MATCH (a)-[e1]->(b)<-[e2]-(a) WHERE a.id=486 AND " +
                        "e1.val.between(e2.val_from, e2.val_to)"),
                "expected1,expected2,expected3,expected4",
                "expected1[(s21)-[e13]->(s11)<-[e13]-(s21)]," +
                        "expected2[(s21)-[e13]->(s11)<-[e19]-(s21)]," +
                        "expected3[(s21)-[e19]->(s11)<-[e13]-(s21)]," +
                        "expected4[(s21)-[e19]->(s11)<-[e19]-(s21)]"
        });
        // taken from isomorphism test, should yield same results
        /*
         * 1.[(E15 St & Irving Pl) -> (Washington Park)]
         * 2.[(Broadway & W29) -[edgeId:19]->(8 Ave & W31)]
         * 3.[(Lispenard St) -> (Broadway & W 51 St)]
         */
        data.add(new String[]{
                "Between_HOM_3_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                "MATCH (a)-[e]->(b) WHERE NOT e.val.between(2013-06-01T00:04:00, " +
                        "2013-06-01T00:08:00)"),
                "expected1,expected2,expected3",
                "expected1[(s3)-[e3]->(s4)], expected2[(s21)-[e19]->(s11)], " +
                        "expected3[(s28)-[e18]->(s29)]"
        });
        /*
         * 1.[(W37 St & 5 Ave) -> (Hicks St)]
         * 2.[(Hicks St) -> (Hicks St)]
         * 3.[(Stanton St) -> (Hancock St & Bedford Ave)]
         * 4.[(Broadway & E 14) -[edgeId:6]-> (S 5 Pl & S 5 St)]
         * 5.[(Lispenard St) -> (Broadway & W 51 St)]
         */
        data.add(new String[]{
                "From_HOM_4_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                "MATCH (a)-[e]->(b) WHERE Interval(2013-06-01T00:34:00,2013-06-01T00:35:00)" +
                        ".between(e.val_from, e.val_to)"),
                "expected1,expected2,expected3,expected4,expected5",
                "expected1[(s7)-[e5]->(s2)], expected2[(s2)-[e2]->(s2)]," +
                        "expected3[(s12)-[e8]->(s13)], expected4[(s8)-[e6]->(s9)]," +
                        "expected5[(s28)-[e18]->(s29)]"
        });
        /*
         * test to show difference to fromTo, cf. From_ISO_5_default_citibike
         * same as in isomorphism test
         * 1.[Broadway & W24) -[edgeId:0]-> (9 Ave & W18)
         * 2.[Broadway & W24) -[edgeId:1]-> (9 Ave & W18)
         */
        data.add(new String[]{
                "Between_HOM_5_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                "MATCH (a)-[e]->(b) WHERE a.id=444 AND " +
                        "e.val.between(2013-06-01T00:00:01,2013-06-01T00:00:08)"),
                "expected1,expected2",
                "expected1[(s0)-[e0]->(s1)], expected2[(s0)-[e1]->(s1)]"
        });
        /*
         * 1.[(Broadway & W24 St)]
         */
        data.add(new String[]{
                "Between_HOM_6_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a) WHERE a.tx.between(2013-05-10T23:50:00," +
                                "2013-05-10T23:59:59)"
                ),
                "expected1",
                "expected1[(s0)]"

        });
        /*
         * 1.[(W37 St & 5 Ave) -> (Hicks St)]
         * 2.[(Stanton St) -> (Hancock St & Bedford Ave)]
         */
        data.add(new String[]{
                "Between_HOM_7_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-[e]->(b) WHERE Interval(2013-06-01T00:34:00,2013-06-01T00:35:00)" +
                                ".between(e.val_from, e.val_to) AND " +
                                "a.tx.between(2013-07-25, 2013-07-29)"),
                "expected1,expected2",
                "expected1[(s7)-[e5]->(s2)], expected2[(s12)-[e8]->(s13)]"
        });
        //(empty)
        data.add(new String[]{
                "Between_HOM_8_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a)-->(b) WHERE NOT a.tx.between(b.val_from, b.tx_to)"
                ),
                "",
                ""
        });
        //1.[(Broadway & W 24 St) (Broadway & W 24 St)]
        //2.[(Broadway & W 24 St) (Little West St & 1 Pl)]
        data.add(new String[]{
                "FromTo_HOM_10_default_citibike",
                CBCypherTemporalPatternMatchingTest.defaultData,
                CBCypherTemporalPatternMatchingTest.noDefaultAsOf(
                        "MATCH (a) (b) WHERE tx.fromTo(1970-01-01,2013-05-11) " +
                                "AND a.id<=b.id"
                ),
                "expected1",
                "expected1[(s0) (s0)]"
        });
        return data;

    }
}