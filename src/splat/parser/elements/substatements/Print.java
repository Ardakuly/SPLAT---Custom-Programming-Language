package splat.parser.elements.substatements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;
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

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        Value value = expression.evaluate(funcMap, varAndParamMap);

        if (value.getType() == Type.String) {

            System.out.println(value.getStringValue());

        } else if (value.getType() == Type.Boolean) {

            System.out.println(value.getBooleanValue());

        }else if (value.getType() == Type.Integer) {

            System.out.println(value.getIntegerValue());

        }

    }

    public Expression getExpressions() {
        return expression;
    }

    public void setExpressions(Expression expression) {
        this.expression = expression;
    }
}
