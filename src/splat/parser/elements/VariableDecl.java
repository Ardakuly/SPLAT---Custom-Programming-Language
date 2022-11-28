package splat.parser.elements;

import splat.lexer.Token;
import splat.parser.elements.primitiveDataType.DataType;

public class VariableDecl extends Declaration {

	private DataType type;
	private String label;
	private String value;

	
	// Need to add extra arguments for setting fields in the constructor 
	public VariableDecl(Token tok, DataType type, String label, String value) {
		super(tok);
		this.type = type;
		this.label = label;
		this.value = value;
	}

	public VariableDecl(Token tok) {
		super(tok);
	}

	// Getters?


	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
