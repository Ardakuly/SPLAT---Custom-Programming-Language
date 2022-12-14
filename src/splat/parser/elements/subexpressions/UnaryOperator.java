package splat.parser.elements.subexpressions;

import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.subvalues.BooleanValue;
import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.Map;

public class UnaryOperator extends Expression {

    private String operator;
    private Expression expression;

    public UnaryOperator(Token tok) {
        super(tok);
    }

    public UnaryOperator(Token tok, String operator, Expression expression) {
        super(tok);
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        Type expressionType = expression.analyzeAndGetType(funcMap, varAndParamMap);

        if (expressionType != Type.Boolean) {
            throw new SemanticAnalysisException("Type for unary operator must be Boolean", this);
        }

        return Type.Boolean;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {

        Value calculated = this.getExpression().evaluate(funcMap, varAndParamMap);

        Boolean value = calculated.getBooleanValue();

        if (!value) {

            return new BooleanValue(true, Type.Boolean);

        } else {

            return new BooleanValue(false, Type.Boolean);

        }

    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
