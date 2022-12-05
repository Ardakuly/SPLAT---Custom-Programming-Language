package splat.parser.elements;

import splat.lexer.Token;

public class VariableDecl extends Declaration {

	private Type type;
	private String value;

	
	// Need to add extra arguments for setting fields in the constructor

	public VariableDecl(Token tok) {
		super(tok);
	}

	// Getters?


	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	// Fix this as well
	public String toString() {
		return getLabel() + " " + getType();
	}
}
