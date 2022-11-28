package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.Statement;

import java.util.List;

public class Print extends Statement {

    private List<Expression> expression;

    public Print(Token tok) {
        super(tok);
    }

    public List<Expression> getExpressions() {
        return expression;
    }

    public void setExpressions(List<Expression> expression) {
        this.expression = expression;
    }
}
