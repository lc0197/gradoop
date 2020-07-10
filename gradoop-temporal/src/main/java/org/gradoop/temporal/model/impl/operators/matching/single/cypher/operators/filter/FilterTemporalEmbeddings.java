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
package org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.filter;


import org.apache.flink.api.java.DataSet;
import org.gradoop.temporal.model.impl.operators.matching.common.query.predicates.TemporalCNF;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.PhysicalTPGMOperator;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.filter.functions.FilterTemporalEmbedding;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGMMetaData;


/**
 * Filters a set of TPGM Embeddings by the given predicates
 * The resulting embeddings have the same schema as the input embeddings
 */
public class FilterTemporalEmbeddings implements PhysicalTPGMOperator {
  /**
   * Candidate Embeddings
   */
  private final DataSet<EmbeddingTPGM> input;
  /**
   * Predicates in conjunctive normal form
   */
  private final TemporalCNF predicates;
  /**
   * Maps variable names to embedding entries;
   */
  private final EmbeddingTPGMMetaData metaData;

  /**
   * Operator name used for Flink operator description
   */
  private String name;

  /**
   * New embedding filter operator
   *
   * @param input      Candidate embeddings
   * @param predicates Predicates to used for filtering
   * @param metaData   Maps variable names to embedding entries
   */
  public FilterTemporalEmbeddings(DataSet<EmbeddingTPGM> input, TemporalCNF predicates,
                                  EmbeddingTPGMMetaData metaData) {
    this.input = input;
    this.predicates = predicates;
    this.metaData = metaData;
    this.setName("FilterEmbeddings");
  }

  @Override
  public DataSet<EmbeddingTPGM> evaluate() {
    return input
      .filter(new FilterTemporalEmbedding(predicates, metaData))
      .name(getName());
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String newName) {
    this.name = newName;
  }
}
