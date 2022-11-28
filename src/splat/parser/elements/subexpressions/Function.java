package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;

import java.util.List;

public class Function extends Expression {

    private List<String> parameters;

    public Function(Token tok) {
        super(tok);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
