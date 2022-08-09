package com.microdream.complie_logic.lab5;

import com.microdream.complie_logic.lab1.*;
import java.util.*;

/**
 * SimpleBlock 语言的递归下降分析器.
 */
public class RecursionDescendParser {
    private BlockLexer lexer = null;
    private Token lookAhead = null;
    private static int CNT;
    private static int nextstm;
    private ArrayList<String> TACList; //存放 TAC 的列表
    public RecursionDescendParser() {}
    public void doParse(String filePath){
        lexer = new BlockLexer(filePath);
        CNT = 0;
        TACList = new ArrayList<String>();
        this.parse();
    }
    private void printTAC() {
        for(int i=0; i<this.TACList.size(); i++) {
            System.out.println(i + ":" + this.TACList.get(i));
        }
    }
    //创建只有一个节点的链表
    private ArrayList<Integer> makeList(int index){
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(index);
        return list;
    }
    //将两个链表合并成一个
    private ArrayList<Integer> merge(ArrayList<Integer> p1, ArrayList<Integer> p2){
        ArrayList<Integer> list = new ArrayList<Integer>();
        if(p1 != null) list.addAll(p1);
        if(p2 != null) list.addAll(p2);
        return list;
    }
    //回填
    private void backPatch(ArrayList<Integer> list, int value) {
        if(list == null) return;
        for(int item : list) {
            if(item >= this.TACList.size()) {
                System.out.println("backpatch error, found illegal pointer:" + item + "with value="+ value);
                continue;
            }
            String code = this.TACList.get(item);
            code = code + " " + value;
            this.TACList.set(item, code);
        }
    }
    private Token matchToken(TokenType type, String functionName){
        if(lookAhead.getType() != type){
            parsingError(type.toString(), functionName);
        }
        Token matchedSymbol = lookAhead;
        lookAhead = lexer.nextToken();
        return matchedSymbol;
    }
    private void parsingError(String types, String functionName){
        printTAC();
        System.out.println("Parsing Error! in " + functionName);
        System.out.println("encounter " + lookAhead.getLexeme());
        System.out.println("at line " + lookAhead.getLine() + ",column " +lookAhead.getColumn());
        System.out.println("while expecting " + types);
        System.exit(1);
    }
    /**
     * 调用开始符号对应的方法，进行语法分析。
     * @return 返回分析是否成功。
     */
    private void parse() {
        lookAhead = lexer.nextToken();
        simpleblock();
        printTAC();
    }
    /**
     * simpleblock = LBRACE sequence RBRACE * B -> { S } */
    private void simpleblock() {
        if(lookAhead.getType() == TokenType.LBRACKET){
            matchToken(TokenType.LBRACKET, "simpleblock");
            sequence();
            this.TACList.add("halt");
            matchToken(TokenType.RBRACKET, "simpleblock");
        }else{
            parsingError(TokenType.LBRACKET.toString(), "simpleblock");
        }
    }
    /**
    * sequence = assignmentStatement sequence |
    * ifStatement sequence | * whileStatement sequence | * epsilon
    * S -> AS | IS | WS | ε */
    private void sequence(){
        if(lookAhead.getType() == TokenType.IDENTIFIER){
            assignmentStatement();
            sequence();
        }else if(lookAhead.getType() == TokenType.KEY_IF){
            ifStatement();
            sequence();
        }else if(lookAhead.getType() == TokenType.KEY_WHILE){
            whileStatement();
            sequence();
        }else if(lookAhead.getType() == TokenType.RBRACKET){
//match epsilon
        }else{
            String errorTypes = TokenType.IDENTIFIER.toString() + "," +
                    TokenType.RBRACKET.toString();
            parsingError(errorTypes, "sequence");
        }
    }
    /***********************begin************************/
    private void whileStatement() {
        if(lookAhead.getType() == TokenType.KEY_WHILE) {
            matchToken(TokenType.KEY_WHILE, "whileStatement");
            matchToken(TokenType.LPAREN, "whileStatement");
            ArrayList<Integer> whilebegin = makeList(nextstm);
            AddressList boolexpression=boolexpression();
            matchToken(TokenType.RPAREN, "whileStatement");
            backPatch(boolexpression.trueList, nextstm);
            matchToken(TokenType.LBRACKET, "whileStatement");
            sequence();
            matchToken(TokenType.RBRACKET, "whileStatement");
            this.TACList.add("goto"+" "+whilebegin.get(0));
            nextstm++;
            backPatch(boolexpression.falseList, nextstm);
        }
    }

