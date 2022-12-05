package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Type;
import java.util.Map;

public class Literal extends Expression {

    private String value;
    private Type type;

    public Literal(Token tok) {
        super(tok);
    }

    public Literal(Token tok, String value, Type type) {
        super(tok);
        this.value = value;
        this.type = type;
    }


    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return this.getType();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
