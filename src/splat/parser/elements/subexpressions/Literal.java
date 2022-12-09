package splat.parser.elements.subexpressions;

import splat.executor.Value;
import splat.executor.subvalues.BooleanValue;
import splat.executor.subvalues.IntegerValue;
import splat.executor.subvalues.StringValue;
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

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {

        if (this.getType() == Type.Integer) return new IntegerValue(Integer.parseInt(this.getValue()), this.getType());
        else if (this.getType() == Type.String) return new StringValue(this.getValue(), this.getType());
        else if (this.getType() == Type.Boolean) return new BooleanValue((this.getValue().equals("true")) ? true : false, this.getType());

        return null;
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
