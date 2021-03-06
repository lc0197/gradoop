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
package org.gradoop.flink.algorithms.gelly.functions;

import org.apache.flink.api.java.functions.FunctionAnnotation;
import org.apache.flink.types.NullValue;
import org.gradoop.common.model.api.entities.Edge;
import org.gradoop.common.model.impl.id.GradoopId;

/**
 * Maps Gradoop edge to a Gelly edge consisting of Gradoop source and target
 * identifier and {@link NullValue} as edge value.
 *
 * @param <E> Gradoop edge type.
 */
@FunctionAnnotation.ForwardedFields("sourceId->f0;targetId->f1")
public class EdgeToGellyEdgeWithNullValue<E extends Edge> implements EdgeToGellyEdge<E, NullValue> {
  /**
   * Reduce object instantiations
   */
  private final org.apache.flink.graph.Edge<GradoopId, NullValue> reuseEdge;

  /**
   * Constructor.
   */
  public EdgeToGellyEdgeWithNullValue() {
    reuseEdge = new org.apache.flink.graph.Edge<>();
    reuseEdge.setValue(NullValue.getInstance());
  }

  @Override
  public org.apache.flink.graph.Edge<GradoopId, NullValue> map(E edge) throws Exception {
    reuseEdge.setSource(edge.getSourceId());
    reuseEdge.setTarget(edge.getTargetId());
    return reuseEdge;
  }
}
