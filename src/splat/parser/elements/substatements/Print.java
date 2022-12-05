package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;

public class Print extends Statement {

    private Expression expression;

    public Print(Token tok) {
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        expression.analyzeAndGetType(funcMap, varAndParamMap);
    }

    public Expression getExpressions() {
        return expression;
    }

    public void setExpressions(Expression expression) {
        this.expression = expression;
    }
}
