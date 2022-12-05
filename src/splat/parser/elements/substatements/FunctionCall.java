package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;

public class FunctionCall extends Statement {

    private String label;
    private List<Expression> arguments;

    public FunctionCall(Token tok) {
        super(tok);
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        if (!funcMap.containsKey(this.getLabel())) {
            throw new SemanticAnalysisException
                    ("Called function does not exist", new FunctionDecl(new Token(label, super.getLine(), super.getColumn())));
        }

        List<Expression> arguments = this.getArguments();

        FunctionDecl decl = funcMap.get(label);

        for (int i = 0; i < decl.getParameters().size(); i++) {

            if (decl.getParameters().get(i).getType() != arguments.get(i).analyzeAndGetType(funcMap, varAndParamMap)) {
                throw new SemanticAnalysisException("Type of the argument and type of parameter in declaration does not match", this);
            }

        }




    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public void setArguments(List<Expression> arguments) {
        this.arguments = arguments;
    }
}
