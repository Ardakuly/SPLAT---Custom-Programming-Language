package splat.executor.subvalues;

import splat.executor.Value;
import splat.parser.elements.Type;

public class BooleanValue extends Value {

    public BooleanValue(Boolean value, Type type) {
        super(null, null, value, type);
    }

    public Boolean getBooleanValue() {
        return super.getBooleanValue();
    }

    public Type getType() {
        return super.getType();
    }

}
