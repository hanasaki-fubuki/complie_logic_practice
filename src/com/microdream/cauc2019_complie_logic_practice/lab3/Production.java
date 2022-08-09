package com.microdream.complie_logic.lab3;

import com.microdream.complie_logic.lab1.TokenType;

public class Production {
    TokenType left;
    TokenType[] right;
    public Production(TokenType left, TokenType[] right) {
        this.left = left;
        this.right = right;
    }
    public TokenType getLeft() {
        return this.left;
    }
    public TokenType[] getRight() {
        return this.right;
    }
}
