package splat.executor.subvalues;

import splat.executor.Value;
import splat.parser.elements.Type;

public class IntegerValue extends Value {

    public IntegerValue(Integer value, Type type) {
        super(value, null,null, type);
    }



    public Integer getIntegerValue() {
        return super.getIntegerValue();
    }

    public Type getType() {
        return super.getType();
    }
}
