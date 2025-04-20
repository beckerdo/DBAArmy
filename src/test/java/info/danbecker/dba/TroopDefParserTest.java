package info.danbecker.dba;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import org.antlr.v4.runtime.CharStream;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Test pieces of ANTLR lexer and parser for use with DBA. */
public class TroopDefParserTest {
    @Test
    public void testLexer() {
        System.out.println( "Using Java version=" + Runtime.version() );
        CharStream charStream = CharStreams.fromString("Kn/(2xLCh or WWg)");
        DBAArmyLexer lexer = new DBAArmyLexer(charStream);
        List<Token> tokens = (List<Token>) lexer.getAllTokens();
        assertEquals ( 9, tokens.size());
        // tokens
	    //    .forEach( t -> System.out.println( "Token type=" + t.getType() +  ", " + t.getText() )            );
    }

    @Test
    public void testParser() {
        CharStream charStream = CharStreams.fromString("Kn/(2xLCh or WWg)");
        DBAArmyLexer lexer = new DBAArmyLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer); // This class is useless alone.
        DBAArmyParser parser = new DBAArmyParser(tokenStream);
        String[] ruleNames = parser.getRuleNames();
        assertEquals( 24, ruleNames.length);

        ParseTree tree = parser.expr();
        // System.out.println(tree.toStringTree(parser)); // print LISP-style tree
        assertInstanceOf( DBAArmyParser.ExprContext.class, tree); // Kn / expression
        assertEquals( 3, tree.getChildCount()); // Kn / expression
    }
}