    //ifStatement	→  if ( boolexpression ) { sequence } OptionalElse
    private void ifStatement() {
        if(lookAhead.getType() == TokenType.KEY_IF) {
            matchToken(TokenType.KEY_IF, "ifStatement");
            matchToken(TokenType.LPAREN, "ifStatement");
            AddressList boolexpression=boolexpression();
            matchToken(TokenType.RPAREN, "ifStatement");
            backPatch(boolexpression.trueList, nextstm);
            matchToken(TokenType.LBRACKET, "ifStatement");
            sequence();
            matchToken(TokenType.RBRACKET, "ifStatement");
            optionalElse(boolexpression);
        }
    }

    //OptionalElse	→  else { sequence }
    //OptionalElse	→  ε
    private void optionalElse(AddressList inh) {
        if(lookAhead.getType() == TokenType.KEY_ELSE) {
            matchToken(TokenType.KEY_ELSE, "optionalElse");
            ArrayList<Integer> elseNext = makeList(nextstm);
            nextstm++;
            this.TACList.add("goto");
            backPatch(inh.falseList, nextstm);

            matchToken(TokenType.LBRACKET, "optionalElse");
            sequence();
            matchToken(TokenType.RBRACKET, "optionalElse");
            backPatch(elseNext, nextstm);
        }
        else if(lookAhead.getType() == TokenType.IDENTIFIER||lookAhead.getType() == TokenType.ifStatement||
                lookAhead.getType() == TokenType.whileStatement||lookAhead.getType() == TokenType.RBRACKET) {
            backPatch(inh.falseList, nextstm);
        }
    }

    //Boolexpression  →  boolterm boolexpression_1	select=TRUE  FALSE  (   ID  NUM
    private AddressList boolexpression() {
        if(lookAhead.getType() == TokenType.BOOL_TRUE||lookAhead.getType() == TokenType.BOOL_FALSE||
                lookAhead.getType() == TokenType.LPAREN||lookAhead.getType() == TokenType.IDENTIFIER||lookAhead.getType() == TokenType.NUMBER_LITERAL)
        {
            AddressList boolterm=boolterm();
            AddressList boolexpression_1= boolexpression_1(boolterm);
            return boolexpression_1;
        }
        else {
            return null;
        }
    }

    //Boolexpression_1  →  OR boolterm boolexpression_1				select=OR
    //Boolexpression_1  →  ε										select=)
    private AddressList boolexpression_1(AddressList inh) {
        if(lookAhead.getType() == TokenType.LOGICAL_OR) {
            matchToken(TokenType.LOGICAL_OR, "boolexpression_1");
            backPatch(inh.falseList, nextstm);
            AddressList boolterm=boolterm();
            AddressList boolexpression_1=new AddressList();
            boolexpression_1.trueList=merge(boolterm.trueList, inh.trueList);
            boolexpression_1.falseList=boolterm.falseList;
            boolexpression_1=boolexpression_1(boolexpression_1);
            return boolexpression_1;
        }
        else if(lookAhead.getType() == TokenType.RPAREN) {
            return inh;
        }
        else {
            return null;
        }
    }
    //Boolterm	 →  boolfactor boolterm_1		select=TRUE  FALSE  (   ID  NUM
    private AddressList boolterm() {
        if(lookAhead.getType() == TokenType.BOOL_TRUE||lookAhead.getType() == TokenType.BOOL_FALSE||
                lookAhead.getType() == TokenType.LPAREN||lookAhead.getType() == TokenType.IDENTIFIER||lookAhead.getType() == TokenType.NUMBER_LITERAL) {
            AddressList boolfactor =boolfactor();
            AddressList boolterm_1=boolterm_1(boolfactor);
            return boolterm_1;
        }
        else {
            return null;
        }
    }

