package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.Map;

public class Label extends Expression {

    private String label;

    public Label(Token tok) {
        super(tok);
    }

    public Label(Token tok, String label) {
        super(tok);
        this.label = label;
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        if (!varAndParamMap.containsKey(this.getLabel())) {
            throw new SemanticAnalysisException("Variable '" + this.label  + "' is not defined", this);
        }

        return varAndParamMap.get(this.getLabel());
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
