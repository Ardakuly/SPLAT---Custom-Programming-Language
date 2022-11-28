package splat.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import splat.lexer.Token;
import splat.parser.elements.*;
import splat.parser.elements.primitiveDataType.DataType;
import splat.parser.elements.subexpressions.*;
import splat.parser.elements.substatements.*;

public class Parser {

	private LinkedList<Token> tokens;
	private final Pattern namePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
	private final Pattern integerPattern = Pattern.compile("^[1-9][0-9]*$");
	private List<Statement> statements = new ArrayList<>();
	private Set<String> varExist = new HashSet<>();
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

			System.out.println(tokens.getFirst() + "-----> parse");

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

		if (!peekNext(")")) {

			List<VariableDecl> parameters = new ArrayList<>();

			do {
				if (peekNext(",")) checkNext(",");

				parameters.add(parseVarDecl());

			} while (peekNext(","));

			functionDecl.setParameters(parameters);

		}

		checkNext(")"); // remove ')'

		//------------------ Parameters--------------------//

		checkNext(":"); //remove ':'

		// ----------------- Return Type -------------------//

		Token returnType = tokens.removeFirst(); // Take return statement

		if (DataType.String.name().equals(returnType.getValue()) ) {
			functionDecl.setReturnType(DataType.String);
		} else if (DataType.Integer.name().equals(returnType.getValue())) {
			functionDecl.setReturnType(DataType.Integer);
		} else if (DataType.Boolean.name().equals(returnType.getValue())) {
			functionDecl.setReturnType(DataType.Boolean);
		}else if ("void".equals(returnType.getValue())) {
			functionDecl.setReturnType(DataType.Boolean);
		} else {
			throw new ParseException("ReturnType for variable does not meet requirements --->" + returnType.getValue(), returnType);
		}

		// ----------------- Return Type -------------------//

		checkNext("is"); //remove is

		if (!peekNext("begin")) {

			do {
				functionDecl.getVariables().add(parseVarDecl());

			} while (!tokens.getFirst().getValue().equals("begin") );

//			checkNext(";"); // remove ';'
    	}

		checkNext("begin");

		if (!peekNext("end")) {

			functionDecl.setStatements(new ArrayList<>(parseStmts(new ArrayList<>())));

		}

		System.out.println(tokens.getFirst() + "-----> ");

		statements = new ArrayList<>();



		checkNext("end"); //remove 'end'
		checkNext(";"); //remove ';'

		System.out.println(tokens.getFirst() + "-----> ");

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

		varExist.add(labelToken.getValue());

		tokens.removeFirst(); // remove ":"

		Token typeToken = tokens.removeFirst();

		VariableDecl varDecl = new VariableDecl(typeToken);

		Matcher matcher = namePattern.matcher(labelToken.getValue());

		if (matcher.matches()) {
			varDecl.setLabel(labelToken.getValue());
		} else {
			throw new ParseException("Variable name does not meet requirements", labelToken);
		}

