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

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.graph.Vertex;

/**
 * Convert input to a Gelly Vertex with K key and VV value.
 *
 * @param <I>   Input type.
 * @param <K>   Key type of the output gelly vertex.
 * @param <VV>  Value type of the output gelly vertex.
 */
public interface ElementToGellyVertex<I, K, VV> extends MapFunction<I, Vertex<K, VV>> {
}
