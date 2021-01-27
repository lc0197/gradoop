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
package org.gradoop.flink.model.impl.operators.matching.common.query.predicates.booleans;

import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.CNF;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.QueryComparableFactory;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.QueryPredicate;
import org.s1ck.gdl.model.predicates.booleans.And;

import java.util.Objects;

/**
 * Wraps an {@link org.s1ck.gdl.model.predicates.booleans.And} predicate
 */
public class AndPredicate extends QueryPredicate {
  /**
   * Holds the wrapped predicate
   */
  private final And and;

  /**
   * Optional factory for creating QueryComparables
   */
  private final QueryComparableFactory comparableFactory;

  /**
   * Returns a new AndPredicate
   * @param and the predicate
   */
  public AndPredicate(And and) {
    this(and, null);
  }

  /**
   * Returns a new AndPredicate
   * @param and the predicate
   * @param comparableFactory factory for query comparables
   */
  public AndPredicate(And and, QueryComparableFactory comparableFactory) {
    this.and = and;
    this.comparableFactory = comparableFactory;
  }

  /**
   * Converts the predicate into conjunctive normal form
   * @return predicate in CNF
   */
  public CNF asCNF() {
    return getLhs().asCNF()
      .and(getRhs().asCNF());
  }

  /**
   * Retruns the wrapped left hand side predicate
   * @return the left hand side
   */
  public QueryPredicate getLhs() {
    return QueryPredicate.createFrom(and.getArguments()[0], comparableFactory);
  }

  /**
   * Retruns the wrapped right hand side predicate
   * @return the right hand side
   */
  public QueryPredicate getRhs() {
    return QueryPredicate.createFrom(and.getArguments()[1], comparableFactory);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AndPredicate that = (AndPredicate) o;

    return Objects.equals(and, that.and);
  }

  @Override
  public int hashCode() {
    return and != null ? and.hashCode() : 0;
  }
}
