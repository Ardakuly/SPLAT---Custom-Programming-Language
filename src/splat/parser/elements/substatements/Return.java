package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;

public class Return extends Statement {

    private Expression expression;

    public Return(Token tok) {
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

         if  (this.getExpressions() == null) {
             if (varAndParamMap.get("0return") != Type.Void) {
                 throw new SemanticAnalysisException("Return type must be void", this);
             }
             return;
         }

         if (varAndParamMap.get("0return") != expression.analyzeAndGetType(funcMap, varAndParamMap)) {
             throw new SemanticAnalysisException("Return type does not match", this);
         }
    }

    public Expression getExpressions() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