		if (DataType.String.name().equals(typeToken.getValue()) ) {
			varDecl.setType(DataType.String);
		} else if (DataType.Integer.name().equals(typeToken.getValue())) {
			varDecl.setType(DataType.Integer);
		} else if (DataType.Boolean.name().equals(typeToken.getValue())) {
			varDecl.setType(DataType.Boolean);
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
			statements = parseStmt(statements);
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

			checkNext("("); //remove '('

			ifStatement.setExpressions(parseExpr());

			System.out.println(tokens.getFirst());

			checkNext(")"); //remove '('
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

		} else if (peekNext("print")) {

			Token printToken = tokens.removeFirst();

			Print printStatement = new Print(printToken);

			if (peekNext("(")) checkNext("(");

			if (peekTwoAhead(")")) throw new ParseException("Invalid use of parenthesis", tokens.getFirst());

			printStatement.setExpressions(parseExpr());

			if (peekNext(")")) checkNext(")");

			checkNext(";");

			return statements;

		} else if (peekNext("print_line")) {

			Token printLineToken = tokens.removeFirst();

			Print_line printLineStatement = new Print_line(printLineToken);

			checkNext(";"); //remove ';'

			return statements;

		} else if (peekNext("return")) {

			Token returnToken = tokens.removeFirst();

			Return returnStatement = new Return(returnToken);

			if (peekNext("(")) checkNext("(");

			if (!peekNext(";")) returnStatement.setExpressions(parseExpr());

			if (peekNext(")")) checkNext(")");

			checkNext(";"); //remove ';'

			return statements;

		} else if (peekNext("while")) {

			Token whileToken = tokens.removeFirst();

			WhileLoop whileLoopStatement = new WhileLoop(whileToken);

			checkNext("(");

			whileLoopStatement.setExpression(parseExpr());

			checkNext(")");
			checkNext("do");

			whileLoopStatement.setStatements(parseStmt(new ArrayList<>()));

			checkNext("end");
			checkNext("while");
			checkNext(";");

			statements.add(whileLoopStatement);

			parsed = true;

		} else if (isValid && !peekTwoAhead("(")){

			Token initializationToken = tokens.removeFirst();

//			if (!varExist.contains(initializationToken.getValue())) {
//				throw new ParseException("Variable has not been inirialized ---> " + initializationToken.getValue(), initializationToken);
//			}

			Initialization initializationStatement =
					new Initialization(initializationToken);

			initializationStatement.setLabel(initializationToken.getValue());

			checkNext(":");
			checkNext("=");

//			boolean exist = false;
//
//			for (String declaration : declarations) {
//				if (declaration.equals(initializationStatement.getLabel())) {
//					exist = true; break;
//				}
//			}

//			if (!exist) throw new ParseException("Variable does not exist", initializationToken);

			if (peekNext("(")) checkNext("(");

			initializationStatement.setLiteral(parseExpr());

			if (peekNext(")")) checkNext(")");

			checkNext(";");

			parsed = true;

		} else if (isValid && peekTwoAhead("(")) {

			Token functionCallToken = tokens.removeFirst();

			FunctionCall functionCallStatement = new FunctionCall(functionCallToken);

			checkNext("(");

			functionCallStatement.setLabel(functionCallToken.getValue());


			if (!peekNext(")")) {
				functionCallStatement.setParameters(parseExpr());
			}

			checkNext(")");

			checkNext(";");

			statements.add(functionCallStatement);

			parsed = true;

		}

		if (!parsed) throw new ParseException("Bad statement exception", tokens.removeFirst());

