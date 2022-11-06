package splat.lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Lexer {

	private File progFile;

	public Lexer(File progFile) {
		this.progFile = progFile;
	}

	public List<Token> tokenize() throws LexException {

		LinkedList<Token> tokens = new LinkedList<>();

		try {

			Scanner scanner = new Scanner(progFile);

			Stack<Character> quote = new Stack<>();
			StringBuilder operator = new StringBuilder();
			StringBuilder word = new StringBuilder();

			List<Boolean> allowedCharacters = new ArrayList<>(256);

			for (int i = 0; i <= 255; i++) {

				if (i == 34 || (i >= 39 && i <= 62) || (i >= 65 && i <= 90) || i == 95 || (i >= 97 && i <= 123) || i == 125) {
					allowedCharacters.add(i, true);
				} else {
					allowedCharacters.add(i , false);
				}

			}


			List<Character> operators = Arrays.asList('>', '<', '/', '*', '-', '+', '%');
			List<String> complexOperators = Arrays.asList(">=", "<=", "==", "and", "or");
			List<Character> specialCharacters = Arrays.asList('.', ',', ';', ':', '(', ')', '{', '}');

			while (scanner.hasNextLine()) {

				String line = scanner.nextLine();

				String[] separated = line.split(" ");

				for (String e : separated) {

					for (int i = 0; i < e.length(); i++) {

						if (e.charAt(i) == ' ' || e.charAt(i) == '\t') continue;

						if (specialCharacters.contains(e.charAt(i))) {
							addWordOrOperator(operator, word, tokens);
							tokens.add(new Token(e.charAt(i) + "", 0,0));
						} else if (Arrays.asList((char)34, (char)39).contains(e.charAt(i)) && quote.isEmpty()) {
							tokens.add(new Token(e.charAt(i) + "", 0,0));
							quote.push(e.charAt(i));
						} else if (Arrays.asList((char)34, (char)39).contains(e.charAt(i))) {

							if (quote.isEmpty())
								throw new LexException("Closing column does not have opening column", 0, 0);
							else if (e.charAt(i) == (char)34 && quote.peek() != (char)34)
								throw new LexException("Closing column does not match opening column", 0, 0);
							else if (e.charAt(i) == (char)39 && quote.peek() != (char)39)
								throw new LexException("Closing column does not match opening column", 0, 0);
							else {
								tokens.add(new Token(e.charAt(i)+"", 0,0));
							}

							quote.pop();

						} else if ((operators.contains(e.charAt(i)) || e.charAt(i) == '=') && quote.isEmpty()) {

							if (complexOperators.contains(operator.toString())) {
								tokens.add(new Token(operator + "", 0, 0));
								operator.setLength(0);
							}

							operator.append(e.charAt(i));
						} else if (allowedCharacters.get(e.charAt(i))) {  //Character.isLetterOrDigit(e.charAt(i)) || allowed.contains(e.charAt(i))
							word.append(e.charAt(i));
						}  else if (!allowedCharacters.get(e.charAt(i)) && quote.isEmpty()){
							throw new LexException("Not allowed character " + e.charAt(i), 0 ,0);
						}


					}

					addWordOrOperator(operator, word, tokens);



				}

				if (!quote.isEmpty()) throw new LexException("Quote has not been closed", 0 ,0);


			}


		} catch (FileNotFoundException exception) {
			System.out.println(exception);
		}

		return tokens;


	}

	private void addWordOrOperator(StringBuilder operator, StringBuilder word, LinkedList<Token> tokens) throws LexException {

		if (operator.length() > 0) {

			if (operator.toString().equals("=") && !tokens.getLast().getValue().equals(":")) {
				throw new LexException("Equal operator not supported", 0, 0);
			}

			tokens.add(new Token(operator.toString(), 0 ,0));
			operator.setLength(0);
		}

		if (word.length() > 0) {
			if (Character.isDigit(word.charAt(0)) && Character.isAlphabetic(word.charAt(word.length()-1))) {
				throw new LexException("Variable starts with number", 0 ,0);
			}
			tokens.add(new Token(word.toString(), 0 ,0));
			word.setLength(0);
		}
	}

}
