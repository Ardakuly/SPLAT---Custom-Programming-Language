package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.VariableDecl;
import splat.parser.elements.primitiveDataType.DataType;

import java.util.List;

public class NVFunctionCall extends Expression {

    private String label;
    private List<Expression> parameters;
    private DataType returnType;

    public NVFunctionCall(Token tok) {
        super(tok);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public void setParameters(List<Expression> parameters) {
        this.parameters = parameters;
    }

    public DataType getReturnType() {
        return returnType;
    }

    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }
}