    //Boolterm_1	→  AND boolfactor boolterm_1		select = AND
    //Boolterm_1  →  ε							select = )   OR
    private AddressList boolterm_1(AddressList inh) {
        if(lookAhead.getType() == TokenType.LOGICAL_AND) {
            matchToken(TokenType.LOGICAL_AND, "boolterm_1");
            backPatch(inh.trueList, nextstm);
            AddressList boolfator =boolfactor();
            AddressList boolterm_1=new AddressList();
            boolterm_1.trueList=boolfator.trueList;
            boolterm_1.falseList=merge(boolfator.falseList, inh.falseList);
            boolterm_1=boolterm_1(boolterm_1);
            return boolterm_1;
        }
        else if(lookAhead.getType() == TokenType.LOGICAL_OR||lookAhead.getType() == TokenType.RPAREN){
            return inh;
        }
        else {
            return null;
        }
    }

    //Boolfactor   →  true					select = TRUE
    //Boolfactor   →  false					select = FALSE
    //Boolfactor   →  relationalExpression	select = (    ID    NUM
    private AddressList boolfactor() {
        if(lookAhead.getType() == TokenType.BOOL_TRUE) {
            matchToken(TokenType.BOOL_TRUE, "boolfactor");
            AddressList boolfactor =new AddressList();
            boolfactor.trueList = makeList(nextstm);
            this.TACList.add("goto");
            return boolfactor;
        }
        else if(lookAhead.getType() == TokenType.BOOL_FALSE){
            matchToken(TokenType.BOOL_FALSE, "boolfactor");
            AddressList boolfactor1 = new AddressList();
            boolfactor1.falseList = makeList(nextstm);
            this.TACList.add("goto");
            return boolfactor1;
        }else if(lookAhead.getType() == TokenType.LPAREN||lookAhead.getType() == TokenType.IDENTIFIER||lookAhead.getType() == TokenType.NUMBER_LITERAL)
        {
            AddressList relationalExpression=relationalExpression();
            return relationalExpression;
        }
        else {
            return null;
        }
    }

    //relationalExpression → expression relationalOperator expression	select = (    ID    NUM
    private AddressList relationalExpression() {
        if(lookAhead.getType() == TokenType.LPAREN||lookAhead.getType() == TokenType.IDENTIFIER||lookAhead.getType() == TokenType.NUMBER_LITERAL)
        {
            String e1=expression();
            String rel=relationalOperator();
            String  e2=expression();
            AddressList addressList=new AddressList();
            addressList.trueList=makeList(nextstm++);
            addressList.falseList=makeList(nextstm++);
            this.TACList.add("if "+e1+rel+e2+" goto");
            this.TACList.add("goto");
            return addressList;
        }
        else {
            return null;
        }

    }

    //relationalOperator	 →  <		select = <
    //relationalOperator	 →  >		select = >
    //relationalOperator	 →  <=		select = <=
    //relationalOperator	 →  >=		select = >=
    //relationalOperator	 →  ==		select = ==
    //relationalOperator	 →  !=		select = !=
    private String relationalOperator() {
        if(lookAhead.getType() == TokenType.LESS) {
            Token a=matchToken(TokenType.LESS, "relationalOperator");
            return a.getLexeme();
        }
        else if(lookAhead.getType() == TokenType.GREATER) {
            Token a=matchToken(TokenType.GREATER, "relationalOperator");
            return a.getLexeme();
        }
        else if(lookAhead.getType() == TokenType.LESS_EQUAL) {
            Token a=matchToken(TokenType.LESS_EQUAL, "relationalOperator");
            return a.getLexeme();
        }
        else if(lookAhead.getType() == TokenType.GREATER_EQUAL) {
            Token a=matchToken(TokenType.LESS_EQUAL, "relationalOperator");
            return a.getLexeme();
        }
        else if(lookAhead.getType() == TokenType.EQUAL) {
            Token a=matchToken(TokenType.EQUAL, "relationalOperator");
            return a.getLexeme();
        }
        else if(lookAhead.getType() == TokenType.NOT_EQUAL) {
            Token a=matchToken(TokenType.NOT_EQUAL, "relationalOperator");
            return a.getLexeme();
        }else {
            return null;}
    }
    /***********************end************************/

