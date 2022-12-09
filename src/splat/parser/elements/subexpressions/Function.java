package splat.parser.elements.subexpressions;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.executor.subvalues.BooleanValue;
import splat.executor.subvalues.IntegerValue;
import splat.executor.subvalues.StringValue;
import splat.lexer.Token;
import splat.parser.elements.*;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Function extends Expression {

    private String label;
    private List<Expression> arguments;

    public Function(Token tok) {
        super(tok);
    }

    public Function(Token tok, String label, List<Expression> arguments) {
        super(tok);
        this.label = label;
        this.arguments = arguments;
    }


    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        if (!funcMap.containsKey(this.getLabel())) {
            throw new SemanticAnalysisException
                    ("Called function does not exist", this);
        }

        List<Expression> arguments = this.getArguments();

        FunctionDecl decl = funcMap.get(label);

        if (decl.getParameters() == null && arguments.size() > 0) {
            throw new SemanticAnalysisException("Function does not accept any parameters", this);
        }

        for (int i = 0; i < arguments.size(); i++) {

            if (decl.getParameters().get(i).getType() != arguments.get(i).analyzeAndGetType(funcMap, varAndParamMap)) {
                throw new SemanticAnalysisException("Type of the argument and type of parameter in declaration does not match", this);
            }

        }

        return funcMap.get(this.getLabel()).getReturnType();

    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {

        FunctionDecl decl  = funcMap.get(this.getLabel());

        // ------- Store variables and parameters to track their values

        Map<String, Value> localVarAndParamMap = new HashMap<>();

        List<Expression> arguments = this.getArguments();

        int indexArgument = 0;

        for (VariableDecl var : decl.getParameters()) {

            Value value = arguments.get(indexArgument++).evaluate(funcMap, varAndParamMap);

            if (var.getType() == Type.Integer) {

                localVarAndParamMap.put(var.getLabel(), new IntegerValue(value.getIntegerValue(), Type.Integer));

            } else if (var.getType() == Type.String) {

                localVarAndParamMap.put(var.getLabel(), new StringValue(value.getStringValue(), Type.String));

            } else if (var.getType() == Type.Boolean) {

                localVarAndParamMap.put(var.getLabel(), new BooleanValue(value.getBooleanValue(), Type.Boolean));

            }
        }

        for (VariableDecl var : decl.getVariables()) {

            if (var.getType() == Type.Integer) {

                localVarAndParamMap.put(var.getLabel(), new IntegerValue(0, Type.Integer));

            } else if (var.getType() == Type.String) {

                localVarAndParamMap.put(var.getLabel(), new StringValue("", Type.String));

            } else if (var.getType() == Type.Boolean) {

                localVarAndParamMap.put(var.getLabel(), new BooleanValue(false, Type.Boolean));

            }
        }

        // executing statements of a function

        Value returnValue = null;

        for (Statement stmt : decl.getStatements()) {

            try {

                stmt.execute(funcMap, localVarAndParamMap);

            } catch (ReturnFromCall returnFromCall) {

                returnValue = returnFromCall.getReturnVal();
                break;

            }
        }

        return returnValue;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public String getLabel() {
        return label;
    }
}
