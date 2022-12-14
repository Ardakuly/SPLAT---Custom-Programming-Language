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

public class Initialization extends Statement {

    private String label;
    private Expression literal;

    public Initialization(Token tok) {
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        if(varAndParamMap.get(label) != literal.analyzeAndGetType(funcMap, varAndParamMap)) {

            throw new SemanticAnalysisException("Variable type and expression type does not match", this);

        }

    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        Value calculated = literal.evaluate(funcMap, varAndParamMap);

        varAndParamMap.put(this.getLabel(),calculated);

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Expression getLiteral() {
        return literal;
    }

    public void setLiteral(Expression literal) {
        this.literal = literal;
    }
}
