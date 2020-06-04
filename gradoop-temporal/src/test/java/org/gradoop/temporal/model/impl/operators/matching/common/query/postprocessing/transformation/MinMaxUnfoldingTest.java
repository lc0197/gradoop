package org.gradoop.temporal.model.impl.operators.matching.common.query.postprocessing.transformation;

import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.TemporalCNF;
import org.junit.Test;
import org.s1ck.gdl.model.comparables.time.MaxTimePoint;
import org.s1ck.gdl.model.comparables.time.MinTimePoint;
import org.s1ck.gdl.model.comparables.time.TimeLiteral;
import org.s1ck.gdl.model.comparables.time.TimeSelector;
import org.s1ck.gdl.model.predicates.expressions.Comparison;
import org.s1ck.gdl.utils.Comparator;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.s1ck.gdl.model.comparables.time.TimeSelector.TimeField.*;
import static org.s1ck.gdl.utils.Comparator.*;

public class MinMaxUnfoldingTest {

    TimeSelector ts1 = new TimeSelector("a", TX_FROM);
    TimeSelector ts2 = new TimeSelector("a", TX_TO);
    TimeSelector ts3 = new TimeSelector("a", VAL_FROM);
    TimeLiteral l1 = new TimeLiteral("1970-01-01");
    TimeLiteral l2 = new TimeLiteral("2020-05-24");

    MinTimePoint min1 = new MinTimePoint(ts1, ts2, ts3);
    MinTimePoint min2 = new MinTimePoint(ts3, l1);

    MaxTimePoint max1 = new MaxTimePoint(l2, ts1, ts2);
    MaxTimePoint max2 = new MaxTimePoint(ts2, l1);

    MinMaxUnfolding unfolder = new MinMaxUnfolding();

