package org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.booleans;

import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.CNF;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.QueryPredicate;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.util.QueryPredicateFactory;
import org.s1ck.gdl.model.predicates.Predicate;
import org.s1ck.gdl.model.predicates.booleans.And;
import org.s1ck.gdl.model.predicates.booleans.Not;
import org.s1ck.gdl.model.predicates.booleans.Or;
import org.s1ck.gdl.model.predicates.booleans.Xor;

import java.util.Objects;

/**
 * Wraps an {@link org.s1ck.gdl.model.predicates.booleans.Xor} predicate
 * Extension for temporal predicates
 */
public class XorPredicate extends org.gradoop.flink.model.impl.operators.matching.common.query.predicates.booleans.XorPredicate {
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
        super(xor);
        this.xor = xor;
    }

    @Override
    public CNF asCNF() {
        Predicate lhs = xor.getArguments()[0];
        Predicate rhs = xor.getArguments()[1];

        QueryPredicate wrapper = QueryPredicateFactory.createFrom(
                new Or(new And(lhs, new Not(rhs)), new And(new Not(lhs), rhs))
        );

        return wrapper.asCNF();
    }

    @Override
    public QueryPredicate getLhs() {
        return QueryPredicateFactory.createFrom(xor.getArguments()[0]);
    }

    @Override
    public QueryPredicate getRhs() {
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
