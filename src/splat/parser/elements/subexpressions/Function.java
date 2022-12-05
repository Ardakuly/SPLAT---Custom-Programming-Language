package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.List;
import java.util.Map;

public class Function extends Expression {

    private String label;
    private List<Expression> arguments;

    public Function(Token tok) {
        super(tok);
    }

    public Function(Token tok, String label, List<Expression> arguments) {
        super(tok);
        this.label = label;
        this.arguments = arguments;
    }


    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        if (!funcMap.containsKey(this.getLabel())) {
            throw new SemanticAnalysisException
                    ("Called function does not exist", this);
        }

        List<Expression> arguments = this.getArguments();

        FunctionDecl decl = funcMap.get(label);

        if (decl.getParameters() == null && arguments.size() > 0) {
            throw new SemanticAnalysisException("Function does not accept any parameters", this);
        }

        for (int i = 0; i < arguments.size(); i++) {

            if (decl.getParameters().get(i).getType() != arguments.get(i).analyzeAndGetType(funcMap, varAndParamMap)) {
                throw new SemanticAnalysisException("Type of the argument and type of parameter in declaration does not match", this);
            }

        }

        return funcMap.get(this.getLabel()).getReturnType();

    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public String getLabel() {
        return label;
    }
}