    /**
     * assignmentStatement = IDENTIFIER ASSIGN expression SEMICOLON
     * A -> id = E; */
    private void assignmentStatement(){
        if(lookAhead.getType() == TokenType.IDENTIFIER){
            Token id = matchToken(TokenType.IDENTIFIER, "assignmentStatement");
            matchToken(TokenType.ASSIGN, "assignmentStatement");
            String eName = expression();
            matchToken(TokenType.SEMICOLON, "assignmentStatement");
            this.TACList.add(id.getLexeme() + "=" + eName);
        } else {
            String errorTypes = TokenType.IDENTIFIER.toString();
            parsingError(errorTypes, "assignmentStatement");
        }
    }
    /**
     * expression = term expression_1
     * E -> TE' * @return
     */
    private String expression(){
        if(lookAhead.getType() == TokenType.IDENTIFIER
                || lookAhead.getType() == TokenType.LPAREN
                || lookAhead.getType() == TokenType.NUMBER_LITERAL){
            String tName = term();
            String eName = expression_1(tName);
            return eName;
        } else {
            String errorTypes = TokenType.IDENTIFIER.toString()
                    + "," + TokenType.NUMBER_LITERAL.toString()
                    + "," + TokenType.LPAREN.toString();
            parsingError(errorTypes, "expression");
            return null;
        }
    }
    /**
     * expression_1 = PLUS term expression_1 | select = +
     * MINUS term expression_1 | select = - * epsilon select = ; ) < > <= >= == != &&|| * E' -> +TE' | -TE' | ε */
    private String expression_1(String inh){
        if(lookAhead.getType() == TokenType.PLUS){
            matchToken(TokenType.PLUS, "expression_1");
            String tName = term();
            String e1Inh = this.newTemp();
            this.TACList.add(e1Inh + "=" + inh + "+" + tName);
            String e1Syn = expression_1(e1Inh);
            return e1Syn;
        } else if (lookAhead.getType() == TokenType.MINUS) {
            matchToken(TokenType.MINUS, "expression_1");
            String tName = term();
            String e1Inh = this.newTemp();
            this.TACList.add(e1Inh + "=" + inh + "-" + tName);
            String e1Syn = expression_1(e1Inh);
            return e1Syn;
        } else if(lookAhead.getType() == TokenType.SEMICOLON
                || lookAhead.getType() == TokenType.RPAREN
                || lookAhead.getType() == TokenType.LESS
                || lookAhead.getType() == TokenType.LESS_EQUAL
                || lookAhead.getType() == TokenType.GREATER
                || lookAhead.getType() == TokenType.GREATER_EQUAL
                || lookAhead.getType() == TokenType.EQUAL
                || lookAhead.getType() == TokenType.NOT_EQUAL
                || lookAhead.getType() == TokenType.LOGICAL_AND
                || lookAhead.getType() == TokenType.LOGICAL_OR){
            //match epsilon
            //select = ; ) < > <= >= == != && ||
            return inh;
        } else {
            String errorTypes = TokenType.PLUS.toString()
                    + "," + TokenType.MINUS.toString()
                    + "," + TokenType.SEMICOLON.toString()
                    + "," + TokenType.LESS.toString()
                    + "," + TokenType.LESS_EQUAL.toString()
                    + "," + TokenType.GREATER.toString()
                    + "," + TokenType.GREATER_EQUAL.toString()
                    + "," + TokenType.EQUAL.toString()
                    + "," + TokenType.NOT_EQUAL.toString()
                    + "," + TokenType.LOGICAL_AND.toString()
                    + "," + TokenType.LOGICAL_OR.toString();
            parsingError(errorTypes, "expression_1");
            return null;
        }
    }
    /**
     * term = factor term_1
     * T -> FT' */
    private String term(){
        if(lookAhead.getType() == TokenType.IDENTIFIER
                || lookAhead.getType() == TokenType.LPAREN
                || lookAhead.getType() == TokenType.NUMBER_LITERAL){
            String fName = factor();
            String tName = term_1(fName);
            return tName;
        } else {
            String errorTypes = TokenType.IDENTIFIER.toString()
                    + "," + TokenType.NUMBER_LITERAL.toString()
                    + "," + TokenType.LPAREN.toString();
            parsingError(errorTypes, "term");
            return null;
        }
    }
    /**
     * term_1 = MULT factor term_1 | select = *
     * DIV factor term_1 | select = / * MOD factor term_1 | select = %
     * epsilon select = + - ; ) < > <= >= == != &&|| * T' -> *FT' | /FT' | %FT' | ε */
    private String term_1(String inh){
        if(lookAhead.getType() == TokenType.TIMES){
            matchToken(TokenType.TIMES, "term_1");
            String fName = factor();
            String t1Inh = this.newTemp();
            this.TACList.add(t1Inh + "=" + inh + "*" + fName);
            String t1Syn = term_1(t1Inh);
            return t1Syn;
        } else if (lookAhead.getType() == TokenType.DIVIDE) {
            matchToken(TokenType.DIVIDE, "term_1");
            String fName = factor();
            String t1Inh = this.newTemp();
            this.TACList.add(t1Inh + "=" + inh + "/" + fName);
            String t1Syn = term_1(t1Inh);
            return t1Syn;
        } else if(lookAhead.getType() == TokenType.REMAINDER){
            matchToken(TokenType.REMAINDER, "term_1");
            String fName = factor();
            String t1Inh = this.newTemp();
            this.TACList.add(t1Inh + "=" + inh + "%" + fName);
            String t1Syn = term_1(t1Inh);
            return t1Syn;
        } else if(lookAhead.getType() == TokenType.PLUS
                || lookAhead.getType() == TokenType.MINUS
                || lookAhead.getType() == TokenType.SEMICOLON
                || lookAhead.getType() == TokenType.RPAREN
                || lookAhead.getType() == TokenType.LESS
                || lookAhead.getType() == TokenType.LESS_EQUAL
                || lookAhead.getType() == TokenType.GREATER
                || lookAhead.getType() == TokenType.GREATER_EQUAL
                || lookAhead.getType() == TokenType.EQUAL
                || lookAhead.getType() == TokenType.NOT_EQUAL
                || lookAhead.getType() == TokenType.LOGICAL_AND
                || lookAhead.getType() == TokenType.LOGICAL_OR){
                //match epsilon
                //follow(T') = + - ; ) < > <= >= == != && ||
            return inh;
        } else {
            String errorTypes = TokenType.TIMES.toString()
                    + "," + TokenType.DIVIDE.toString()
                    + "," + TokenType.REMAINDER.toString()
                    + "," + TokenType.PLUS.toString()
                    + "," + TokenType.MINUS.toString()
                    + "," + TokenType.RPAREN.toString()
                    + "," + TokenType.SEMICOLON.toString()
                    + "," + TokenType.LESS.toString()
                    + "," + TokenType.LESS_EQUAL.toString()
                    + "," + TokenType.GREATER.toString()
                    + "," + TokenType.GREATER_EQUAL.toString()
                    + "," + TokenType.EQUAL.toString()
                    + "," + TokenType.NOT_EQUAL.toString()
                    + "," + TokenType.LOGICAL_AND.toString()
                    + "," + TokenType.LOGICAL_OR.toString();
            parsingError(errorTypes, "term_1");
            return null;
        }
    }
    /**
     * factor = LPAREN expression RPAREN | * IDENTIFIER | * NUMBER_LITERAL * F -> (E) | id | number */
    private String factor() {
        if(lookAhead.getType() == TokenType.LPAREN){
            matchToken(TokenType.LPAREN, "factor");
            String eName = expression();
            matchToken(TokenType.RPAREN, "factor");
            return eName;
        }else if(lookAhead.getType() == TokenType.IDENTIFIER){
            Token id = matchToken(TokenType.IDENTIFIER, "factor");
            return(id.getLexeme());
        }else if(lookAhead.getType() == TokenType.NUMBER_LITERAL){
            Token id = matchToken(TokenType.NUMBER_LITERAL, "factor");
            return(id.getLexeme());
        }else{
            String errorTypes = TokenType.LPAREN.toString()
                    + "," + TokenType.IDENTIFIER.toString()
                    + "," + TokenType.NUMBER_LITERAL.toString();
            parsingError(errorTypes, "factor");
            return null;
        }
    }
    private String newTemp() {
        CNT++;
        return "T" + CNT;
    }
}