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
package org.gradoop.flink.algorithms.fsm.dimspan.functions.preprocessing;

import com.google.common.collect.Sets;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.gradoop.flink.algorithms.fsm.dimspan.tuples.LabeledGraphIntString;
import org.gradoop.flink.model.impl.tuples.WithCount;

import java.util.Set;

/**
 * {@code graph -> (edgeLabel,1L),..}
 */
public class ReportEdgeLabels implements FlatMapFunction<LabeledGraphIntString, WithCount<String>> {

  /**
   * Reuse tuple to avoid instantiations.
   */
  private WithCount<String> reuseTuple = new WithCount<>(null, 1);

  @Override
  public void flatMap(LabeledGraphIntString graph,
    Collector<WithCount<String>> out) throws Exception {

    Set<String> edgeLabels = Sets.newHashSet(graph.getEdgeLabels());

    for (String label : edgeLabels) {
      reuseTuple.setObject(label);
      out.collect(reuseTuple);
    }
  }
}
