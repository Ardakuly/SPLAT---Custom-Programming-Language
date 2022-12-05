package splat.parser.elements;

import splat.lexer.Token;

public abstract class  Declaration extends ASTElement {

	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Declaration(Token tok) {
		super(tok);
	}
}
