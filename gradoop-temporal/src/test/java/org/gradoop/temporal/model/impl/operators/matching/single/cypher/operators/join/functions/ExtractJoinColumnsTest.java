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
package org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.join.functions;

import org.apache.commons.lang.ArrayUtils;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;


/**
 * Practically identical to ExtractJoinColumnsTest in flink, but adjusted to temporal data
 */
public class ExtractJoinColumnsTest {
  @Test
  public void testSingleColumn() throws Exception {
    GradoopId v0 = GradoopId.get();
    GradoopId v1 = GradoopId.get();

    EmbeddingTPGM embedding = new EmbeddingTPGM();
    embedding.add(v0);
    embedding.add(v1);

    ExtractJoinColumns udf = new ExtractJoinColumns(Collections.singletonList(0));

    Assert.assertEquals(ArrayUtils.toString(v0.toByteArray()), udf.getKey(embedding));
  }

  @Test
  public void testMultiColumn() throws Exception {
    GradoopId v0 = GradoopId.get();
    GradoopId v1 = GradoopId.get();

    EmbeddingTPGM embedding = new EmbeddingTPGM();
    embedding.add(v0);
    embedding.add(v1);

    ExtractJoinColumns udf = new ExtractJoinColumns(Arrays.asList(0, 1));

    Assert.assertEquals(
      ArrayUtils.toString(v0.toByteArray()) + ArrayUtils.toString(v1.toByteArray()),
      udf.getKey(embedding)
    );
  }

  @Test
  public void testMultiColumnReverse() throws Exception {
    GradoopId v0 = GradoopId.get();
    GradoopId v1 = GradoopId.get();

    EmbeddingTPGM embedding = new EmbeddingTPGM();
    embedding.add(v0);
    embedding.add(v1);

    ExtractJoinColumns udf1 = new ExtractJoinColumns(Arrays.asList(0, 1));
    ExtractJoinColumns udf2 = new ExtractJoinColumns(Arrays.asList(1, 0));

    Assert.assertNotEquals(udf1.getKey(embedding), udf2.getKey(embedding));
  }
}
