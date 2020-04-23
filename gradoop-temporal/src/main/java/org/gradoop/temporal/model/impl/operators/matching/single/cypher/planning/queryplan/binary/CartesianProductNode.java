package org.gradoop.temporal.model.impl.operators.matching.single.cypher.planning.queryplan.binary;

import org.apache.flink.api.common.operators.base.CrossOperatorBase;
import org.apache.flink.api.java.DataSet;
import org.gradoop.flink.model.impl.operators.matching.common.MatchStrategy;
import org.gradoop.flink.model.impl.operators.matching.single.cypher.planning.queryplan.JoinNode;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.join.CartesianProduct;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.planning.queryplan.BinaryNode;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.planning.queryplan.PlanNode;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGMMetaData;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartesianProductNode extends BinaryNode implements JoinNode {
    /**
     * Morphism type for vertices
     */
    private final MatchStrategy vertexStrategy;
    /**
     * Morphism type for edges
     */
    private final MatchStrategy edgeStrategy;
    /**
     * Cross hint for Flink optimizer
     */
    private final CrossOperatorBase.CrossHint crossHint;

    /**
     * Creates  a new node.
     *
     * @param leftChild left input plan node
     * @param rightChild right right input plan node
     * @param vertexStrategy morphism setting for vertices
     * @param edgeStrategy morphism setting for edges
     */
    public CartesianProductNode(PlanNode leftChild, PlanNode rightChild,
                                MatchStrategy vertexStrategy, MatchStrategy edgeStrategy) {
        this(leftChild, rightChild,
                vertexStrategy, edgeStrategy, CrossOperatorBase.CrossHint.OPTIMIZER_CHOOSES);
    }

    /**
     * Creates a new node.
     *
     * @param leftChild left input plan node
     * @param rightChild right input plan node
     * @param vertexStrategy morphism setting for vertices
     * @param edgeStrategy morphism setting for edges
     * @param crossHint Join hint for the Flink optimizer
     */
    public CartesianProductNode(PlanNode leftChild, PlanNode rightChild,
                                MatchStrategy vertexStrategy, MatchStrategy edgeStrategy,
                                CrossOperatorBase.CrossHint crossHint) {
        super(leftChild, rightChild);
        this.vertexStrategy = vertexStrategy;
        this.edgeStrategy = edgeStrategy;
        this.crossHint = crossHint;
    }

    @Override
    public DataSet<EmbeddingTPGM> execute() {
        CartesianProduct op = new CartesianProduct(getLeftChild().execute(), getRightChild().execute(),
                getRightChild().getEmbeddingMetaData().getEntryCount(),
                getDistinctVertexColumnsLeft(), getDistinctVertexColumnsRight(),
                getDistinctEdgeColumnsLeft(), getDistinctEdgeColumnsRight(), crossHint);
        op.setName(this.toString());
        return op.evaluate();
    }

    @Override
    protected EmbeddingTPGMMetaData computeEmbeddingMetaData() {
        EmbeddingTPGMMetaData leftInputMetaData = getLeftChild().getEmbeddingMetaData();
        EmbeddingTPGMMetaData rightInputMetaData = getRightChild().getEmbeddingMetaData();
        EmbeddingTPGMMetaData embeddingMetaData = new EmbeddingTPGMMetaData(leftInputMetaData);

        int entryCount = leftInputMetaData.getEntryCount();

        // append the non-join entry mappings from the right to the left side
        for (String var : rightInputMetaData.getVariables()) {
            embeddingMetaData.setEntryColumn(var, rightInputMetaData.getEntryType(var), entryCount++);
        }

        // append all property mappings from the right to the left side
        int propertyCount = leftInputMetaData.getPropertyCount();
        for (String var : rightInputMetaData.getVariables()) {
            for (String key : rightInputMetaData.getPropertyKeys(var)) {
                embeddingMetaData.setPropertyColumn(var, key, propertyCount++);
            }
        }

        //append all time mappings from the right side to the left side
        int timeCount = leftInputMetaData.getTimeCount();
        Set<String> rightTimeVariables = rightInputMetaData.getTimeDataMapping().keySet();
        // not possible to iterate over rightTimeVariables, sequence of variables is important
        for (String var : rightInputMetaData.getVariables()){
            if(!rightTimeVariables.contains(var)){
                continue;
            }
            embeddingMetaData.setTimeColumn(var, timeCount++);
        }
        return embeddingMetaData;
    }

    /**
     * According to the specified {@link CartesianProductNode#vertexStrategy}, the method returns
     * the columns that need to contain distinct entries in the left embedding.
     *
     * @return distinct vertex columns of the left embedding
     */
    private List<Integer> getDistinctVertexColumnsLeft() {
        return getDistinctVertexColumns(getLeftChild().getEmbeddingMetaData());
    }

    /**
     * According to the specified {@link CartesianProductNode#vertexStrategy}, the method returns
     * the columns that need to contain distinct entries in the right embedding.
     *
     * @return distinct vertex columns of the right embedding
     */
    private List<Integer> getDistinctVertexColumnsRight() {
        return getDistinctVertexColumns(getRightChild().getEmbeddingMetaData());
    }

    /**
     * According to the specified {@link CartesianProductNode#vertexStrategy} and the specified
     * {@link EmbeddingTPGMMetaData}, the method returns the columns that need to contain distinct
     * entries.
     *
     * @param metaData meta data for the embedding
     * @return distinct vertex columns
     */
    private List<Integer> getDistinctVertexColumns(final EmbeddingTPGMMetaData metaData) {
        return vertexStrategy == MatchStrategy.ISOMORPHISM ?
                metaData.getVertexVariables().stream()
                        .map(metaData::getEntryColumn)
                        .collect(Collectors.toList()) : Collections.emptyList();
    }

    /**
     * According to the specified {@link CartesianProductNode#edgeStrategy}, the method returns
     * the columns that need to contain distinct entries in the left embedding.
     *
     * @return distinct edge columns of the left embedding
     */
    private List<Integer> getDistinctEdgeColumnsLeft() {
        return getDistinctEdgeColumns(getLeftChild().getEmbeddingMetaData());
    }

    /**
     * According to the specified {@link CartesianProductNode#edgeStrategy}, the method returns
     * the columns that need to contain distinct entries in the right embedding.
     *
     * @return distinct edge columns of the right embedding
     */
    private List<Integer> getDistinctEdgeColumnsRight() {
        return getDistinctEdgeColumns(getRightChild().getEmbeddingMetaData());
    }

    /**
     * According to the specified {@link CartesianProductNode#edgeStrategy} and the specified
     * {@link EmbeddingTPGMMetaData}, the method returns the columns that need to contain distinct
     * entries.
     *
     * @param metaData meta data for the embedding
     * @return distinct edge columns
     */
    private List<Integer> getDistinctEdgeColumns(EmbeddingTPGMMetaData metaData) {
        return edgeStrategy == MatchStrategy.ISOMORPHISM ?
                metaData.getEdgeVariables().stream()
                        .map(metaData::getEntryColumn)
                        .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return String.format("CartesianProductNode{" +
                        "vertexMorphismType=%s, " +
                        "edgeMorphismType=%s}",
                vertexStrategy, edgeStrategy);
    }


}
