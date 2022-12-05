package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;

public class Print_line extends Statement {

    public Print_line(Token tok) {

        super(tok);

    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        return;
    }

}
