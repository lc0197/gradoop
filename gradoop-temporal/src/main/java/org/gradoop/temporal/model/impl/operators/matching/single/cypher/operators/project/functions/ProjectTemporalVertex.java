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
package org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.project.functions;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGMFactory;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;

import java.util.List;

/**
 * Projects a TPGM Vertex by a set of properties.
 * <p>
 * {@code TemporalVertex -> EmbeddingTPGM(GraphElementEmbedding(Vertex))}
 */
public class ProjectTemporalVertex extends RichMapFunction<TemporalVertex, EmbeddingTPGM> {
  /**
   * Names of the properties that will be kept in the projection
   */
  private final List<String> propertyKeys;

  /**
   * Creates a new vertex projection function
   *
   * @param propertyKeys List of propertyKeys that will be kept in the projection
   */
  public ProjectTemporalVertex(List<String> propertyKeys) {
    this.propertyKeys = propertyKeys;
  }

  @Override
  public EmbeddingTPGM map(TemporalVertex vertex) {
    return EmbeddingTPGMFactory.fromVertex(vertex, propertyKeys);
  }
}
