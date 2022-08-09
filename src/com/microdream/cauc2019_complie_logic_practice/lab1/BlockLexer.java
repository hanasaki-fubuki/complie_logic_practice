package com.microdream.complie_logic.lab1;

import java.io.*;

public class BlockLexer {
    private PushbackReader in = null;
    private StringBuffer lexeme = new StringBuffer();
    private char c;
    private int line = 0;
    private int column = 0;
    public BlockLexer(String infile) {
        PushbackReader reader = null;
        try {
            reader = new PushbackReader(new FileReader(infile));
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        in = reader;
    }

    //取得下一个字符
    private void nextChar() {
        try {
            c = (char)in.read();
            lexeme.append(c);
            column++;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    //回退一个字符（多读入的）
    private void pushbackChar() {
        try {
            in.unread(lexeme.charAt(lexeme.length() - 1));
            lexeme.deleteCharAt(lexeme.length() - 1);
            column--;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    //取得词法记号，并重置状态变量
    private Token getToken(TokenType type) {
        String t = lexeme.toString();
        lexeme.setLength(0);
        return new Token(type, t, line + 1, column - t.length() + 1);
    }
    //扔掉一个字符（此时单词应该还未开始，只需把长度设为 0 即可）
    private void dropChar() {
        lexeme.setLength(0);
    }

    //去空格、换行、回车等
    private void removeSpace() {
        nextChar();
        while (Character.isWhitespace(c)) {
            if (c == '\n') {
                line++;
                column = 0;
            }
            dropChar();
            nextChar();
        }
        pushbackChar();
    }
    //识别标识符
    private Token getID_or_Keywords() {
        int s = 0;
        while(true) {
            switch(s) {
                case 0:
                    nextChar();
                    if(Character.isLetterOrDigit(c) || c=='_') s = 0;
                    else s = 1;
                    break;
                case 1:
                    pushbackChar();
                    String t = lexeme.toString();
                    if (t.equalsIgnoreCase("int")){
                        return getToken(TokenType.KEY_INT);
                    } else if(t.equalsIgnoreCase("boolean")) {
                        return getToken(TokenType.KEY_BOOLEAN);
                    } else if(t.equalsIgnoreCase("if")) {
                        return getToken(TokenType.KEY_IF);
                    } else if(t.equalsIgnoreCase("else")) {
                        return getToken(TokenType.KEY_ELSE);
                    } else if(t.equalsIgnoreCase("while")) {
                        return getToken(TokenType.KEY_WHILE);
                    } else if(t.equalsIgnoreCase("true")) {
                        return getToken(TokenType.BOOL_TRUE);
                    } else if(t.equalsIgnoreCase("false")) {
                        return getToken(TokenType.BOOL_FALSE);
                    } else {
                        return getToken(TokenType.IDENTIFIER);
                    }
            }
        }
    }

    /*****************************begin*******************************/

    //识别整形常数，可能是十进制、八进制或十六进制
    private Token getIntConst() {
        int s = 0;
        if (c == '0') s = 1;
        while(true) {
            switch (s){
                case 0:
                    nextChar();
                    if (Character.isDigit(c)) s = 0;
                    else {
                        pushbackChar();
                        return getToken(TokenType.NUMBER_LITERAL);
                    }
                    break;
                case 1:
                    nextChar();
                    if (c == 'x' || c == 'X') s = 3;
                    else if (Character.isDigit(c)) s = 2;
                    else {
                        pushbackChar();
                        return  getToken(TokenType.NUMBER_LITERAL);
                    }
                    break;
                case 2:
                    nextChar();
                    if (c >= '0' && Character.isDigit(c) && c <= '7') s = 2;
                    else {
                        pushbackChar();
                        return getToken(TokenType.NUMBER_LITERAL);
                    }
                    break;
                case 3:
                    nextChar();
                    if (Character.isDigit(c) || Character.toUpperCase(c) >= 'A' && Character.toUpperCase(c) <= 'F') s = 3;
                    else {
                        pushbackChar();
                        return getToken(TokenType.NUMBER_LITERAL);
                    }
                    break;
            }
        }
    }

    //识别/,/=
    //去多行注释/* */
    //去单行注释//
    private Token getDivide_or_removeComment() {
        int s = 0;
        while (true){
            nextChar();
            switch (s){
                case 0:
                    if (c == '/') s = 1;
                    else if (c == '*') s = 2;
                    else if (c == '=') return getToken(TokenType.DIVIDEEQUAL);
                    else{
                        pushbackChar();
                        return getToken(TokenType.DIVIDE);
                    }
                    break;
                case 1:
                    if (c == '\n'){
                        line++;
                        column = 0;
                        dropChar();
                        return null;
                    } else {
                        s = 1;
                    }
                    break;
                case 2:
                    if (c == '*') s = 3;
                    else if (c == '\n') {
                        line++;
                        column = 0;
                        s = 2;
                    } else s = 2;
                    break;
                case 3:
                    if (c == '/'){
                        dropChar();
                        return null;
                    } else if (c == '\n') {
                        line++;
                        column = 0;
                        s = 2;
                    } else s = 2;
                    break;
            }
        }
    }
    //识别+,++,+=
    private Token getPlus() {
        nextChar();
        if (c == '=') {
            return getToken(TokenType.PLUSEQUAL);
        }
        if (c == '+') {
            return getToken(TokenType.PLUSPLUS);
        } else {
            pushbackChar();
            return getToken(TokenType.PLUS);
        }
    }

    //识别-,--,-=
    private Token getMinus() {
        nextChar();
        if (c == '=') {
            return getToken(TokenType.MINUSEQUAL);
        }
        if (c == '-') {
            return getToken(TokenType.MINUSMINUS);
        } else {
            pushbackChar();
            return getToken(TokenType.MINUS);
        }
    }

    //识别*,*=
    private Token getTimes() {
        nextChar();
        if (c != '=') {
            pushbackChar();
            return getToken(TokenType.TIMES);
        }
        else return getToken(TokenType.TIMESEQUAL);
    }

    //识别%,%=
    private Token getRemainder() {
        nextChar();
        if (c != '=') {
            pushbackChar();
            return getToken(TokenType.REMAINDER);
           }
        else return getToken(TokenType.REMAINDEREQUAL);
    }

    //识别>,>>,>=
    private Token getGreater() {
        nextChar();
        if (c == '>') {
            return getToken(TokenType.RIGHTSHIFT);
        }
        if (c == '=') {
            return getToken(TokenType.GREATER_EQUAL);
        } else {
            pushbackChar();
            return getToken(TokenType.GREATER);
        }
    }
    //识别<,<<,<=
    private Token getLess() {
        nextChar();
        if (c == '<') {
            return getToken(TokenType.LEFTSHIFT);
        }
        if (c == '=') {
            return getToken(TokenType.LESS_EQUAL);
        } else {
            pushbackChar();
            return getToken(TokenType.LESS);
        }
    }
    //识别=,==
    private Token getAssign_or_Equal() {
        nextChar();
        if (c != '=') {
            pushbackChar();
            return getToken(TokenType.ASSIGN);
        }
        else return getToken(TokenType.EQUAL);
    }

    //识别!,!=
    private Token getNot_or_NotEqual() {
        nextChar();
        if (c != '=') {
            pushbackChar();
            return getToken(TokenType.LOGICAL_NOT);
        }
        else return getToken(TokenType.NOT_EQUAL);
    }

    //识别&&
    private Token getAnd() {
        nextChar();
        if (c == '&') {
            return getToken(TokenType.LOGICAL_AND);
        }
        return null;
    }

    //识别||
    private Token getOr() {
        nextChar();
        if (c == '|') {
            return getToken(TokenType.LOGICAL_OR);
        }
        return null;
    }

    /*****************************end*******************************/

    //获取下一个 token
    public Token nextToken() {
        Token token = null;
        while(null == token) {
            removeSpace();
            nextChar();
            if ( Character.isDigit(c) ) {
                token = getIntConst();
            } else if ( Character.isLetter(c) || c == '_'){
                token = getID_or_Keywords();
            } else if ( c == '+'){
                token = getPlus();
            } else if ( c == '-'){
                token = getMinus();
            } else if ( c == '*'){
                token = getTimes();
            } else if ( c == '/'){
                token = getDivide_or_removeComment();
            } else if ( c == '%'){
                token = getRemainder();
            } else if ( c == '!'){
                token = getNot_or_NotEqual();
            } else if ( c == '&'){
                token = getAnd();
            } else if ( c == '|'){
                token = getOr();
            } else if ( c == '='){
                token = getAssign_or_Equal();
            } else if ( c == '>'){
                token = getGreater();
            } else if ( c == '<'){
                token = getLess();
            } else if ( c == '('){
                token = getToken(TokenType.LPAREN);
            } else if ( c == ')'){
                token = getToken(TokenType.RPAREN);
            } else if ( c == '{'){
                token = getToken(TokenType.LBRACKET);
            } else if ( c == '}'){
                token = getToken(TokenType.RBRACKET);
            } else if ( c == ';'){
                token = getToken(TokenType.SEMICOLON);
            } else if (c == ','){
                token = getToken(TokenType.COMMA);
            } else if (c == '.'){
                token = getToken(TokenType.DOT);
            } else if ((c & 0xff) == 0xff) {
                token = getToken(TokenType.EOF);
            } else {
                System.out.println(" get nextToken error!");
                System.out.println(" find illegal character " + c);
                System.out.println(" at line " + (line + 1) + ",colum " + column);
                System.exit(1);
            }
        }
        return token;
    }
}
