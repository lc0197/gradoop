package org.gradoop.examples.cypher;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.hadoop.conf.Configuration;
import org.gradoop.flink.io.api.DataSource;
import org.gradoop.flink.io.impl.csv.CSVDataSource;
import org.gradoop.flink.io.impl.csv.indexed.IndexedCSVDataSource;
import org.gradoop.flink.model.impl.epgm.GraphCollection;
import org.gradoop.flink.model.impl.epgm.LogicalGraph;
import org.gradoop.flink.model.impl.operators.matching.common.statistics.GraphStatistics;
import org.gradoop.flink.model.impl.operators.matching.common.statistics.GraphStatisticsHDFSReader;
import org.gradoop.flink.util.GradoopFlinkConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static org.apache.flink.api.java.ExecutionEnvironment.getExecutionEnvironment;

public class CypherBenchmark {
  /**
   * Option to declare path to indexed input graph
   */
  private static final String OPTION_INPUT_PATH = "i";
  /**
   * Option do declare path to statistics
   */
  private static final String OPTION_STATISTICS_PATH = "s";
  /**
   * Option to declare output path to statistics csv file
   */
  private static final String OPTION_CSV_PATH = "c";
  /**
   * Option to declare verification
   */
  private static final String OPTION_QUERY = "q";
  /**
   * Option for used first name in query.
   */
  private static final String OPTION_FIRST_NAME = "n";
  /**
   * Used input path
   */
  private static String INPUT_PATH;
  /**
   * Used to indicate if statistics are used
   */
  private static boolean HAS_STATISTICS;
  /**
   * Used statistics input path
   */
  private static String STATISTICS_INPUT_PATH;
  /**
   * Used output path for csv statistics
   */
  private static String CSV_PATH;
  /**
   * Used query
   */
  private static String QUERY;
  /**
   * Used first name for query (q1,q2,q3)
   */
  private static String FIRST_NAME;

  /**
   * Command line options for the runner.
   */
  protected static final Options OPTIONS = new Options();

  static {
    OPTIONS.addOption(OPTION_INPUT_PATH, "input", true,
      "Input path to indexed source files.");
    OPTIONS.addOption(OPTION_CSV_PATH, "csv", true,
      "Output path to csv statistics output");
    OPTIONS.addOption(OPTION_QUERY, "query", true,
      "Used query (q1,q2,q3,q4,q5,q6)");
    OPTIONS.addOption(OPTION_FIRST_NAME, "query-name", true,
      "Used first Name in Cypher Query");
    OPTIONS.addOption(OPTION_STATISTICS_PATH, "statistics", true,
      "Input path to previously generated statistics.");
  }

  /**
   * Main program to run the benchmark. Arguments are the available options.
   *
   * @param args program arguments
   * @throws Exception IO or execution Exception
   */
  public static void main(String[] args) throws Exception {
    CommandLine cmd = parseArguments(args);

    if (cmd == null) {
      return;
    }

    // test if minimum arguments are set
    performSanityCheck(cmd);

    // read cmd arguments
    readCMDArguments(cmd);

    ExecutionEnvironment env = getExecutionEnvironment();
    GradoopFlinkConfig config = GradoopFlinkConfig.createConfig(env);

    // read graph
    DataSource source = new CSVDataSource(INPUT_PATH, config);
    LogicalGraph graph = source.getLogicalGraph();

    // prepare collection
    GraphCollection collection;

    // get cypher query
    String query = getQuery(QUERY);

    // execute cypher with or without statistics
    if (HAS_STATISTICS) {
      GraphStatistics statistics = GraphStatisticsHDFSReader
        .read(STATISTICS_INPUT_PATH, new Configuration());

      collection = graph.query(query, statistics);
    } else {
      collection = graph.query(query);
    }

    // count embeddings
    System.out.println(collection.getGraphHeads().count());

    // execute and write job statistics
    writeCSV(env);
  }

  /**
   * Returns used query for benchmark
   *
   * @param query argument input
   * @return used query
   */
  private static String getQuery(String query) {
    switch (query) {
    case "q1" : return Queries.q1(FIRST_NAME);
    case "q2" : return Queries.q2(FIRST_NAME);
    case "q3" : return Queries.q3(FIRST_NAME);
    case "q4" : return Queries.q4();
    case "q5" : return Queries.q5();
    case "q6" : return Queries.q6();
    default : throw new IllegalArgumentException("Unsupported query: " + query);
    }
  }

  protected static CommandLine parseArguments(String[] args)
    throws ParseException {
    return new DefaultParser().parse(OPTIONS, args);
  }

  /**
   * Reads the given arguments from command line
   *
   * @param cmd command line
   */
  private static void readCMDArguments(CommandLine cmd) {
    INPUT_PATH = cmd.getOptionValue(OPTION_INPUT_PATH);
    CSV_PATH = cmd.getOptionValue(OPTION_CSV_PATH);
    QUERY = cmd.getOptionValue(OPTION_QUERY);
    HAS_STATISTICS = cmd.hasOption(OPTION_STATISTICS_PATH);
    STATISTICS_INPUT_PATH = cmd.getOptionValue(OPTION_STATISTICS_PATH);
    FIRST_NAME = cmd.getOptionValue(OPTION_FIRST_NAME);
  }

  /**
   * Checks if the minimum of arguments is provided
   *
   * @param cmd command line
   */
  private static void performSanityCheck(CommandLine cmd) {
    if (!cmd.hasOption(OPTION_INPUT_PATH)) {
      throw new IllegalArgumentException("Define a graph input directory.");
    }
    if (!cmd.hasOption(OPTION_CSV_PATH)) {
      throw new IllegalArgumentException("Path to CSV-File need to be set.");
    }
    if (!cmd.hasOption(OPTION_QUERY)) {
      throw new IllegalArgumentException("Define a query to run (q1,q2,q3,q4,q5,q6).");
    }
    if (cmd.getOptionValue(OPTION_QUERY).equals("q1") ||
      cmd.getOptionValue(OPTION_QUERY).equals("q2") ||
      cmd.getOptionValue(OPTION_QUERY).equals("q3")) {
      if (!cmd.hasOption(OPTION_FIRST_NAME)) {
        throw new IllegalArgumentException("Define a first name for query");
      }
    }
  }

  /**
   * Method to create and add lines to a csv-file
   *
   * @param env given ExecutionEnvironment
   * @throws IOException exeption during file writing
   */
  private static void writeCSV(ExecutionEnvironment env) throws IOException {

    String head = String.format("%s|%s|%s|%s|%s%n",
      "Parallelism",
      "dataset",
      "query",
      "usedStatistics",
      "Runtime(s)");

    String tail = String.format("%s|%s|%s|%s|%s%n",
      env.getParallelism(),
      INPUT_PATH,
      QUERY,
      HAS_STATISTICS,
      env.getLastJobExecutionResult().getNetRuntime(TimeUnit.SECONDS));

    File f = new File(CSV_PATH);
    if (f.exists() && !f.isDirectory()) {
      FileUtils.writeStringToFile(f, tail, true);
    } else {
      PrintWriter writer = new PrintWriter(CSV_PATH, "UTF-8");
      writer.print(head);
      writer.print(tail);
      writer.close();
    }
  }
}