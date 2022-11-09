package splat.parser.elements;

import splat.lexer.Token;

import java.util.List;

public class FunctionDecl extends Declaration {

	private String returnType;
	private String label;
	private List<VariableDecl> parameters;
	private List<VariableDecl> variables;
	
	// Need to add extra arguments for setting fields in the constructor 
	public FunctionDecl(Token tok, String returnType, String label, List<VariableDecl> parameters) {
		super(tok);
		this.returnType = returnType;
		this.label = label;
		this.parameters = parameters;
	}

	// Getters?


	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<VariableDecl> getParameters() {
		return parameters;
	}

	public void setParameters(List<VariableDecl> parameters) {
		this.parameters = parameters;
	}

	// Fix this as well
	public String toString() {
		return this.label + " " + "( " + parameters.toString() + ")" + " : " +
				this.returnType + " " + "is" + " " + variables.toString() + " " + "begin" + " " + "stmts"  + "end" + ";";
	}
}
