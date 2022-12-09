package splat.executor;

import java.util.HashMap;
import java.util.Map;
import splat.executor.subvalues.BooleanValue;
import splat.executor.subvalues.IntegerValue;
import splat.executor.subvalues.StringValue;
import splat.parser.elements.*;

public class Executor {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap;
	private Map<String, Value> progVarMap;
	
	public Executor(ProgramAST progAST) {
		this.progAST = progAST;
		this.funcMap = new HashMap<>();
		this.progVarMap = new HashMap<>();
	}

	public void runProgram() throws ExecutionException {

		// This sets the maps that will be needed for executing function 
		// calls and storing the values of the program variables
		setMaps();
		
		try {
			
			// Go through and execute each of the statements
			for (Statement stmt : progAST.getStmts()) {
				stmt.execute(funcMap, progVarMap);
			}
			
		// We should never have to catch this exception here, since the
		// main program body cannot have returns
		} catch (ReturnFromCall ex) {
			System.out.println("Internal error!!! The main program body "
					+ "cannot have a return statement -- this should have "
					+ "been caught during semantic analysis!");
			
			throw new ExecutionException("Internal error -- fix your "
					+ "semantic analyzer!", -1, -1);
		}
	}
	
	private void setMaps() {

		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel();

			if (decl instanceof FunctionDecl) {

				funcMap.put(label, (FunctionDecl)decl);

			} else if (decl instanceof VariableDecl) {

				if (((VariableDecl) decl).getType() == Type.Integer) {

					progVarMap.put(label, new IntegerValue(0, Type.Integer));

				} else if (((VariableDecl) decl).getType() == Type.String) {

					progVarMap.put(label, new StringValue("", Type.String));

				} else if (((VariableDecl) decl).getType() == Type.Boolean) {

					progVarMap.put(label, new BooleanValue(false, Type.Boolean));

				}

			}

		}

	}

}
