package splat.lexer;

public class Token {

    private String value;
    private int row;
    private int column;

    public Token(String value, int row, int column) {
        this.value = value;
        this.row = row;
        this.column = column;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {

        return "Token value: " + value + ". " + "Row: " + row + ". " + "Column: " + column + ".";

    }
}
