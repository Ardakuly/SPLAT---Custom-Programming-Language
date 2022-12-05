package splat.parser.elements.subexpressions;

import splat.lexer.Token;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.Type;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.Arrays;
import java.util.Map;

public class BinaryOperation extends Expression {

    private String operator;
    private Expression expressionLeft;
    private Expression expressionRight;

    public BinaryOperation(Token tok) {
        super(tok);
    }

    public BinaryOperation(Token tok, String operator, Expression expressionLeft, Expression expressionRight) {
        super(tok);
        this.operator = operator;
        this.expressionLeft = expressionLeft;
        this.expressionRight = expressionRight;
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        Type expressionLeftType = this.getExpressionLeft().analyzeAndGetType(funcMap, varAndParamMap);
        Type expressionRightType = this.getExpressionRight().analyzeAndGetType(funcMap, varAndParamMap);
        Type returnType = null;

        if (Arrays.asList(">", "<", ">=", "<=").contains(this.getOperator())) {
            if (expressionLeftType != Type.Integer && expressionRightType != Type.Integer) {
                throw new SemanticAnalysisException("Operands type for this type of operator must be Integer", this);
            }
            returnType = Type.Boolean;
        }

        if (Arrays.asList("-", "*", "/", "%").contains(this.getOperator())) {
            if (expressionLeftType != Type.Integer && expressionRightType != Type.Integer) {
                throw new SemanticAnalysisException("Operands type for this type of operator must be Integer", this);
            }
            returnType = Type.Integer;
        }

        if (Arrays.asList("and", "or").contains(this.getOperator())) {
            if (expressionLeftType != Type.Boolean && expressionRightType != Type.Boolean) {
                throw new SemanticAnalysisException("Operands type for this type of operator must be Boolean", this);
            }
            returnType = Type.Boolean;
        }

        if (this.getOperator().equals("+")) {
            if (expressionLeftType == Type.Integer && expressionRightType == Type.Integer) {
                returnType = Type.Integer;
            } else if (expressionLeftType == Type.String && expressionRightType == Type.String) {
                returnType = Type.String;
            } else if ((expressionLeftType == Type.String && expressionRightType == Type.Integer)
                    || (expressionLeftType == Type.Integer && expressionRightType == Type.String)) {
                returnType = Type.String;
            } else {
                throw new SemanticAnalysisException("Operands type for this type of operator must be String or Integer", this);
            }
        }

        if (this.getOperator().equals("==")) {
            returnType = Type.Boolean;

            if (expressionLeftType != expressionRightType) {
                throw new SemanticAnalysisException("Operands type for this type of operator ('==') must match", this);
            }
        }

        return returnType;

    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Expression getExpressionLeft() {
        return expressionLeft;
    }

    public void setExpressionLeft(Expression expressionLeft) {
        this.expressionLeft = expressionLeft;
    }

    public Expression getExpressionRight() {
        return expressionRight;
    }

    public void setExpressionRight(Expression expressionRight) {
        this.expressionRight = expressionRight;
    }
}
