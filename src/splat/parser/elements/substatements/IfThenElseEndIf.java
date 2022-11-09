package splat.parser.elements.substatements;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.Statement;

import java.util.List;

public class IfThenElseEndIf extends Statement {

    private List<Expression> expressions;
    private List<Statement> statementsThen;
    private List<Statement> statementsElse;

    public IfThenElseEndIf(Token tok) {
        super(tok);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Statement> getStatementsThen() {
        return statementsThen;
    }

    public void setStatementsThen(List<Statement> statementsThen) {
        this.statementsThen = statementsThen;
    }

    public List<Statement> getStatementsElse() {
        return statementsElse;
    }

    public void setStatementsElse(List<Statement> statementsElse) {
        this.statementsElse = statementsElse;
    }
}
