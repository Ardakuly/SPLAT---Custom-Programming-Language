package splat.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import splat.lexer.Token;
import splat.parser.elements.*;
import splat.parser.elements.Type;
import splat.parser.elements.subexpressions.*;
import splat.parser.elements.substatements.*;

public class Parser {

	private LinkedList<Token> tokens;

	private final Pattern namePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

	private final Pattern integerPattern = Pattern.compile("^[1-9][0-9]*$");

	private List<Statement> statements = new ArrayList<>();

	private List<String> declarations = new ArrayList<>();


	public Parser(LinkedList<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'." + tok.getRow(), tok);
		}
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}
	
	
	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);
			
			checkNext("program");
			
			List<Declaration> decls = parseDecls();
			
			checkNext("begin");

			List<Statement> stmts = parseStmts(new ArrayList<>());
			
			checkNext("end");
			checkNext(";");
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<Declaration>();
		
		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}

		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {

		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected --> " + tok.getValue() + tok.getRow(), tok);
		}
	}
	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {

		Token label = tokens.removeFirst();

		FunctionDecl functionDecl = new FunctionDecl(label);

		Matcher matcher = namePattern.matcher(label.getValue());

		if (matcher.matches()) {

			functionDecl.setLabel(label.getValue());

		} else {

			throw new ParseException("Function name does not meet requirements", new Token(label.getValue(), label.getRow(), label.getColumn()));

		}

		//------------------ Parameters--------------------//

		checkNext("("); // remove '('

		List<VariableDecl> parameters = new ArrayList<>();

		if (!peekNext(")")) {


			do {
				if (peekNext(",")) checkNext(",");

				parameters.add(parseVarDecl());

			} while (peekNext(","));

		}

		functionDecl.setParameters(parameters);

		checkNext(")"); // remove ')'

		//------------------ Parameters--------------------//

		checkNext(":"); //remove ':'

		// ----------------- Return Type -------------------//

		Token returnType = tokens.removeFirst(); // Take return statement

		if (Type.String.name().equals(returnType.getValue()) ) {
			functionDecl.setReturnType(Type.String);
		} else if (Type.Integer.name().equals(returnType.getValue())) {
			functionDecl.setReturnType(Type.Integer);
		} else if (Type.Boolean.name().equals(returnType.getValue())) {
			functionDecl.setReturnType(Type.Boolean);
		}else if ("void".equals(returnType.getValue())) {
			functionDecl.setReturnType(Type.Void);
		} else {
			throw new ParseException("ReturnType for variable does not meet requirements --->" + returnType.getValue(), returnType);
		}

		// ----------------- Return Type -------------------//

		checkNext("is"); //remove is

		if (!peekNext("begin")) {

			do {
				functionDecl.getVariables().add(parseVarDecl());

			} while (!tokens.getFirst().getValue().equals("begin") );

    	}

		checkNext("begin");

		if (!peekNext("end")) {

			functionDecl.setStatements(new ArrayList<>(parseStmts(new ArrayList<>())));

		}

		statements = new ArrayList<>();

		checkNext("end"); //remove 'end'
		checkNext(";"); //remove ';'

		declarations.add(functionDecl.getLabel());

		return functionDecl;
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {

		Token labelToken = tokens.removeFirst();

		if (Arrays.asList("while", "if", "print", "print_line", "return").contains(labelToken.getValue())) {

			throw new ParseException("Invalid variable name", labelToken);

		}

		tokens.removeFirst(); // remove ":"

		Token typeToken = tokens.removeFirst();

		VariableDecl varDecl = new VariableDecl(typeToken);

		Matcher matcher = namePattern.matcher(labelToken.getValue());

		if (matcher.matches()) {

			varDecl.setLabel(labelToken.getValue());

		} else {

			throw new ParseException("Variable name does not meet requirements", labelToken);

		}

		if (Type.String.name().equals(typeToken.getValue()) ) {

			varDecl.setType(Type.String);

		} else if (Type.Integer.name().equals(typeToken.getValue())) {

			varDecl.setType(Type.Integer);

		} else if (Type.Boolean.name().equals(typeToken.getValue())) {

			varDecl.setType(Type.Boolean);

		} else {

			throw new ParseException("DataType for variable does not meet requirements "
					+ typeToken.getValue() + " " + typeToken.getRow(), typeToken);

		}

		if (peekNext(";")) checkNext(";");

		declarations.add(varDecl.getLabel());

		return varDecl;
	}
	
	/*
	 * <stmts> ::= (  <stmt>  )*
	 */

	private List<Statement> parseStmts(List<Statement> statements) throws ParseException {

		while (!peekNext("end") && !peekNext("else")) {
			parseStmt(statements);
		}

		return statements;

	}

	private List<Statement> parseStmt(List<Statement> statements) throws ParseException {

		if (peekNext("end")) return statements;

		boolean parsed = false;

		Matcher matcher = namePattern.matcher(tokens.getFirst().getValue());
		Boolean isValid = matcher.find();

		if (peekNext("if")) {

			Token ifToken = tokens.removeFirst();
			If ifStatement = new If(ifToken);

			if(peekNext("(") && peekTwoAhead("(")) checkNext("("); //remove '('

			Expression previous = null;

			while (!peekNext("then")) {
				previous = parseExpr(previous);

				if (peekNext(")")) checkNext(")");

			}

			ifStatement.setExpression(previous);

			if(peekNext(")")) checkNext(")"); //remove '('
			checkNext("then"); //remove 'then'

			ifStatement.setStatementsThen(parseStmts(new ArrayList<>()));

			if (peekNext("else")) {
				checkNext("else"); //remove 'else'
				ifStatement.setStatementsElse(parseStmts(new ArrayList<>()));
			}

			checkNext("end"); //remove 'end'
			checkNext("if"); //remove 'if'
			checkNext(";"); //remove ';'

			parsed = true;

			statements.add(ifStatement);

		} else if (peekNext("print")) {

			Token printToken = tokens.removeFirst();

			Print printStatement = new Print(printToken);

			if (peekNext("(") && peekTwoAhead("(")){
				checkNext("(");
			}

			if (peekTwoAhead(")")) throw new ParseException("Invalid use of parenthesis", tokens.getFirst());

			Expression previous = null;

			while (!peekNext(";")) {

				previous = parseExpr(previous);

				if (peekNext(")")) checkNext(")");
			}

			printStatement.setExpressions(previous);

			if (peekNext(")")) checkNext(")");

			checkNext(";");

			statements.add(printStatement);

			return statements;

		} else if (peekNext("print_line")) {

			Token printLineToken = tokens.removeFirst();

			Print_line printLineStatement = new Print_line(printLineToken);

			checkNext(";"); //remove ';'

			statements.add(printLineStatement);

			return statements;

		} else if (peekNext("return")) {

			Token returnToken = tokens.removeFirst();

			Return returnStatement = new Return(returnToken);

			if (peekNext("(") && peekTwoAhead("(")) checkNext("(");

			if (!peekNext(";")) {

				Expression previous = null;

				while (!peekNext(";")) {

					previous = parseExpr(previous);

					if (peekNext(")")) checkNext(")");
				}

				returnStatement.setExpression(previous);
			}

			if (peekNext(")")) checkNext(")");

			checkNext(";"); //remove ';'

			statements.add(returnStatement);

			return statements;

		} else if (peekNext("while")) {

			Token whileToken = tokens.removeFirst();

			WhileLoop whileLoopStatement = new WhileLoop(whileToken);

			if(peekNext("(") && peekTwoAhead("(")) checkNext("(");

			Expression previous = null;

			while (!peekNext("do")) {

				previous = parseExpr(previous);

				if (peekNext(")")) checkNext(")");
			}

			whileLoopStatement.setExpression(previous);

			//if(peekNext(")")) checkNext(")");

			checkNext("do");

			whileLoopStatement.setStatements(parseStmts(new ArrayList<>()));

			checkNext("end");
			checkNext("while");
			checkNext(";");

			statements.add(whileLoopStatement);

			parsed = true;

		} else if (isValid && !peekTwoAhead("(")){

			Token initializationToken = tokens.removeFirst();

			Initialization initializationStatement =
					new Initialization(initializationToken);

			initializationStatement.setLabel(initializationToken.getValue());

			checkNext(":");
			checkNext("=");


			while (peekNext("(") && peekTwoAhead("(")) checkNext("(");

			Expression previous = null;

			while (!peekNext(";")) {

				previous = parseExpr(previous);

				if (peekNext(")")) checkNext(")");
			}

			initializationStatement.setLiteral(previous);

			if (peekNext(")")) checkNext(")");

			checkNext(";");

			parsed = true;

			statements.add(initializationStatement);

		} else if (isValid && peekTwoAhead("(")) {

			Token functionCallToken = tokens.removeFirst();

			FunctionCall functionCallStatement = new FunctionCall(functionCallToken);

			if(peekNext("(")) checkNext("(");

			List<Expression> arguments = new ArrayList<>();

			while (!peekNext(")") && !peekNext(";")) {

				if (peekNext(",")) checkNext(",");

				Expression expression = parseExpr(null);

				if (expression != null) arguments.add(expression);

			}

			functionCallStatement.setArguments(arguments);

			functionCallStatement.setLabel(functionCallToken.getValue());

			if(peekNext(")")) checkNext(")");

			checkNext(";");

			statements.add(functionCallStatement);

			parsed = true;

		}

		if (!parsed) throw new ParseException("Bad statement exception", tokens.removeFirst());

		return statements;

	}


	private Expression parseExpr(Expression previous) throws ParseException {

		// Literals + negative Integer ++++
		// label +++
		// FunctionCall
		// Unary Operator && Binary Operator

		Token token = tokens.getFirst();

		Matcher name = namePattern.matcher(token.getValue());

		boolean isMatchName = name.find() && (!token.getValue().startsWith("" + (char)34) && !token.getValue().endsWith("" + (char)34));

		Matcher literal = integerPattern.matcher(token.getValue());

		boolean isMatchLiteral = literal.find();

		Matcher negativeInteger = integerPattern.matcher(tokens.get(2).getValue());

		// -------------------- NEGATIVE INTEGER ---------------------------------------------//

		if (peekNext("(") && tokens.get(1).getValue().equals("-") && negativeInteger.find() && tokens.get(3).getValue().equals(")")) {

			checkNext("(");

			tokens.removeFirst();

			Token literalToken = tokens.removeFirst();

			Expression expression = new Literal(literalToken, "-" + literalToken.getValue(), Type.Integer);

			checkNext(")");

			return expression;

		}

		// -------------------- NEGATIVE INTEGER ---------------------------------------------//

		// -------------------- Literals -----------------------------------------------------//

		if (isMatchLiteral || tokens.getFirst().getValue().equals("0")) {

			Token literalToken = tokens.removeFirst();

			Expression expression = new Literal(literalToken, literalToken.getValue(), Type.Integer);

			return expression;

		} else if (tokens.getFirst().getValue().startsWith("" + (char)34) && tokens.getFirst().getValue().endsWith("" + (char)34)) {

			Token literalToken = tokens.removeFirst();

			Expression expression = new Literal(literalToken, literalToken.getValue(), Type.String);

			return expression;

		} else if (tokens.getFirst().getValue().equals("true") || tokens.getFirst().getValue().equals("false")) {

			Token literalToken = tokens.removeFirst();

			Expression expression = new Literal(literalToken, literalToken.getValue(), Type.Boolean);

			return expression;

			// -------------------- Literals -----------------------------------------------------//

		} else if (isMatchName && !peekTwoAhead("(") && !token.getValue().equals("not")) {

			// -------------------- Label -----------------------------------------------------//

			Token labelToken = tokens.removeFirst();

			Expression label = new Label(labelToken, labelToken.getValue());

			return label;

			// -------------------- Label -----------------------------------------------------//
		} else if (isMatchName && peekTwoAhead("(")) {

			// -------------------- Function -----------------------------------------------------//

			Token nVFunctionCallToken = tokens.removeFirst();

			checkNext("(");

			List<Expression> arguments = new LinkedList<>();

			while (!peekNext(")")) {

				if (peekNext(",")) checkNext(",");

				arguments.add(parseExpr(null));
			}

			checkNext(")");

			Expression nVFunctionCall = new Function(nVFunctionCallToken, nVFunctionCallToken.getValue(), arguments);

			return nVFunctionCall;

			// -------------------- Function -----------------------------------------------------//

		} else if (previous == null && (peekNext("(") || isBinaryExpression())) {

			checkNext("(");

			if (peekTwoAhead(")")) throw new ParseException("Need Binary or Unary expressions", tokens.getFirst());

			// -------------------- Unary Operator -----------------------------------------------------//

			boolean isUnaryOperator = isUnaryOperator();

			Expression unaryOperatorExpression = null;

			if (isUnaryOperator) {

				Token unaryOperatorToken = tokens.removeFirst();

				unaryOperatorExpression = new UnaryOperator(unaryOperatorToken, unaryOperatorToken.getValue(), parseExpr(null));

				checkNext(")");

			}

			// -------------------- Unary Operator -----------------------------------------------------//

			// -------------------- Binary Operator -----------------------------------------------------//
			boolean isBinaryOperatorExpression = isBinaryExpression();

			if (isBinaryOperatorExpression) { //   ((((x*x)*x)-((y*y)*10))+(x+(y/10)));

				Expression expressionLeft = (isUnaryOperator) ?  unaryOperatorExpression : parseExpr(null);

				Token binaryOperatorToken = tokens.removeFirst();

				Expression binaryOperatorExpression = new BinaryOperation
						(binaryOperatorToken, binaryOperatorToken.getValue(), expressionLeft, parseExpr(null));

				checkNext(")");

				Expression left = binaryOperatorExpression;

				if (isBinaryExpression() && !peekNext(")")) {

					Token binaryOperatorToken2 = tokens.removeFirst();

					Expression binaryOperatorExpression2 = new BinaryOperation
							(binaryOperatorToken2, binaryOperatorToken2.getValue(), left, parseExpr(null));

					if (peekNext(")") && peekTwoAhead(")")) checkNext(")");

					return binaryOperatorExpression2;

				}

				return binaryOperatorExpression;

			}

			return unaryOperatorExpression;
			// -------------------- Binary Operator -----------------------------------------------------//

		} else if (previous != null && (peekNext("(") || isBinaryExpression())) {

			// -------------------- Binary Operator with previous  -----------------------------------------------------//

			if (peekNext("(")) checkNext("(");

			boolean isBinaryOperatorExpression = isBinaryExpression();

			if (isBinaryOperatorExpression) {

				Expression previous1 = previous;

				Token binaryOperatorToken = tokens.removeFirst();

				if (peekNext("(") && peekTwoAhead("(")) checkNext("(");

				Expression binaryOperatorExpression = new BinaryOperation(binaryOperatorToken, binaryOperatorToken.getValue(), previous1, parseExpr(null));

				checkNext(")");

				while(peekNext(")") && peekTwoAhead(")")) checkNext(")");

				Expression left = binaryOperatorExpression;

				if (isBinaryExpression()) {

					Token binaryOperatorToken2 = tokens.removeFirst();

					Expression binaryOperatorExpression2 = new BinaryOperation
							(binaryOperatorToken2, binaryOperatorToken2.getValue(), left, parseExpr(null));

					checkNext(")");

					return binaryOperatorExpression2;

				}

				return binaryOperatorExpression;

				// -------------------- Binary Operator with previous  -----------------------------------------------------//

			}

			return null;

		} else {

			throw new ParseException("This type of expression does not supported -> " + token.getValue() + " " + token.getRow(), token);

		}

	}

	private boolean isBinaryExpression() {

		for (String operator : Arrays.asList("and", "or", ">", "<", "==", ">=", "<=", "+", "-", "*", "/", "%")) {

			if (peekNext(operator) || peekTwoAhead(operator)) {
				return true;
			}

		}

		return false;
	}

	private boolean isUnaryOperator() {

		for (String operator : Arrays.asList("-", "not")) {

			if (peekNext(operator)) {
				return true;
			}

		}

		return false;

	}

}
