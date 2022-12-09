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

         Type expressionType = expression.analyzeAndGetType(funcMap, varAndParamMap);

         if (varAndParamMap.get("0return") != expressionType) {

             throw new SemanticAnalysisException("Return type does not match", this);

         } else {

             varAndParamMap.put("0ActualReturn", expressionType);

         }


    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        if (this.getExpressions() != null) {

            Value calculated = expression.evaluate(funcMap, varAndParamMap);

            throw new ReturnFromCall(calculated);
        } else {

            throw new ReturnFromCall(null);

        }
    }

    public Expression getExpressions() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
