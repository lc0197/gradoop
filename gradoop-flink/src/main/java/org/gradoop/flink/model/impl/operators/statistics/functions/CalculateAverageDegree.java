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
package org.gradoop.flink.model.impl.operators.statistics.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.gradoop.common.model.impl.pojo.EPGMGraphHead;
import org.gradoop.flink.model.impl.operators.sampling.common.SamplingEvaluationConstants;

/**
 * Calculates the average degree, depending on the sum-value of the degree type
 * (degree, incomingDegree, outgoingDegree) stored as property in the graph head.
 */
public class CalculateAverageDegree implements MapFunction<EPGMGraphHead, EPGMGraphHead> {

  /**
   * The used average degree-type property key
   */
  private final String propertyKey;

  /**
   * Public constructor
   *
   * @param propertyKey The used average degree-type property key
   */
  public CalculateAverageDegree(String propertyKey) {
    this.propertyKey = propertyKey;
  }

  @Override
  public EPGMGraphHead map(EPGMGraphHead graphHead) {
    long numVertices = graphHead.getPropertyValue("vertexCount").getLong();
    long sumDegrees = graphHead.getPropertyValue(
      SamplingEvaluationConstants.PROPERTY_KEY_SUM_DEGREES).getLong();
    long average = (long) Math.ceil((double) sumDegrees / (double) numVertices);
    graphHead.setProperty(propertyKey, average);
    return graphHead;
  }
}
