package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;

public class If extends Statement {

    private Expression expression;
    private List<Statement> statementsThen;
    private List<Statement> statementsElse;

    public If(Token tok) {
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        // Checking for expression

        if (Type.Boolean != expression.analyzeAndGetType(funcMap, varAndParamMap)) {
            throw new SemanticAnalysisException("Expression type is not boolean", this);
        }


        // Checking for statements after then

        for (Statement statement : statementsThen) {
            statement.analyze(funcMap, varAndParamMap);
        }

        // Checking for statements after else

        if (statementsElse != null) {
            for (Statement statement : statementsElse) {
                statement.analyze(funcMap, varAndParamMap);
            }
        }


    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public List<Statement> getStatementsThen() {
        return statementsThen;
    }

    public void setStatementsThen(List<Statement> statementsThen) {
        this.statementsThen = statementsThen;
    }

    public List<Statement> getStatementsElse() {
        return statementsElse;
    }

    public void setStatementsElse(List<Statement> statementsElse) {
        this.statementsElse = statementsElse;
    }
}
