import io.github.parseworks.Lists;
import io.github.parseworks.Parser;
import io.github.parseworks.parsers.Combinators;
import io.github.parseworks.parsers.Lexical;

import java.util.List;


import static io.github.parseworks.parsers.Combinators.*;
import static io.github.parseworks.parsers.Lexical.*;



public class CSVParser {

    public static Parser<Character, Void> newLine =  Lexical.oneOf("\r\n").oneOrMore().as(null);

    public static Parser<Character,String> quoted =
        Combinators.isNot('"')
            .or(attempt(string("\"\"")).as('"'))
            .zeroOrMore()
            .map(Lists::join)
            .between('"');

    public static Parser<Character, String> unquoted =
        not(Lexical.oneOf("\"\r\n,"))
            .oneOrMore()
            .map(Lists::join)
            .expecting("unquoted field");

    public static Parser<Character,List<String>> line = Combinators.oneOf(
            newLine.as(List.of()),
            Combinators.oneOf(unquoted,quoted).zeroOrMoreSeparatedBy(chr(',')));

    public static void main(String[] args) {
        System.out.println("Parsed CSV lines:");
        System.out.println(line.parse("v1,v2,\"v,3\nand\"\" 4\"").value());
    }
//return ;

}
