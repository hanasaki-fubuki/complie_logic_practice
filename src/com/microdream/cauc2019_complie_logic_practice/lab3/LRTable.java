package com.microdream.complie_logic.lab3;

import java.util.*;
import com.microdream.complie_logic.lab1.TokenType;

public class LRTable {
    private static HashMap<Integer, HashMap<TokenType, LRTableEntry>> table = null;
    static {
        table = new HashMap<Integer, HashMap<TokenType, LRTableEntry>>();
        addItem(0, TokenType.IDENTIFIER, new LRTableEntry('s',5));
        addItem(0, TokenType.LPAREN, new LRTableEntry('s',4));
        addItem(0, TokenType.Expression, new LRTableEntry('g',1));
        addItem(0, TokenType.Term, new LRTableEntry('g',2));
        addItem(0, TokenType.Factor, new LRTableEntry('g',3));
        addItem(1, TokenType.PLUS, new LRTableEntry('s',6));
        addItem(1, TokenType.EOF, new LRTableEntry('a',0));
        addItem(2, TokenType.TIMES, new LRTableEntry('s',7));
        addItem(2, TokenType.PLUS, new LRTableEntry('r',2));
        addItem(2, TokenType.RPAREN, new LRTableEntry('r',2));
        addItem(2, TokenType.EOF, new LRTableEntry('r',2));

        /************************begin******************************/

        addItem(3, TokenType.PLUS, new LRTableEntry('r', 4));
        addItem(3, TokenType.TIMES, new LRTableEntry('r', 4));
        addItem(3, TokenType.RPAREN, new LRTableEntry('r', 4));
        addItem(3, TokenType.EOF, new LRTableEntry('r', 4));

        addItem(4, TokenType.IDENTIFIER, new LRTableEntry('s', 5));
        addItem(4, TokenType.LPAREN, new LRTableEntry('s', 4));
        addItem(4, TokenType.Expression, new LRTableEntry('g', 8));
        addItem(4, TokenType.Term, new LRTableEntry('g', 2));
        addItem(4, TokenType.Factor, new LRTableEntry('g', 3));

        addItem(5, TokenType.PLUS, new LRTableEntry('r', 6));
        addItem(5, TokenType.TIMES, new LRTableEntry('r', 6));
        addItem(5, TokenType.RPAREN, new LRTableEntry('r', 6));
        addItem(5, TokenType.EOF, new LRTableEntry('r', 6));

        addItem(6, TokenType.IDENTIFIER, new LRTableEntry('s', 5));
        addItem(6, TokenType.LPAREN, new LRTableEntry('s', 4));
        addItem(6, TokenType.Term, new LRTableEntry('g', 9));
        addItem(6, TokenType.Factor, new LRTableEntry('g', 3));

        addItem(7, TokenType.IDENTIFIER, new LRTableEntry('s', 5));
        addItem(7, TokenType.LPAREN, new LRTableEntry('s', 4));
        addItem(7, TokenType.Factor, new LRTableEntry('g', 10));

        addItem(8, TokenType.PLUS, new LRTableEntry('s', 6));
        addItem(8, TokenType.RPAREN, new LRTableEntry('s', 11));

        addItem(9, TokenType.PLUS, new LRTableEntry('r', 1));
        addItem(9, TokenType.TIMES, new LRTableEntry('s', 7));
        addItem(9, TokenType.RPAREN, new LRTableEntry('r', 1));
        addItem(9, TokenType.EOF, new LRTableEntry('r', 1));

        addItem(10, TokenType.PLUS, new LRTableEntry('r', 3));
        addItem(10, TokenType.TIMES, new LRTableEntry('r', 3));
        addItem(10, TokenType.RPAREN, new LRTableEntry('r', 3));
        addItem(10, TokenType.EOF, new LRTableEntry('r', 3));

        addItem(11, TokenType.PLUS, new LRTableEntry('r', 5));
        addItem(11, TokenType.TIMES, new LRTableEntry('r', 5));
        addItem(11, TokenType.RPAREN, new LRTableEntry('r', 5));
        addItem(11, TokenType.EOF, new LRTableEntry('r', 5));

        /************************end******************************/

    }
    private static void addItem(int row, TokenType column, LRTableEntry entry) {
        HashMap<TokenType, LRTableEntry> tmp = null;
        tmp = table.get(row);
        if(tmp == null) tmp = new HashMap<TokenType, LRTableEntry>();
        tmp.put(column, entry);
        table.put(row, tmp);
    }
    public static LRTableEntry get(int row, TokenType column) {
        HashMap<TokenType, LRTableEntry> tmp = null;
        tmp = table.get(row);
        if(tmp == null) return null;
        return tmp.get(column);
    }
}
