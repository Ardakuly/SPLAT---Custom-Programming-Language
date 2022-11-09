package splat.parser.elements;

import splat.lexer.Token;

public class VariableDecl extends Declaration {

	private String type;
	private String label;

	
	// Need to add extra arguments for setting fields in the constructor 
	public VariableDecl(Token tok, String type, String label) {
		super(tok);
		this.type = type;
		this.label = label;
	}

	// Getters?


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


	// Fix this as well
	public String toString() {
		return label + " " + type;
	}
}
