package com.microdream.complie_logic.lab1;

public class Lab1Main {
    public static void main(String args[]) {
        BlockLexer l = new BlockLexer("src/com/microdream/complie_logic/lab1/lab1test1.txt");
        Token s = l.nextToken();
        while (s != null && s.getType() != TokenType.EOF) {
            System.out.println(s);
            s = l.nextToken();
        }
    }
}

