package org.gradoop.temporal.model.impl.operators.matching.single.cypher.functions;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.gradoop.common.model.api.entities.Edge;
import org.gradoop.common.model.api.entities.EdgeFactory;
import org.gradoop.common.model.api.entities.Element;
import org.gradoop.common.model.api.entities.GraphHead;
import org.gradoop.common.model.api.entities.GraphHeadFactory;
import org.gradoop.common.model.api.entities.Vertex;
import org.gradoop.common.model.api.entities.VertexFactory;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.properties.PropertyValue;
import org.gradoop.flink.model.impl.operators.matching.single.PatternMatching;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGMMetaData;
import org.gradoop.flink.model.impl.operators.matching.single.cypher.utils.ExpandDirection;
import org.gradoop.temporal.model.impl.pojo.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extracts elements from an {@link EmbeddingTPGM}.
 * Is almost identical to
 * {@link org.gradoop.flink.model.impl.operators.matching.single.cypher.functions.ElementsFromEmbedding}
 * extends it for TPGM embeddings
 *
 * @param <G> The graph head type.
 * @param <V> The vertex type.
 * @param <E> The edge type.
 */
public class ElementsFromEmbeddingTPGM<
        G extends TemporalGraphHead,
        V extends TemporalVertex,
        E extends TemporalEdge> implements FlatMapFunction<EmbeddingTPGM, Element> {
    /**
     * Constructs temporal graph heads
     */
    private final GraphHeadFactory<G> graphHeadFactory;
    /**
     * Constructs temporal vertices
     */
    private final VertexFactory<V> vertexFactory;
    /**
     * Constructs temporal edges
     */
    private final EdgeFactory<E> edgeFactory;
    /**
     * Describes the embedding content
     */
    private final EmbeddingTPGMMetaData metaData;
    /**
     * Source vertex variables by edge id
     */
    private final Map<String, Pair<String, String>> sourceTargetVariables;
    /**
     * Stores the mapping between query variable and element id. The mapping is added as a property
     * to the graph head representing an embedding.
     */
    private final Map<PropertyValue, PropertyValue> variableMapping;
    /**
     * Stores the identifiers that have already been processed.
     */
    private final Set<GradoopId> processedIds;
    /**
     * Stores the mapping between return pattern variables and its labels
     */
    private final Map<String, String> labelMapping;

    /**
     * Constructor.
     * @param graphHeadFactory      temporal graph head factory
     * @param vertexFactory         temporal vertex factory
     * @param edgeFactory           temporal edge factory
     * @param embeddingMetaData     meta data for the TPGM embedding
     * @param sourceTargetVariables source and target vertex variables by edge variable
     */
    public ElementsFromEmbeddingTPGM(GraphHeadFactory<G> graphHeadFactory,
                                     VertexFactory<V> vertexFactory, EdgeFactory<E> edgeFactory,
                                     EmbeddingTPGMMetaData embeddingMetaData,
                                     Map<String, Pair<String, String>> sourceTargetVariables) {
        this(graphHeadFactory, vertexFactory, edgeFactory, embeddingMetaData,
                sourceTargetVariables, Maps.newHashMapWithExpectedSize(0));
    }
    /**
     * Constructor.
     * @param graphHeadFactory      temporal graph head factory
     * @param vertexFactory         temporal vertex factory
     * @param edgeFactory           temporal edge factory
     * @param embeddingMetaData     meta data for the TPGM embedding
     * @param sourceTargetVariables source and target vertex variables by edge variable
     * @param labelMapping          mapping between newElementVariables and its labels
     */
    public ElementsFromEmbeddingTPGM(GraphHeadFactory<G> graphHeadFactory,
                                     VertexFactory<V> vertexFactory,
                                     EdgeFactory<E> edgeFactory,
                                     EmbeddingTPGMMetaData embeddingMetaData,
                                     Map<String, Pair<String, String>> sourceTargetVariables,
                                     Map<String, String> labelMapping) {
        this.graphHeadFactory = graphHeadFactory;
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        this.metaData = embeddingMetaData;
        this.sourceTargetVariables = sourceTargetVariables;
        this.labelMapping = labelMapping;
        this.variableMapping = new HashMap<>(embeddingMetaData.getEntryCount());
        this.processedIds = new HashSet<>(embeddingMetaData.getEntryCount());
    }

    @Override
    public void flatMap(EmbeddingTPGM embedding, Collector<Element> out) throws Exception {
        // clear for each embedding
        processedIds.clear();

        // create graph head for this embedding
        TemporalGraphHead graphHead = graphHeadFactory.createGraphHead();

        // used to determine global time
        Long[] globalTime = new Long[]{TemporalElement.DEFAULT_TIME_FROM,
        TemporalElement.DEFAULT_TIME_TO, TemporalElement.DEFAULT_TIME_FROM, TemporalElement.DEFAULT_TIME_TO};

        // vertices
        for (String vertexVariable : metaData.getVertexVariables()) {
            GradoopId id = embedding.getId(metaData.getEntryColumn(vertexVariable));
            Long[] tempData = embedding.getTimes(metaData.getTimeColumn(vertexVariable));
            globalTime = updateGlobalTime(globalTime, tempData);

            if (labelMapping.containsKey(vertexVariable)) {
                String label = labelMapping.get(vertexVariable);
                initVertexWithData(out, graphHead, id, label, tempData);
            } else {
                initVertex(out, graphHead, id, tempData);
            }
            variableMapping.put(PropertyValue.create(vertexVariable), PropertyValue.create(id));
        }

        // edges
        GradoopId edgeId;
        GradoopId sourceId;
        GradoopId targetId;
        Long[] timeData;
        for (String edgeVariable : metaData.getEdgeVariables()) {
            edgeId = embedding.getId(metaData.getEntryColumn(edgeVariable));
            sourceId = embedding.getId(
                    metaData.getEntryColumn(sourceTargetVariables.get(edgeVariable).getLeft()));
            targetId = embedding.getId(
                    metaData.getEntryColumn(sourceTargetVariables.get(edgeVariable).getRight()));

            timeData = embedding.getTimes(metaData.getTimeColumn(edgeVariable));
            globalTime = updateGlobalTime(globalTime, timeData);

            if (labelMapping.containsKey(edgeVariable)) {
                String label = labelMapping.get(edgeVariable);
                initEdgeWithData(out, graphHead, edgeId, sourceId, targetId, label, timeData);
            } else {
                initEdge(out, graphHead, edgeId, sourceId, targetId, timeData);
            }
            variableMapping.put(PropertyValue.create(edgeVariable), PropertyValue.create(edgeId));
        }

        // paths
        Long[] defaultTime = new Long[]{TemporalElement.DEFAULT_TIME_FROM, TemporalElement.DEFAULT_TIME_TO,
                TemporalElement.DEFAULT_TIME_FROM, TemporalElement.DEFAULT_TIME_TO};

        for (String pathVariable : metaData.getPathVariables()) {
            ExpandDirection direction = metaData.getDirection(pathVariable);
            List<GradoopId> path = embedding.getIdList(metaData.getEntryColumn(pathVariable));
            List<PropertyValue> mappingValue = new ArrayList<>(path.size());
            for (int i = 0; i < path.size(); i += 2) {
                edgeId = path.get(i);
                mappingValue.add(PropertyValue.create(edgeId));

                if (direction == ExpandDirection.OUT) {
                    sourceId = i > 0 ?
                            path.get(i - 1) :
                            embedding.getId(
                                    metaData.getEntryColumn(sourceTargetVariables.get(pathVariable).getLeft()));


                    targetId = i < path.size() - 1 ?
                            path.get(i + 1) :
                            embedding.getId(
                                    metaData.getEntryColumn(sourceTargetVariables.get(pathVariable).getRight()));

                    if (i + 2 < path.size()) {
                        mappingValue.add(PropertyValue.create(targetId));
                    }
                } else {
                    sourceId = i < path.size() - 1 ?
                            path.get(i + 1) :
                            embedding.getId(
                                    metaData.getEntryColumn(sourceTargetVariables.get(pathVariable).getLeft()));

                    targetId = i > 0 ?
                            path.get(i - 1) :
                            embedding.getId(metaData.getEntryColumn(
                                    sourceTargetVariables.get(pathVariable).getRight()));

                    if (i > 0) {
                        mappingValue.add(PropertyValue.create(sourceId));
                    }
                }

                initVertex(out, graphHead, sourceId, defaultTime);
                initVertex(out, graphHead, targetId, defaultTime);
                initEdge(out, graphHead, edgeId, sourceId, targetId, defaultTime);
            }
            variableMapping.put(PropertyValue.create(pathVariable), PropertyValue.create(mappingValue));
        }

        if(globalTime[0]>globalTime[1]){
            globalTime[0]= Long.MIN_VALUE;
            globalTime[1] = Long.MIN_VALUE;
        }
        if(globalTime[2]>globalTime[3]){
            globalTime[2]= Long.MIN_VALUE;
            globalTime[3] = Long.MIN_VALUE;
        }

        graphHead.setProperty(PatternMatching.VARIABLE_MAPPING_KEY, variableMapping);
        graphHead.setTransactionTime(new Tuple2<>(globalTime[0], globalTime[1]));
        graphHead.setValidTime(new Tuple2<>(globalTime[2], globalTime[3]));
        out.collect(graphHead);



    }

    /**
     * Updates existing global time data (tx_from, tx_to, val_from, val_to) with time data
     * from an element.
     * @param global global time data
     * @param local time data of an element
     * @return updated global time data
     */
    private Long[] updateGlobalTime(Long[] global, Long[] local){
        return new Long[]{
                Math.max(global[0], local[0]),
                Math.min(global[1], local[1]),
                Math.max(global[2], local[2]),
                Math.min(global[3], local[3])
        };
    }

    /**
     * Initializes an vertex using the specified parameters
     *
     * @param out flat map collector
     * @param graphHead temporal graph head to assign vertex to
     * @param vertexId vertex identifier
     */
    private void initVertex(Collector<Element> out, TemporalGraphHead graphHead,
                            GradoopId vertexId, Long[] timeData) {
        initVertexWithData(out, graphHead, vertexId, null, timeData);
    }

    /**
     * Initializes an vertex using the specified parameters and adds its label
     * if the given vertex was created for the return pattern.
     *
     * @param out flat map collector
     * @param graphHead temporal graph head to assign vertex to
     * @param vertexId vertex identifier
     * @param label label associated with vertex
     */
    private void initVertexWithData(Collector<Element> out, TemporalGraphHead graphHead,
                                    GradoopId vertexId,
                                    String label, Long[] timeData) {
        if (!processedIds.contains(vertexId)) {
            TemporalVertex v = vertexFactory.initVertex(vertexId);
            v.addGraphId(graphHead.getId());
            v.setLabel(label);
            v.setTxFrom(timeData[0]);
            v.setTxTo(timeData[1]);
            v.setValidFrom(timeData[2]);
            v.setValidTo(timeData[3]);
            out.collect(v);
            processedIds.add(vertexId);
        }
    }

    /**
     * Initializes an edge using the specified parameters.
     *
     * @param out flat map collector
     * @param graphHead temporal graph head to assign edge to
     * @param edgeId edge identifier
     * @param sourceId source vertex identifier
     * @param targetId target vertex identifier
     */
    private void initEdge(Collector<Element> out, TemporalGraphHead graphHead,
                          GradoopId edgeId, GradoopId sourceId, GradoopId targetId, Long[] timeData) {
        initEdgeWithData(out, graphHead, edgeId, sourceId, targetId, null, timeData);
    }

    /**
     * Initializes an edge using the specified parameters and adds its label
     * if the given edge was created for return pattern
     *
     * @param out flat map collector
     * @param graphHead temporal graph head to assign edge to
     * @param edgeId edge identifier
     * @param sourceId source vertex identifier
     * @param targetId target vertex identifier
     * @param label label associated with edge
     */
    private void initEdgeWithData(Collector<Element> out, TemporalGraphHead graphHead,
                                  GradoopId edgeId, GradoopId sourceId, GradoopId targetId,
                                  String label, Long[] timeData) {
        if (!processedIds.contains(edgeId)) {
            TemporalEdge e = edgeFactory.initEdge(edgeId, sourceId, targetId);
            e.addGraphId(graphHead.getId());
            e.setLabel(label);
            e.setTxFrom(timeData[0]);
            e.setTxTo(timeData[1]);
            e.setValidFrom(timeData[2]);
            e.setValidTo(timeData[3]);
            out.collect(e);
            processedIds.add(edgeId);
        }
    }
}
