package io.github.parsercompare;

import io.github.parseworks.Parser;

import java.util.List;
import java.util.Map;

import static io.github.parseworks.Parser.pure;
import static io.github.parseworks.parsers.Combinators.*;
import static io.github.parseworks.parsers.Lexical.*;

public class ParseworksCsvParser implements TestParser {

    private static final Parser<Character, Void> newLine = oneOf("\r\n").oneOrMore().as(null);

    static Parser<Character,String>  quoted = escapedString('"','"', Map.of( '"', '"'));

    // UNQUOTED FIELD: runs until comma or EOL; use conditional to ensure we don't start with a quote
    static Parser<Character, String> unquoted = takeWhile(c -> c != ',' && c != '\n' && c != '\r').onlyIf(c -> c != '"');

    private static final Parser<Character, List<String>> line =  oneOf(unquoted, quoted, pure("")).zeroOrMoreSeparatedBy(chr(','));

    private static final Parser<Character, List<List<String>>> csv = line.zeroOrMoreSeparatedBy(newLine);

    @Override
    public String getName() {
        return "parseworks";
    }

    @Override
    public List<List<String>> parseCSV(String input) {
        return csv.parse(input).value();
    }

}
