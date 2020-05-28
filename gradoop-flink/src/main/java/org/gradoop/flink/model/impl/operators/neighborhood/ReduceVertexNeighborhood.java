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
package org.gradoop.flink.model.impl.operators.neighborhood;

import org.apache.flink.api.java.DataSet;
import org.gradoop.common.model.api.entities.Edge;
import org.gradoop.common.model.api.entities.GraphHead;
import org.gradoop.common.model.api.entities.Vertex;
import org.gradoop.flink.model.api.epgm.BaseGraph;
import org.gradoop.flink.model.api.epgm.BaseGraphCollection;
import org.gradoop.flink.model.api.functions.VertexAggregateFunction;
import org.gradoop.flink.model.impl.functions.epgm.Id;
import org.gradoop.flink.model.impl.operators.neighborhood.functions.NeighborVertexReduceFunction;
import org.gradoop.flink.model.impl.operators.neighborhood.functions.ShuffledVertexIdsFromEdge;
import org.gradoop.flink.model.impl.operators.neighborhood.functions.VertexIdsFromEdge;
import org.gradoop.flink.model.impl.operators.neighborhood.functions.VertexToFieldOne;
import org.gradoop.flink.model.impl.operators.neighborhood.functions.VertexToFieldZero;
import org.gradoop.flink.model.impl.operators.neighborhood.keyselector.IdInTuple;

/**
 * Reduce vertex neighborhood operator.
 *
 * @param <G>  The graph head type.
 * @param <V>  The vertex type.
 * @param <E>  The edge type.
 * @param <LG> The type of the graph.
 * @param <GC> The type of the graph collection.
 */
public class ReduceVertexNeighborhood<
  G extends GraphHead,
  V extends Vertex,
  E extends Edge,
  LG extends BaseGraph<G, V, E, LG, GC>,
  GC extends BaseGraphCollection<G, V, E, LG, GC>> extends VertexNeighborhood<LG> {

  /**
   * Valued constructor.
   *
   * @param function  vertex aggregate function
   * @param direction considered edge direction
   */
  public ReduceVertexNeighborhood(VertexAggregateFunction function, EdgeDirection direction) {
    super(function, direction);
  }

  @Override
  public LG execute(LG graph) {
    DataSet<V> vertices;
    switch (getDirection()) {
    case IN:
      // takes edges and gets the corresponding vertices and applies the aggregate function for
      // vertices of incoming edges
      vertices = graph.getEdges()
        // tuple of source id and target id
        .map(new VertexIdsFromEdge<>())
        .join(graph.getVertices())
        .where(1).equalTo(new Id<>())
        // replace the second id with the vertex
        .with(new VertexToFieldOne<>())
        .join(graph.getVertices())
        // replace the first id with the vertex
        .where(0).equalTo(new Id<>())
        .with(new VertexToFieldZero<>())
        // group by the target vertex
        .groupBy(new IdInTuple<>(1))
        // aggregate values
        .reduceGroup(new NeighborVertexReduceFunction<>((VertexAggregateFunction) getFunction()));
      break;
    case OUT:
      // takes edges and gets the corresponding vertices and applies the aggregate function for
      // vertices of outgoing edges
      vertices = graph.getEdges()
        // tuple of target id and source id
        .map(new VertexIdsFromEdge<>(true))
        .join(graph.getVertices())
        .where(1).equalTo(new Id<>())
        // replace the second id with the vertex
        .with(new VertexToFieldOne<>())
        .join(graph.getVertices())
        // replace the first id with the vertex
        .where(0).equalTo(new Id<>())
        .with(new VertexToFieldZero<>())
        // group by the target vertex
        .groupBy(new IdInTuple<>(1))
        // aggregate values
        .reduceGroup(new NeighborVertexReduceFunction<>((VertexAggregateFunction) getFunction()));
      break;
    case BOTH:
      // takes edges and gets the corresponding vertices and applies the aggregate function for
      // vertices of incoming and outgoing edges
      vertices = graph.getEdges()
        // maps source-target and target-source ids from the edge
        .flatMap(new ShuffledVertexIdsFromEdge<>())
        .join(graph.getVertices())
        .where(1).equalTo(new Id<>())
        // replace the second id with the vertex
        .with(new VertexToFieldOne<>())
        .join(graph.getVertices())
        .where(0).equalTo(new Id<>())
        // replace the first id with the vertex
        .with(new VertexToFieldZero<>())
        .groupBy(new IdInTuple<>(1))
        // aggregate values
        .reduceGroup(new NeighborVertexReduceFunction<>((VertexAggregateFunction) getFunction()));
      break;
    default:
      vertices = null;
    }
    return graph.getFactory().fromDataSets(graph.getGraphHead(), vertices, graph.getEdges());
  }
}
