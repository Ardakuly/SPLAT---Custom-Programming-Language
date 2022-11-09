package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.Statement;

import java.util.List;

public class IfThenEndIf extends Statement {

    private List<Expression> expressions;
    private List<Statement> statementsThen;

    public IfThenEndIf(Token tok) {
        super(tok);
    }

    public List<Expression> getExpression() {
        return expressions;
    }

    public void setExpression(List<Expression> expression) {
        this.expressions = expression;
    }

    public List<Statement> getStatementsThen() {
        return statementsThen;
    }

    public void setStatementsThen(List<Statement> statementsThen) {
        this.statementsThen = statementsThen;
    }
}
