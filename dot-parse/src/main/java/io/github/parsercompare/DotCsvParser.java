package io.github.parsercompare;

import com.google.common.labs.parse.Parser;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.labs.parse.Parser.*;
import static com.google.mu.util.CharPredicate.isNot;
import static com.google.mu.util.CharPredicate.noneOf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class DotCsvParser implements CsvParser {

    private static final Parser<?> newLine =
        Stream.of("\n", "\r\n", "\r").map(Parser::string).collect(Parser.or());

    private static final Parser<String> quoted =
        consecutive(isNot('"'), "quoted")
            .or(string("\"\"").thenReturn("\"")) // escaped quote
            .zeroOrMore(joining())
            .between("\"", "\"");

    private static final Parser<String> unquoted = consecutive(noneOf("\"\r\n,"), "unquoted field");

    private static final Parser<List<String>> line =
        anyOf(
            newLine.thenReturn(List.of()),  // empty line => [], not [""]
            anyOf(quoted, unquoted)
                .orElse("")  // empty field value is allowed
                .delimitedBy(",")
                .notEmpty()
                .followedByOrEof(newLine));

    private static final Parser<List<List<String>>> csv = line.zeroOrMore(toList()).notEmpty();

    @Override
    public String getName() {
        return "dot-parse";
    }

    @Override
    public List<List<String>> parse(String input) {
        try {
            return csv.parse(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
