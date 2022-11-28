package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.Statement;

import java.util.List;

public class Return extends Statement {

    private List<Expression> expression;

    public Return(Token tok) {
        super(tok);
    }

    public List<Expression> getExpressions() {
        return expression;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expression = expression;
    }
}
