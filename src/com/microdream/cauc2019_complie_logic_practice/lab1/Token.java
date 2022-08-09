package com.microdream.complie_logic.lab1;

public class Token {
    private TokenType type;
    private String token;
    private int line;
    private int column;
    public Token(TokenType type, String token, int line, int column) {
        this.type = type;
        this.token = token;
        this.line = line;
        this.column = column;
    }
    public TokenType getType() {
        return type;
    }
    public int getLine(){
        return line;
    }
    public int getColumn(){
        return column;
    }
    public String getLexeme(){
        return token;
    }
    public String toString() {
        return type + " " + token + " (" + line + ", " + column + ")";
    }
}
