package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.Statement;

import java.util.List;

public class Initialization extends Statement {

    private String label;
    private List<Expression> literal;

    public Initialization(Token tok) {
        super(tok);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Expression> getLiteral() {
        return literal;
    }

    public void setLiteral(List<Expression> literal) {
        this.literal = literal;
    }
}
