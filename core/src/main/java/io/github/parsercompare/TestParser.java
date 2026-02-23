package io.github.parsercompare;

import java.util.List;

public interface TestParser {
    String getName();
    List<List<String>> parseCSV(String input);
}
