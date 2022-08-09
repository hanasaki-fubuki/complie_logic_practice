package com.microdream.complie_logic.lab3;

public class Lab3Main {
    public static void main(String[] args) {
        LR parser = new LR();
        parser.doParse("src/com/microdream/complie_logic/lab3/lab3test1.txt");
    }
}
