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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WhileLoop extends Statement {

    private Expression expression;
    private List<Statement> statements;

    public WhileLoop(Token tok) {
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        // Checking for expression

        if (Type.Boolean != expression.analyzeAndGetType(funcMap, varAndParamMap)) {

            throw new SemanticAnalysisException("Expression type is not boolean", this);

        }


        // Checking for statements
        for (Statement statement : statements) {

            statement.analyze(funcMap, varAndParamMap);

        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        Value calculated = expression.evaluate(funcMap, varAndParamMap);

        if (calculated.getBooleanValue()) {

            Iterator<Statement> statementIterator = this.getStatements().listIterator();

            while (statementIterator.hasNext()) {

                statementIterator.next().execute(funcMap, varAndParamMap);

            }

        }

    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }
}
