package org.gradoop.temporal.model.impl.operators.matching.single.cypher.operators.expand.pojos;

import org.apache.commons.lang3.ArrayUtils;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;

import java.io.Serializable;

/**
 * Represents an intermediate result for the expand operator.
 *
 * Based on an immutable input (EmbeddingTPGM) generated by previous operations.
 * This input is expanded by following edges.
 * Certain temporal information is contained to implement temporal expansion conditions.
 */
public class ExpandEmbeddingTPGM implements Serializable {

    /**
     * The base for the expansion. Created by previous operations during query processing.
     */
    EmbeddingTPGM base;
    /**
     * the extension path (without the last node).
     */
    GradoopId[] path;
    /**
     * last node of the extension path. Stored separately in order to use it for joining.
     */
    GradoopId endVertex;
    /**
     * temporal information about the last edge in the form [tx_from, tx_to, valid_from, valid_to]
     */
    Long[] lastEdgeTimeData;
    /**
     * temporal information about the last node in the form [tx_from, tx_to, valid_from, valid_to]
     */
    Long[] endVertexTimeData;
    /**
     * maximum tx_from for all elements on the path (needed to determine the path's "lifetime")
     */
    Long maxTxFrom;
    /**
     * minimum tx_to for all elements on the path (needed to determine the path's "lifetime")
     */
    Long minTxTo;
    /**
     * maximum valid_from for all elements on the path (needed to determine the path's "lifetime")
     */
    Long maxValidFrom;
    /**
     * minimum valid_to for all elements on the path (needed to determine the path's "lifetime")
     */
    Long minValidTo;



    /**
     * Creates a new expand intermediate result
     *
     * @param base the base embedding
     * @param path the path (excluding the last vertex)
     * @param lastEdgeTimeData time data of the last edge in the form
     *                         [tx_from, tx_to, valid_from, valid_to]
     * @param endVertexTimeData time data of the last vertex in the form
     *                       [tx_from, tx_to, valid_from, valid_to]
     * @param maxTxFrom maximum tx_from value of the path
     * @param minTxTo minimum tx_to value of the path
     * @param maxValidFrom maximum valid_from value of the path
     * @param minValidTo minimum valid_to value of the path
     */
    public ExpandEmbeddingTPGM(EmbeddingTPGM base, GradoopId[] path,
                               Long[] lastEdgeTimeData, Long[] endVertexTimeData, Long maxTxFrom,
                               Long minTxTo, Long maxValidFrom, Long minValidTo){
        this.base = base;
        this.path = ArrayUtils.subarray(path, 0, path.length - 1);
        this.endVertex = path[path.length-1];
        this.lastEdgeTimeData = lastEdgeTimeData;
        this.endVertexTimeData = endVertexTimeData;
        this.maxTxFrom = maxTxFrom;
        this.minTxTo = minTxTo;
        this.maxValidFrom = maxValidFrom;
        this.minValidTo = minValidTo;
    }

    /**
     * returns the base embedding
     * @return base embedding
     */
    public EmbeddingTPGM getBase() {
        return base;
    }

    /**
     * returns the path (excluding the last vertex)
     * @return path (excluding the last vertex)
     */
    public GradoopId[] getPath() {
        return path;
    }

    /**
     * returns the last vertex on the path
     * @return last vertex on the path
     */
    public GradoopId getEndVertex() {
        return endVertex;
    }

    /**
     * returns the last edge's time data
     * @return last edge's time data
     */
    public Long[] getLastEdgeTimeData() {
        return lastEdgeTimeData;
    }

    /**
     * returns last node's time data
     * @return last node's time data
     */
    public Long[] getEndVertexTimeData() {
        return endVertexTimeData;
    }

    /**
     * returns maximum tx_from value on the path
     * @return maximum tx_from value on the path
     */
    public Long getMaxTxFrom() {
        return maxTxFrom;
    }

    /**
     * returns minimum tx_to value on the path
     * @return minimum tx_to value on the path
     */
    public Long getMinTxTo() {
        return minTxTo;
    }

    /**
     * returns maximum valid_from value on the path
     * @return maximum valid_from value on the path
     */
    public Long getMaxValidFrom() {
        return maxValidFrom;
    }

    /**
     * returns minimum valid_to value on the path
     * @return minimum valid_to value on the path
     */
    public Long getMinValidTo() {
        return minValidTo;
    }

    /**
     * constructs a new intermediate result by expanding the current one by another edge
     * @param edge the edge used to expand the current intermediate result
     * @return new embedding representing the expanded intermediate result
     */
    public ExpandEmbeddingTPGM grow(TemporalEdgeWithTiePoint edge){
        GradoopId[] newPath = ArrayUtils.addAll(path, endVertex, edge.getEdge(), edge.getTarget());
        Long newMxTxF = (maxTxFrom!=null && maxTxFrom < edge.getMaxTxFrom())?
                edge.getMaxTxFrom() : maxTxFrom;
        Long newMnTxT = (minTxTo!=null && minTxTo > edge.getMinTxTo())?
                edge.getMinTxTo() : minTxTo;
        Long newMxValF = (maxValidFrom!=null && maxValidFrom < edge.getMaxValidFrom())?
                edge.getMaxValidFrom() : maxValidFrom;
        Long newMnValT = (minValidTo!=null && minValidTo > edge.getMinTxTo())?
                edge.getMinValidTo() : minValidTo;

        return new ExpandEmbeddingTPGM(base, newPath, edge.getEdgeTimeData(),
                edge.getTargetTimeData(), newMxTxF, newMnTxT, newMxValF, newMnValT);
    }

    /**
     * Size of the path
     * @return path size
     */
    public int pathSize(){
        return path.length;
    }

    /**
     * Builds an EmbeddingTPGM from the intermediate result by appending the IDs on the path and
     * the time data of the last vertex on the path
     * @return embedding representation of the intermediate result
     */
    public EmbeddingTPGM toEmbeddingTPGM(){
        EmbeddingTPGM embedding = base;
        embedding.add(path);
        embedding.add(endVertex);
        embedding.addTimeData(endVertexTimeData[0], endVertexTimeData[1], endVertexTimeData[2], endVertexTimeData[3]);
        return embedding;
    }

}