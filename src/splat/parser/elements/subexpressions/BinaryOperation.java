package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;

public class BinaryOperation extends Expression {

    private String operator;

    public BinaryOperation(Token tok) {
        super(tok);
    }


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


}
