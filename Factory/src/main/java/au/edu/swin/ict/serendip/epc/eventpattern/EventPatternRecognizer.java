package au.edu.swin.ict.serendip.epc.eventpattern;

import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

import java.io.StringReader;

/**
 * @author Malinda
 * @deprecated Use au.edu.swin.ict.serendip.grammar.ep.EventPatternRecognizer instead
 */
public class EventPatternRecognizer implements EventPatternParserTokenTypes {

    public static AST patternToAST(String pattern) throws RecognitionException,
    TokenStreamException {
        String expr = pattern;
        EventPatternLexer lexer = new EventPatternLexer(new StringReader(expr));
        EventPatternParser parser = new EventPatternParser(lexer);
        // Parse the input expression

        parser.orpattern();

        CommonAST t = (CommonAST) parser.getAST();
        // Print the resulting tree out in LISP notation

        // Tree
        AST tree = parser.getAST();
        return tree;
    }
}
