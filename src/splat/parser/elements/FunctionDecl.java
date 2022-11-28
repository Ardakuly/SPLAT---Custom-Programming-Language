package splat.parser.elements;

import splat.lexer.Token;
import splat.parser.elements.primitiveDataType.DataType;

import java.util.ArrayList;
import java.util.List;

public class FunctionDecl extends Declaration {

	private DataType returnType;
	private String label;
	private List<VariableDecl> parameters;
	private List<VariableDecl> variables;
	private List<Statement> statements;
	
	// Need to add extra arguments for setting fields in the constructor

	public FunctionDecl(Token tok) {
		super(tok);
	}

	// Getters?


	public DataType getReturnType() {
		return returnType;
	}

	public void setReturnType(DataType returnType) {
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

	public List<VariableDecl> getVariables() {
		if (variables == null) return new ArrayList<>();
		return variables;
	}

	public void setVariables(List<VariableDecl> variables) {
		this.variables = variables;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	// Fix this as well
	public String toString() {
		return this.getLabel() + " " + "( " + ((getParameters() == null) ? " " : getParameters().toString()) + ")" + " : " +
				this.getReturnType() + " " + "is" + " " + getVariables().toString() + " " + "begin" + " " + "stmts"  + "end" + ";";
	}
}
