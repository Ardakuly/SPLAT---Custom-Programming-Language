package splat.semanticanalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import splat.parser.elements.Declaration;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.parser.elements.VariableDecl;
import splat.parser.elements.substatements.Return;

public class SemanticAnalyzer {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap = new HashMap<>(); //declaration
	private Map<String, Type> progVarMap = new HashMap<>(); //type
	
	public SemanticAnalyzer(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void analyze() throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// for our program functions and variables 
		checkNoDuplicateProgLabels(); //checking declaration of function on duplicates
		
		// This sets the maps that will be needed later when we need to
		// typecheck variable references and function calls in the 
		// program body
		setProgVarAndFuncMaps();
		
		// Perform semantic analysis on the functions
		for (FunctionDecl funcDecl : funcMap.values()) {	
			analyzeFuncDecl(funcDecl);
		}
		
		// Perform semantic analysis on the program body
		for (Statement stmt : progAST.getStmts()) {
			stmt.analyze(funcMap, progVarMap);
		}
		
	}

	private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// among our function parameters, local variables, and function names
		checkNoDuplicateFuncLabels(funcDecl);
		
		// Get the types of the parameters and local variables
		Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl);
		
		// Perform semantic analysis on the function body
		Statement returnStatement = null;

		for (Statement stmt : funcDecl.getStatements()) {
			if (stmt.getClass() == Return.class) returnStatement = stmt;
			stmt.analyze(funcMap, varAndParamMap);
		}

		if (varAndParamMap.get("0return") != Type.Void && returnStatement == null) {
			throw new SemanticAnalysisException("Function need return statement", funcDecl);
		}

	}
	
	
	private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) {
		
		// FIXME: Somewhat similar to setProgVarAndFuncMaps()
		Map<String, Type> varAndParamMap = new HashMap<String, Type>();


		// Storing info about parameters of a function
		for (VariableDecl decl : funcDecl.getParameters()) {
			varAndParamMap.put(decl.getLabel(), decl.getType());
		}

		// Storing info about variables of a function
		for (VariableDecl decl : funcDecl.getVariables()) {
			varAndParamMap.put(decl.getLabel(), decl.getType());
		}

		// Storing info about return of a function
		varAndParamMap.put("0return", funcDecl.getReturnType());

		return varAndParamMap;
	}

	private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl) 
									throws SemanticAnalysisException {
		
		// FIXME: Similar to checkNoDuplicateProgLabels()

		// checking for duplicate parameters

		Set<String> duplicate = new HashSet<String>();

		for (VariableDecl decl : funcDecl.getParameters()) {

			String label = decl.getLabel();

			if (duplicate.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in parameters of a function", decl);
			} else {
				duplicate.add(label);
			}
		}

		// checking for duplicate local variables

		for (VariableDecl decl : funcDecl.getVariables()) {

			String label = decl.getLabel();

			if (duplicate.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in local variables of a function", decl);
			} else {
				duplicate.add(label);
			}
		}

		// checking if parameters clashes with functions name

		for (VariableDecl decl : funcDecl.getParameters()) {

			String label =  decl.getLabel();

			if (funcMap.containsKey(label)) {
				throw new SemanticAnalysisException("Cannot have label with the same name as program function '"
						+ label + "' in parameters of a function", decl);
			}
		}

		// checking if local variables clashes with functions name

		for (VariableDecl decl : funcDecl.getVariables()) {

			String label =  decl.getLabel();

			if (funcMap.containsKey(label)) {
				throw new SemanticAnalysisException("Cannot have label with the same name as program function '"
						+ label + "' in local variables of a function", decl);
			}
		}

	}
	
	private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {
		
		Set<String> labels = new HashSet<String>();
		
 		for (Declaration decl : progAST.getDecls()) {
 			String label = decl.getLabel().toString();
 			
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in program", decl);
			} else {
				labels.add(label);
			}
			
		}

	}
	
	private void setProgVarAndFuncMaps() {
		
		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel().toString();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);
				
			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				progVarMap.put(label, varDecl.getType());
			}
		}
	}
}
