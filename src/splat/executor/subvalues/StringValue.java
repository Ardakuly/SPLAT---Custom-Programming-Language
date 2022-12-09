package splat.executor.subvalues;

import splat.executor.Value;
import splat.parser.elements.Type;

public class StringValue extends Value {

    public StringValue(String value, Type type) {
        super(null, value, null, type);
    }


    public String getStringValue() {
        return super.getStringValue();
    }

    public Type getType() {
        return super.getType();
    }
}
