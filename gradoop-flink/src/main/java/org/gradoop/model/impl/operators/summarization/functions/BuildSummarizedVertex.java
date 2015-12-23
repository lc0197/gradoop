/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop. If not, see <http://www.gnu.org/licenses/>.
 */

package org.gradoop.model.impl.operators.summarization.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.graph.Vertex;
import org.gradoop.model.api.EPGMVertex;
import org.gradoop.model.api.EPGMVertexFactory;
import org.gradoop.model.impl.operators.summarization.functions.aggregation.PropertyValueAggregator;
import org.gradoop.model.impl.operators.summarization.tuples.VertexGroupItem;

import java.util.List;

/**
 * Creates a new vertex representing a vertex group. The vertex stores the
 * group label, the group property value and the number of vertices in the
 * group.
 *
 * @param <V> EPGM vertex type
 */
public class BuildSummarizedVertex<V extends EPGMVertex>
  extends BuildBase
  implements MapFunction<VertexGroupItem, V>, ResultTypeQueryable<V> {

  /**
   * Vertex vertexFactory.
   */
  private final EPGMVertexFactory<V> vertexFactory;

  /**
   * Creates map function.
   *
   * @param groupPropertyKeys vertex property key for grouping
   * @param useLabel          true, if vertex label shall be considered
   * @param valueAggregator   aggregate function for vertex values
   * @param vertexFactory     vertex factory
   */
  public BuildSummarizedVertex(List<String> groupPropertyKeys,
    boolean useLabel,
    PropertyValueAggregator valueAggregator,
    EPGMVertexFactory<V> vertexFactory) {
    super(groupPropertyKeys, useLabel, valueAggregator);
    this.vertexFactory = vertexFactory;
  }

  /**
   * Creates a {@link EPGMVertex} object from the given {@link
   * VertexGroupItem} and returns a new {@link Vertex}.
   *
   * @param groupItem vertex group item
   * @return vertex including new vertex data
   * @throws Exception
   */
  @Override
  public V map(VertexGroupItem groupItem) throws
    Exception {
    V sumVertex = vertexFactory.initVertex(groupItem.getGroupRepresentative());

    setLabel(sumVertex, groupItem.getGroupLabel());
    setGroupProperties(sumVertex, groupItem.getGroupPropertyValues());
    setAggregate(sumVertex, groupItem.getGroupAggregate());

    return sumVertex;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public TypeInformation<V> getProducedType() {
    return (TypeInformation<V>)
      TypeExtractor.createTypeInfo(vertexFactory.getType());
  }
}
