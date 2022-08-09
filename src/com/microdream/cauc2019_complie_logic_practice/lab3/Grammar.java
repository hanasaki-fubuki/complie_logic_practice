package com.microdream.complie_logic.lab3;

import java.util.*;
import com.microdream.complie_logic.lab1.TokenType;

public class Grammar {
    public static TokenType startSymbol;
    public static HashMap<Integer,Production> productions;
    static {
        startSymbol = TokenType.Expression;
        productions = new HashMap<Integer,Production>();
        Production p = null;
        //(0)S'->E
        TokenType[] S1 = {TokenType.Expression};
        p = new Production(TokenType.Start, S1);
        productions.put(0, p);
        //(1)E->E + T
        TokenType[] E1 = {TokenType.Expression, TokenType.PLUS, TokenType.Term};
        p = new Production(TokenType.Expression, E1);
        productions.put(1, p);
        //(2)E->T
        TokenType[] E2 = {TokenType.Term};
        p = new Production(TokenType.Expression, E2);
        productions.put(2, p);

        /************************begin******************************/

        //3 T -> T * F
        TokenType[] T1 = {TokenType.Term,TokenType.TIMES,TokenType.Factor};
        p = new Production(TokenType.Term,T1);
        productions.put(3, p);
        //4 T -> F
        TokenType[] T2 = {TokenType.Factor};
        p = new Production(TokenType.Term,T2);
        productions.put(4, p);

        //5 F -> (E)
        TokenType[] F1 = {TokenType.LPAREN,TokenType.Expression,TokenType.RPAREN};
        p = new Production(TokenType.Factor,F1);
        productions.put(5, p);
        //6 F -> id
        TokenType[] F2 = {TokenType.IDENTIFIER};
        p = new Production(TokenType.Factor,F2);
        productions.put(6, p);

        /************************end******************************/

    }
}