    @Test
    public void minMaxUnfoldingTest(){
        Comparison c1 = new Comparison(min1, Comparator.LT, l1);
        TemporalCNF cnf1 = Util.cnfFromLists(
                Arrays.asList(c1)
        );
        TemporalCNF expected1 = Util.cnfFromLists(
                Arrays.asList(
                        new Comparison(min1.getArgs().get(0), Comparator.LT, l1),
                        new Comparison(min1.getArgs().get(1), Comparator.LT, l1),
                        new Comparison(min1.getArgs().get(2), Comparator.LT, l1)
                )
        );
        assertEquals(unfolder.transformCNF(cnf1), expected1);


        Comparison c2 = new Comparison(max1, LTE, l1);
        TemporalCNF cnf2 = Util.cnfFromLists(
                Arrays.asList(c2)
        );
        TemporalCNF expected2 = Util.cnfFromLists(
                Arrays.asList(new Comparison(max1.getArgs().get(0), LTE, l1)),
                Arrays.asList(new Comparison(max1.getArgs().get(1), LTE, l1)),
                Arrays.asList(new Comparison(max1.getArgs().get(2), LTE, l1))

        );
        assertEquals(unfolder.transformCNF(cnf2), expected2);


        Comparison c3 = new Comparison(max1, Comparator.EQ, l1);
        TemporalCNF cnf3 = Util.cnfFromLists(
                Arrays.asList(c3)
        );
        TemporalCNF expected3 = Util.cnfFromLists(
                Arrays.asList(new Comparison(max1, Comparator.EQ, l1)));
        assertEquals(unfolder.transformCNF(cnf3), expected3);


        Comparison c4 = new Comparison(l1, LTE, min1);
        TemporalCNF cnf4 = Util.cnfFromLists(
                Arrays.asList(c4)
        );
        TemporalCNF expected4 = Util.cnfFromLists(
                Arrays.asList(new Comparison(l1, LTE, min1.getArgs().get(0))),
                Arrays.asList(new Comparison(l1, LTE, min1.getArgs().get(1))),
                Arrays.asList(new Comparison(l1, LTE, min1.getArgs().get(2)))
        );
        assertEquals(unfolder.transformCNF(cnf4), expected4);


        Comparison c5 = new Comparison(l1, Comparator.LT, max1);
        TemporalCNF cnf5 = Util.cnfFromLists(
                Arrays.asList(c5)
        );
        TemporalCNF expected5 = Util.cnfFromLists(
                Arrays.asList(
                        new Comparison(l1, Comparator.LT, max1.getArgs().get(0)),
                        new Comparison(l1, Comparator.LT, max1.getArgs().get(1)),
                        new Comparison(l1, Comparator.LT, max1.getArgs().get(2))
                )
        );
        assertEquals(unfolder.transformCNF(cnf5), expected5);


        Comparison c6 = new Comparison(l1, NEQ, min1);
        TemporalCNF cnf6 = Util.cnfFromLists(
                Arrays.asList(c6)
        );
        TemporalCNF expected6 = Util.cnfFromLists(
                Arrays.asList(new Comparison(l1, NEQ, min1)));
        assertEquals(unfolder.transformCNF(cnf6), expected6);


        // MIN < MIN is only unfolded once, rhs stays untouched (to ensure a CNF result)
        Comparison c7 = new Comparison(min2, Comparator.LT, min2);
        TemporalCNF cnf7 = Util.cnfFromLists(
                Arrays.asList(c7)
        );
        TemporalCNF expected7 = Util.cnfFromLists(
                Arrays.asList(
                        new Comparison(min2.getArgs().get(0), Comparator.LT, min2),
                        new Comparison(min2.getArgs().get(1), Comparator.LT, min2)
        ));
        assertEquals(unfolder.transformCNF(cnf7), expected7);

        // MIN < MAX
        Comparison c8 = new Comparison(min2, LTE, max2);
        TemporalCNF cnf8 = Util.cnfFromLists(
                Arrays.asList(c8)
        );
        TemporalCNF expected8 = Util.cnfFromLists(
                Arrays.asList(
                        new Comparison(min2.getArgs().get(0), LTE, max2.getArgs().get(0)),
                        new Comparison(min2.getArgs().get(0), LTE, max2.getArgs().get(1)),
                        new Comparison(min2.getArgs().get(1), LTE, max2.getArgs().get(0)),
                        new Comparison(min2.getArgs().get(1), LTE, max2.getArgs().get(1))
                ));
        assertEquals(unfolder.transformCNF(cnf8), expected8);

        // MAX < MIN
        Comparison c9 = new Comparison(max2, LTE, min2);
        TemporalCNF cnf9 = Util.cnfFromLists(
                Arrays.asList(c9)
        );
        TemporalCNF expected9 = Util.cnfFromLists(
                Arrays.asList(new Comparison(max2.getArgs().get(0), LTE, min2.getArgs().get(0))),
                Arrays.asList(new Comparison(max2.getArgs().get(0), LTE, min2.getArgs().get(1))),
                Arrays.asList(new Comparison(max2.getArgs().get(1), LTE, min2.getArgs().get(0))),
                Arrays.asList(new Comparison(max2.getArgs().get(1), LTE, min2.getArgs().get(1)))
        );
        assertEquals(unfolder.transformCNF(cnf9), expected9);

        // MAX < MAX
        Comparison c10 = new Comparison(max2, LTE, max2);
        TemporalCNF cnf10 = Util.cnfFromLists(
                Arrays.asList(c10)
        );
        TemporalCNF expected10 = Util.cnfFromLists(
                Arrays.asList(new Comparison(max2.getArgs().get(0), LTE, max2.getArgs().get(0)),
                                new Comparison(max2.getArgs().get(0), LTE, max2.getArgs().get(1))),
                Arrays.asList(new Comparison(max2.getArgs().get(1), LTE, max2.getArgs().get(0)),
                                new Comparison(max2.getArgs().get(1), LTE, max2.getArgs().get(1)))
        );
        assertEquals(unfolder.transformCNF(cnf10), expected10);

        Comparison c11 = new Comparison(max2, NEQ, min2);
        TemporalCNF cnf11 = Util.cnfFromLists(
                Arrays.asList(c11)
        );
        TemporalCNF expected11 = Util.cnfFromLists(
                Arrays.asList(c11)
        );
        assertEquals(unfolder.transformCNF(cnf11), expected11);

        // more complex
        TemporalCNF noMinMax = Util.cnfFromLists(
                Arrays.asList(
                        new Comparison(l1, LTE, l2),
                        new Comparison(l1, GTE, l2)
                ),
                Arrays.asList(
                        new Comparison(ts1, NEQ, ts2)
                )
        );
        TemporalCNF complex = cnf1.and(cnf2)
                .and(cnf3).and(cnf4).and(noMinMax)
                .and(cnf5).and(cnf6).and(cnf7).and(cnf8)
                .and(cnf9).and(cnf10).and(cnf11);
        TemporalCNF expectedComplex = expected1.and(expected2).and(expected3)
                .and(expected4).and(noMinMax).and(expected5).and(expected6).and(expected7)
                .and(expected8).and(expected9).and(expected10).and(expected11);
        assertEquals(unfolder.transformCNF(complex), expectedComplex);

        TemporalCNF empty = new TemporalCNF();
        assertEquals(unfolder.transformCNF(empty), empty);
    }
}