		return statements;

	}


	private List<Expression> parseExpr() throws ParseException {

		LinkedList<Expression> expressions = new LinkedList<>();

		while ((!peekNext(")") && (!peekTwoAhead("then"))) && (!peekNext(")") && (!peekTwoAhead("do")))
				 && (!peekNext(";"))) { //&& ((!peekNext(")") && !peekTwoAhead(";")))

			if (peekNext(",")) checkNext(",");

			boolean openScope = false;
			boolean closeScope = false;

			openScope = (peekNext("(")) ? true : false;
			if (openScope) checkNext("(");
			closeScope = (peekTwoAhead(")")) ? true : false;

			boolean executed = false;

			for (String operator : Arrays.asList("and", "or", ">", "<", "==", ">=", "<=", "+", "-", "*", "/", "%")) {

				if (peekNext(operator) && expressions.size() > 0) {

					if (Arrays.asList(BinaryOperation.class, BinaryOperation.class)
							.contains(expressions.getLast().getClass())) {
						throw new ParseException("Invalid use case of expression", tokens.getFirst());
					} else if (Arrays.asList("false", "true").contains(expressions.getLast().getToken().getValue())) {
						throw new ParseException("Invalid use case of expression", tokens.getFirst());
					}

					Token binaryOperatorToken = tokens.removeFirst();

					BinaryOperation binaryOperator = new BinaryOperation(binaryOperatorToken);

					binaryOperator.setOperator(binaryOperatorToken.getValue());

					binaryOperator.setToken(binaryOperatorToken);

					expressions.add(binaryOperator);

					executed = true;

					break;

				}

			}

			if (executed) continue;

			for (String operator : Arrays.asList("not", "-")) {

				if (peekNext(operator)) {

//					if (expressions.size() > 0) {
//						if (expressions.getLast().getClassType() != BinaryOperation.class) {
//							throw new ParseException("Not valid expression use case" + tokens.getFirst().getRow(), tokens.getFirst());
//						}
//					}

					Token unaryOperatorToken = tokens.removeFirst();

					UnaryOperator unaryOperator = new UnaryOperator(unaryOperatorToken);

					String value = (openScope) ? "(" + unaryOperatorToken.getValue() : unaryOperatorToken.getValue();

					if (closeScope) throw new ParseException("Unary Operator does not have operand", unaryOperatorToken);

					openScope = false;

					unaryOperator.setOperator(value);

					unaryOperator.setToken(unaryOperatorToken);

					expressions.add(unaryOperator);

					executed = true;

					break;

				}

			}

			if (executed) continue;

			Matcher matcherInteger = integerPattern.matcher(tokens.getFirst().getValue());

			if (tokens.getFirst().getValue().startsWith("" + (char)34) && tokens.getFirst().getValue().endsWith("" + (char)34)) {

//				if (expressions.size() > 0) {
//					if (expressions.getLast().getClass() == BinaryOperation.class) {
//						throw new ParseException("Invalid use case of expression", tokens.getFirst());
//					}
//				}

				Token stringToken = tokens.removeFirst();
				Literal string = new Literal(stringToken);
				string.setType(DataType.String.name());

				String word = (openScope) ? "(" + stringToken.getValue() : stringToken.getValue();

				if ((closeScope && !peekTwoAhead("then"))
						&& (closeScope && !peekTwoAhead("do"))
						&& (closeScope && !peekTwoAhead(";"))){
					word =  word + ")";
					checkNext(")");
				}

				string.setValue(word);
				string.setToken(stringToken);

				openScope = false;
				closeScope = false;
				expressions.add(string);

				executed = true;
			} else if (matcherInteger.find() || tokens.getFirst().getValue().equals("0")) {

//				if (expressions.size() > 0) {
//					if (expressions.getLast().getClassType() != BinaryOperation.class &&
//							expressions.getLast().getClassType() != UnaryOperator.class &&
//							expressions.getLast().getClassType() != Label.class) {
//						throw new ParseException("Not valid expression use case" + tokens.getFirst().getRow(), tokens.getFirst());
//					}
//				}

				Token integerToken = tokens.removeFirst();
				Literal integer = new Literal(integerToken);
				integer.setType(DataType.Integer.name());

				String value = (openScope) ? "(" + integerToken.getValue() : integerToken.getValue();

				if ((closeScope && !peekTwoAhead("then"))
						&& (closeScope && !peekTwoAhead("do"))
						&& (closeScope && !peekTwoAhead(";"))){
					value =  value + ")";
					checkNext(")");
				}

				integer.setValue(value);

				openScope = false;
				closeScope = false;

				integer.setToken(integerToken);
				expressions.add(integer);

				executed = true;
			} else if (tokens.getFirst().getValue().equals("true") || tokens.getFirst().getValue().equals("false")) {

//				if (expressions.size() != 0) {
//					if (expressions.getLast().getClassType() != BinaryOperation.class &&
//						expressions.getLast().getClassType() != UnaryOperator.class &&
//						expressions.getLast().getClassType() != Label.class) {
//					throw new ParseException("Not valid expression use case" + tokens.getFirst().getRow(), tokens.getFirst());
//				}

				Token booleanToken = tokens.removeFirst();
				Literal booleaN = new Literal(booleanToken);
				booleaN.setType(DataType.Boolean.name());

				String value = (openScope) ? "(" + booleanToken.getValue() : booleanToken.getValue();

				if ((closeScope && !peekTwoAhead("then"))
						&& (closeScope && !peekTwoAhead("do"))
						&& (closeScope && !peekTwoAhead(";"))){
					value =  value + ")";
					checkNext(")");
				}
				booleaN.setValue(value);

				booleaN.setToken(booleanToken);

				openScope = false;
				closeScope = false;
				expressions.add(booleaN);

				executed = true;
			}

			if (executed) continue;

			Matcher labelMatcher = namePattern.matcher(tokens.getFirst().getValue());

			if (labelMatcher.find()) {

				Token labelToken = tokens.removeFirst();

				Label label = new Label(labelToken);

				String name = (openScope) ? "(" + labelToken.getValue() : labelToken.getValue();

				if ((closeScope && !peekTwoAhead("then"))
						&& (closeScope && !peekTwoAhead("do"))
						&& (closeScope && !peekTwoAhead(";"))){
					name =  name + ")";
					checkNext(")");
				}

				label.setLabel(name);

				label.setToken(labelToken);

				openScope = false;
				closeScope = false;

				expressions.add(label);

				executed = true;
			}

			if (executed) continue;


		}

		if (expressions.size() == 0) throw new ParseException("This type of expression does not exist --> "
				+ tokens.getFirst().getValue() + " " + tokens.getFirst().getRow(), tokens.removeFirst());

		return expressions;


	}

