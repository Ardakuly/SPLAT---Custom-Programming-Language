package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.primitiveDataType.DataType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UnaryOperator extends Expression {

    private String operator;

    public UnaryOperator(Token tok) {
        super(tok);
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

}
