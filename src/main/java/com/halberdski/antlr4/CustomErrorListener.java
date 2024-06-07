package com.halberdski.antlr4;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
// import org.antlr.v4.runtime.tree.TerminalNode;

public class CustomErrorListener extends BaseErrorListener {
    private Parser parser;

    public CustomErrorListener(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
            int line, int charPositionInLine,
            String msg, RecognitionException e) throws ParseCancellationException {
        String ruleName = getRuleName(recognizer);
        String errorMessage = "Syntax Error in rule '" + ruleName + "' at line " + line + ", position "
                + charPositionInLine + ": " + msg;
        throw new ParseCancellationException(errorMessage);
    }

    private String getRuleName(Recognizer<?, ?> recognizer) {
        if (recognizer instanceof Parser) {
            ParserRuleContext ctx = ((Parser) recognizer).getRuleContext();
            return parser.getRuleNames()[ctx.getRuleIndex()];
        }
        return "";
    }
}
