package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;

public class Literal extends Expression {

    private String value;
    private String type;

    public Literal(Token tok) {
        super(tok);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