//	String name = tokens.getFirst().getValue();
//
//		System.out.println(name);
//
//	boolean isBinaryOperator = false;
//
//		for (String operator : Arrays.asList("and", "or", ">", "<", "==", ">=", "<=", "+", "-", "*", "/", "%")) {
//
//		if (peekNext(operator)) {
//			isBinaryOperator = true;
//			break;
//		}
//
//	}
//
//	boolean isUnaryOperator = false;
//
//		for (String operator : Arrays.asList("not", "-")) {
//
//		if (peekNext(operator)) {
//			isUnaryOperator = true;
//			break;
//		}
//
//	}
//
//	boolean isLiteral = false;
//	String type =  null;
//	Matcher matcherInteger = integerPattern.matcher(tokens.getFirst().getValue());
//
//
//		if (tokens.getFirst().getValue().startsWith("" + (char)34) && tokens.getFirst().getValue().endsWith("" + (char)34)) {
//		isLiteral = true;
//		type = "String";
//	} else if (matcherInteger.find()) {
//		isLiteral = true;
//		type = "Integer";
//	} else if (tokens.getFirst().getValue().equals("true") || tokens.getFirst().getValue().equals("false")) {
//		isLiteral = true;
//		type = "Boolean";
//	}
//
//	Matcher matcherLabel = namePattern.matcher(tokens.getFirst().getValue());
//
//
//		if (isBinaryOperator) {
//
//		Token binaryOperatorToken = tokens.removeFirst();
//
//		BinaryOperation binaryOperator = new BinaryOperation(binaryOperatorToken);
//
//		binaryOperator.setOperator(binaryOperatorToken.getValue());
//
//		binaryOperator.setExpression(parseExpr(binaryOperator));
//
//		return binaryOperator;
//
//	} else if (isUnaryOperator) {
//
//		Token unaryOperatorToken = tokens.removeFirst();
//
//		UnaryOperator unaryOperator = new UnaryOperator(unaryOperatorToken);
//
//		unaryOperator.setOperator(unaryOperatorToken.getValue());
//
//		unaryOperator.setExpression(parseExpr(unaryOperator));
//
//		return unaryOperator;
//
//	} else if (isLiteral) {
//
//		Token literalToken = tokens.removeFirst();
//
//		Literal literal = new Literal(literalToken);
//
//		literal.setType(type);
//
////			if (peekNext(";")) checkNext(";");
////			if (peekNext(")")) checkNext(")");
//
//		return literal;
//
//	} else if (matcherLabel.find()) {
//
//		Token labelToken = tokens.removeFirst();
//
//		Label label = new Label(labelToken);
//
////			if (peekNext(")")) checkNext(")");
////
//		if (!peekNext(")")) label.setExpression(parseExpr(label));
////
////			if (peekNext(";")) checkNext(";");
//
//		return label;
//	}
//
//		throw new ParseException("This type of expression does not exist", tokens.removeFirst());



//	private Statement while_statement() throws ParseException {
//
//		Token keyword = tokens.removeFirst();
//
//		checkNext("(");
//
//		BinaryOperation binaryOperation = new BinaryOperation(tokens.peekFirst(), tokens.removeFirst().toString(),
//				tokens.removeFirst().toString(), tokens.removeFirst().toString());
//
//		checkNext(")");
//
//		checkNext("do");
//
//		WhileLoop statement = new WhileLoop(keyword);
//		statement.setExpression(binaryOperation);
//
//		while(!peekNext("end")) {
//			statement.setStatements(parseStmts());
//		}
//
//		checkNext("while");
//		checkNext(";");
//
//		return statement;
//
//
//	}
//
//	private Statement return_statement() throws ParseException {
//
//		Token keyword = tokens.removeFirst();
//
//		Statement statement = new Return(keyword, tokens.removeFirst().toString());
//
//		checkNext(";");
//
//		return statement;
//	}
//
//	private Statement print_line() {
//
//		Token keyword = tokens.removeFirst();
//
//		Statement statement = new Print_line(keyword, tokens.removeFirst().toString());
//
//		return statement;
//
//	}
//
//	private Statement print() {
//
//		Token keyword = tokens.removeFirst();
//
//		Statement statement = new Print(keyword, tokens.removeFirst().toString());
//
//		return statement;
//
//	}
//
//	private Statement ifthenelse() throws ParseException {
//
//		Token keyword = tokens.removeFirst();
//
//		checkNext("("); //remove '('
//
//		BinaryOperation binaryOperation = new BinaryOperation(tokens.peekFirst(), tokens.removeFirst().toString(),
//				tokens.removeFirst().toString(), tokens.removeFirst().toString());
//
//
//		checkNext(")"); //remove ')'
//
//		checkNext("then"); // remove "then" keyword
//
//		IfThenElseEndIf statement = new IfThenElseEndIf(keyword);
//
//		while (!peekNext("end")) {
//
//			if (peekNext("print")) {
//				print();
//			} else if (peekNext("print_line")) {
//				print_line();
//			} else if (peekNext("return")) {
//				return_statement();
//			} else if (peekNext("while")) {
//				while_statement();
//			} else if (peekNext("else")) {
//
//
//			}
//
//		}
//
//
//	}


}
