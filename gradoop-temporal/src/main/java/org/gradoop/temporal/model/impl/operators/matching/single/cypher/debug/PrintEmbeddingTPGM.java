package org.gradoop.temporal.model.impl.operators.matching.single.cypher.debug;

import org.apache.log4j.Logger;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.properties.PropertyValue;
import org.gradoop.flink.model.impl.operators.matching.common.debug.Printer;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGM;
import org.gradoop.temporal.model.impl.operators.matching.single.cypher.pojos.EmbeddingTPGMMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrintEmbeddingTPGM extends Printer<EmbeddingTPGM, GradoopId> {
    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(PrintEmbeddingTPGM.class);
    /**
     * Meta data describing the current embedding.
     */
    private final EmbeddingTPGMMetaData embeddingMetaData;

    /**
     * Constructor.
     *
     * @param embeddingMetaData meta data for the embedding to print
     */
    public PrintEmbeddingTPGM(EmbeddingTPGMMetaData embeddingMetaData) {
        this.embeddingMetaData = embeddingMetaData;
    }

    @Override
    protected String getDebugString(EmbeddingTPGM embedding) {
        String vertexMapping = embeddingMetaData.getVertexVariables().stream()
                .map(var -> String.format("%s : %s", var,
                        vertexMap.get(embedding.getId(embeddingMetaData.getEntryColumn(var)))))
                .collect(Collectors.joining(", "));

        String edgeMapping = embeddingMetaData.getEdgeVariables().stream()
                .map(var -> String.format("%s : %s", var,
                        edgeMap.get(embedding.getId(embeddingMetaData.getEntryColumn(var)))))
                .collect(Collectors.joining(", "));

        String pathMapping = embeddingMetaData.getPathVariables().stream()
                .map(var -> {
                    List<GradoopId> path = embedding.getIdList(embeddingMetaData.getEntryColumn(var));
                    List<PropertyValue> ids = new ArrayList<>();
                    for (int i = 0; i < path.size(); i++) {
                        if (i % 2 == 0) { // edge
                            ids.add(edgeMap.get(path.get(i)));
                        } else {
                            ids.add(vertexMap.get(path.get(i)));
                        }
                    }
                    return String.format("%s : %s", var, ids);
                })
                .collect(Collectors.joining(", "));

        return String.format("vertex-mapping: {%s}, edge-mapping: {%s}, path-mapping: {%s}",
                vertexMapping, edgeMapping, pathMapping);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}