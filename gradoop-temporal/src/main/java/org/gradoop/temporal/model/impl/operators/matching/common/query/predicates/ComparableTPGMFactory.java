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
package org.gradoop.temporal.model.impl.operators.matching.common.query.predicates;

import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.QueryComparable;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.QueryComparableFactory;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.comparables.ElementSelectorComparable;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.comparables.LiteralComparable;
import org.gradoop.flink.model.impl.operators.matching.common.query.predicates.comparables.PropertySelectorComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.DurationComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.MaxTimePointComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.MinTimePointComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.TimeConstantComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.TimeLiteralComparable;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.comparables.TimeSelectorComparable;
import org.gradoop.gdl.model.comparables.ComparableExpression;
import org.gradoop.gdl.model.comparables.ElementSelector;
import org.gradoop.gdl.model.comparables.Literal;
import org.gradoop.gdl.model.comparables.PropertySelector;
import org.gradoop.gdl.model.comparables.time.Duration;
import org.gradoop.gdl.model.comparables.time.MaxTimePoint;
import org.gradoop.gdl.model.comparables.time.MinTimePoint;
import org.gradoop.gdl.model.comparables.time.TimeConstant;
import org.gradoop.gdl.model.comparables.time.TimeLiteral;
import org.gradoop.gdl.model.comparables.time.TimeSelector;

/**
 * Factory for temporal comparable
 */
public class ComparableTPGMFactory extends QueryComparableFactory {

  /**
   * Holds the system time stamp when the query was issued
   */
  private TimeLiteral now;

  /**
   * Create a new instance
   *
   * @param now system time of the query
   */
  public ComparableTPGMFactory(TimeLiteral now) {
    this.now = now;
  }

  /**
   * Create a new instance
   */
  public ComparableTPGMFactory() {
    this(new TimeLiteral("now"));
  }

  @Override
  public QueryComparable createFrom(ComparableExpression expression) {
    Class cls = expression.getClass();
    if (cls == Literal.class) {
      return new LiteralComparable((Literal) expression);
    } else if (cls == PropertySelector.class) {
      return new PropertySelectorComparable((PropertySelector) expression);
    } else if (cls == ElementSelector.class) {
      return new ElementSelectorComparable((ElementSelector) expression);
    } else if (cls == TimeLiteral.class) {
      return new TimeLiteralComparable((TimeLiteral) expression);
    } else if (cls == TimeSelector.class) {
      return new TimeSelectorComparable((TimeSelector) expression);
    } else if (cls == MinTimePoint.class) {
      return new MinTimePointComparable((MinTimePoint) expression);
    } else if (cls == MaxTimePoint.class) {
      return new MaxTimePointComparable((MaxTimePoint) expression);
    } else if (cls == TimeConstant.class) {
      return new TimeConstantComparable((TimeConstant) expression);
    } else if (cls == Duration.class) {
      return new DurationComparable((Duration) expression, now);
    } else {
      throw new IllegalArgumentException(
        cls.getSimpleName() + " is not a temporal GDL ComparableExpression"
      );
    }
  }

  public TimeLiteral getNow() {
    return now;
  }

  public void setNow(TimeLiteral now) {
    this.now = now;
  }
}
