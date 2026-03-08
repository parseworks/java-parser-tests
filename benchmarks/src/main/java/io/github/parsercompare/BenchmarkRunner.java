package io.github.parsercompare;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BenchmarkRunner {

    @State(Scope.Thread)
    public static class ParserState {
        @Param("placeholder")
        public String parserName;

        public TestParser parser;
        public String csvData;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            csvData = generateCsvData(10000, 10);
            ServiceLoader<TestParser> loader = ServiceLoader.load(TestParser.class);
            for (TestParser p : loader) {
                if (p.getName().equals(parserName)) {
                    parser = p;
                    break;
                }
            }
            if (parser == null) {
                throw new IllegalStateException("Parser not found: " + parserName);
            }

            // Record output for comparison
            List<List<String>> result = parser.parseCSV(csvData);
            String output = result.stream()
                    .map(line -> String.join(",", line))
                    .collect(Collectors.joining("\n"));
            System.out.println("[DEBUG_LOG] Parser: " + parserName + ", Result size: " + (result == null ? "null" : result.size()) + ", Output length: " + output.length());
            Path outputPath = Paths.get("output_" + parserName + ".txt");
            Files.writeString(outputPath, output);
        }
    }

    @Benchmark
    public void testParser(ParserState state) {
        state.parser.parseCSV(state.csvData);
    }

    private static String generateCsvData(int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random(42); // Seed for reproducibility
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (random.nextBoolean()) {
                    sb.append("value").append(i).append(j);
                } else {
                    sb.append("\"value,\"\"").append(i).append(j).append("\"");
                }
                if (j < cols - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws RunnerException {
        List<String> parserNames = new ArrayList<>();
        ServiceLoader<TestParser> loader = ServiceLoader.load(TestParser.class);
        for (TestParser parser : loader) {
            parserNames.add(parser.getName());
        }

        if (parserNames.isEmpty()) {
            throw new IllegalStateException("No TestParser implementations found!");
        }

        Options opt = new OptionsBuilder()
                .include(BenchmarkRunner.class.getSimpleName())
                .param("parserName", parserNames.toArray(new String[0]))
                .build();

        new Runner(opt).run();
    }
}
