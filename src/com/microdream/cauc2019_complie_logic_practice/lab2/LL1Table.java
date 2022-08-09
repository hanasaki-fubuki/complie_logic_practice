package com.microdream.complie_logic.lab2;

import com.microdream.complie_logic.lab1.TokenType;
import java.util.*;

public class LL1Table {
    HashMap<TokenType, HashMap<TokenType, TokenType[]>> table = null;

    public LL1Table() {
        this.table = new HashMap<TokenType, HashMap<TokenType, TokenType[]>>();
        //select(Simpleblock-> {Sequence}) = {{}
        TokenType[] BP1 = {TokenType.LBRACKET, TokenType.Sequence, TokenType.RBRACKET};
        this.addItem(TokenType.Simpleblock, TokenType.LBRACKET, BP1);
        //select(Sequence -> AssignmentStatement Sequence) = {IDENTIFIER}
        TokenType[] SP1 = {TokenType.assignmentStatement, TokenType.Sequence};
        this.addItem(TokenType.Sequence, TokenType.IDENTIFIER, SP1);
        //select(Sequence -> epsilon) = {}}
        TokenType[] SP2 = {TokenType.Epsilon};
        this.addItem(TokenType.Sequence, TokenType.RBRACKET, SP2);

        /***********************begin***************************/

        //select ( assignmentStatement  ¡ú  IDENTIFIER = expression ; ) = {IDENTIFIER}
        TokenType[] AP1 = {
                TokenType.IDENTIFIER, TokenType.ASSIGN, TokenType.Expression, TokenType.SEMICOLON
        };
        this.addItem(TokenType.assignmentStatement, TokenType.IDENTIFIER, AP1);

        // select ( Expression	¡ú  term expression_1 ) = {(, ID, NUM}
        TokenType[] EP1 = {
                TokenType.Term, TokenType.Expression_1
        };
        this.addItem(TokenType.Expression, TokenType.LPAREN, EP1);
        this.addItem(TokenType.Expression, TokenType.IDENTIFIER, EP1);
        this.addItem(TokenType.Expression, TokenType.NUMBER_LITERAL, EP1);

        //Expression_1 ¡ú + term expression_1
        TokenType[] ET= {TokenType.PLUS,TokenType.Term,TokenType.Expression_1};
        this.addItem(TokenType.Expression_1, TokenType.PLUS, ET);

        //Expression_1 ¡ú - term expression_1
        TokenType[] ET2= {TokenType.MINUS,TokenType.Term,TokenType.Expression_1};
        this.addItem(TokenType.Expression_1, TokenType.MINUS, ET2);

        //Expression_1 ¡ú ¦Å
        TokenType[] E1= {TokenType.Epsilon};
        this.addItem(TokenType.Expression_1, TokenType.SEMICOLON, E1);
        this.addItem(TokenType.Expression_1, TokenType.RPAREN, E1);

        //Term ¡ú factor term_1
        TokenType[] TF= {TokenType.Factor,TokenType.Term_1};
        this.addItem(TokenType.Term, TokenType.LPAREN, TF);
        this.addItem(TokenType.Term, TokenType.IDENTIFIER, TF);
        this.addItem(TokenType.Term, TokenType.NUMBER_LITERAL, TF);

        //Term_1 ¡ú * factor term_1
        TokenType[] TF1= {TokenType.TIMES,TokenType.Factor,TokenType.Term_1};
        this.addItem(TokenType.Term_1, TokenType.TIMES, TF1);

        //Term_1 ¡ú / factor term_1
        TokenType[] TF2= {TokenType.DIVIDE,TokenType.Factor,TokenType.Term_1};
        this.addItem(TokenType.Term_1, TokenType.DIVIDE, TF2);

        //Term_1 ¡ú % factor term_1
        TokenType[] TF3= {TokenType.REMAINDER,TokenType.Factor,TokenType.Term_1};
        this.addItem(TokenType.Term_1, TokenType.REMAINDER, TF3);

        // select ( Term_1 	¡ú ¦Å ) = { +  -  ;  )  }
        TokenType[] TF4 = {
                TokenType.Epsilon
        };
        this.addItem(TokenType.Term_1, TokenType.PLUS, TF4);
        this.addItem(TokenType.Term_1, TokenType.MINUS, TF4);
        this.addItem(TokenType.Term_1, TokenType.SEMICOLON, TF4);
        this.addItem(TokenType.Term_1, TokenType.RPAREN, TF4);

        // select ( Factor	¡ú  ( expression ) ) = { ( }
        TokenType[] FP1 = {
                TokenType.LPAREN, TokenType.Expression, TokenType.RPAREN
        };
        this.addItem(TokenType.Factor, TokenType.LPAREN, FP1);

        // select ( Factor	¡ú  IDENTIFIER ) = { ID }
        TokenType[] FP2 = {
                TokenType.IDENTIFIER
        };
        this.addItem(TokenType.Factor, TokenType.IDENTIFIER, FP2);

        // select ( Factor	¡ú  NUMBER_LITERAL ) = { NUM }
        TokenType[] FP3 = {
                TokenType.NUMBER_LITERAL
        };
        this.addItem(TokenType.Factor, TokenType.NUMBER_LITERAL, FP3);

        /***********************end***************************/

    }

    private void addItem(TokenType row, TokenType column, TokenType[] list) {
        HashMap<TokenType, TokenType[]> map;
        map = this.table.get(row);
        if (map == null) map = new HashMap<TokenType, TokenType[]>();
        map.put(column, list);
        this.table.put(row, map);
    }

    public TokenType[] getItem(TokenType row, TokenType column) {
        HashMap<TokenType, TokenType[]> tmp = this.table.get(row);
        if (tmp == null) return null;
        TokenType[] list = tmp.get(column);
        return list;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (TokenType row : this.table.keySet()) {
            for (TokenType column : this.table.get(row).keySet()) {
                buffer.append("(" + row + "," + column + ") = " + this.getItem(row, column));
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }
}