package io.github.parsercompare;

import io.github.parseworks.Lists;
import io.github.parseworks.Parser;
import io.github.parseworks.parsers.Combinators;
import io.github.parseworks.parsers.Lexical;

import java.util.List;

import static io.github.parseworks.parsers.Combinators.*;
import static io.github.parseworks.parsers.Lexical.*;

public class ParseworksCsvParser implements TestParser {

    private static final Parser<Character, Void> newLine = Lexical.oneOf("\r\n").oneOrMore().as(null);

    private static final Parser<Character, String> quoted =
        isNot('"')
            .or(attempt(string("\"\"")).as('"'))
            .zeroOrMore()
            .map(Lists::join)
            .between('"');

    private static final Parser<Character, String> unquoted =
        not(oneOf("\"\r\n,"))
            .oneOrMore()
            .map(Lists::join)
            .expecting("unquoted field");

    private static final Parser<Character, List<String>> line = Combinators.oneOf(
        newLine.as(List.of()),
        oneOf(unquoted, quoted).zeroOrMoreSeparatedBy(chr(',')));

    private static final Parser<Character, List<List<String>>> csv = line.zeroOrMoreSeparatedBy(newLine);

    @Override
    public String getName() {
        return "parseworks";
    }

    @Override
    public List<List<String>> parseCSV(String input) {
        return csv.parse(input).value();
    }

    public static void main(String[] args) {
        System.out.println("Parsed CSV lines:");
        System.out.println(line.parse("v1,v2,\"v,3\nand\"\" 4\"").value());
    }
}
