package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;

import java.util.List;

public class Label extends Expression {

    private String label;

    public Label(Token tok) {
        super(tok);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
