package io.github.parsercompare;

import java.util.List;

public interface CsvParser {
    String getName();
    List<List<String>> parse(String input);
}
