package splat.executor;

import splat.parser.elements.Type;

public abstract class Value {

    private Integer integerValue;
    private String stringValue;
    private Boolean booleanValue;
    private Type type;

	// TODO: Implement me!

    public Value(Integer integerValue, String stringValue, Boolean booleanValue, Type type) {
        this.integerValue = integerValue;
        this.stringValue = stringValue;
        this.booleanValue = booleanValue;
        this.type = type;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
