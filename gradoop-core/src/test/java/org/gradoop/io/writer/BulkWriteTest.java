package org.gradoop.io.writer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.gradoop.GConstants;
import org.gradoop.MapReduceClusterTest;
import org.gradoop.model.Vertex;
import org.gradoop.storage.GraphStore;
import org.gradoop.storage.hbase.EPGVertexHandler;
import org.gradoop.storage.hbase.VertexHandler;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Testing for Bulk Export.
 */
public class BulkWriteTest extends MapReduceClusterTest {

  @Test
  public void bulkWriteTest() throws IOException, ClassNotFoundException,
    InterruptedException {
    Configuration conf = utility.getConfiguration();
    // create store and store some test data
    GraphStore store = createEmptyGraphStore();
    for (Vertex v : createBasicGraphVertices()) {
      store.writeVertex(v);
    }
    store.close();

    /*
    Setup MapReduce Job for BulkExport
     */
    conf.setClass(BulkWriteEPG.VERTEX_LINE_WRITER, SimpleVertexWriter.class,
      VertexLineWriter.class);
    conf.setClass(BulkWriteEPG.VERTEX_HANDLER, EPGVertexHandler.class,
      VertexHandler.class);

    Job job = new Job(conf, BulkWriteTest.class.getName());
    Scan scan = new Scan();
    scan.setCaching(500);
    scan.setCacheBlocks(false);

    // map
    TableMapReduceUtil
      .initTableMapperJob(GConstants.DEFAULT_TABLE_VERTICES, scan,
        BulkWriteEPG.class, Text.class, NullWritable.class, job);
    // no reduce needed for that job
    job.setNumReduceTasks(0);

    // set output path
    Path outputDir = new Path("/output/export");
    FileOutputFormat.setOutputPath(job, outputDir);

    // run
    job.waitForCompletion(true);

    // read map output
    Path outputFile = new Path(outputDir, "part-m-00000");
    BufferedReader br = new BufferedReader(
      new InputStreamReader(utility.getTestFileSystem().open(outputFile)));
    String line;
    int i = 0;
    String[] fileContent = new String[BASIC_GRAPH.length];
    while ((line = br.readLine()) != null) {
      fileContent[i] = line;
      i++;
    }

    // validate text output with input graph
    validateBasicGraphVertices(fileContent);
  }
}
