package com.microdream.complie_logic.lab2;

import com.microdream.complie_logic.lab1.*;
import java.util.*;

public class LL1 {
    private BlockLexer lexer = null;
    private Token lookAhead = null;
    private Stack<TokenType> stack;
    private LL1Table table = null;
    public LL1() {
        this.table = new LL1Table();
    }
    public void doParse(String filePath){
        this.stack = new Stack<TokenType>();
        this.lexer = new BlockLexer(filePath);
        this.parse();
    }
    public void parse() {

        /****************begin*******************/

        int step=1;
        this.stack.push(TokenType.EOF);
        this.stack.push(TokenType.Simpleblock);
        this.lookAhead=this.lexer.nextToken();
        while(true) {
            TokenType x=this.stack.pop();
            if(this.isTerminal(x)) {
                if(x==TokenType.EOF) {
                    System.out.println(step+" [EOF] EOF success");
                    break;
                }
                else {
                    System.out.print(step+" [");
                    for(TokenType s:this.stack)
                    {
                        System.out.print(s+",");
                    }
                    System.out.print(x);
                    System.out.print("] "+ x+" "+ x+"∆•≈‰\n");
                }
                this.lookAhead=this.lexer.nextToken();
            }else {
                TokenType[] production =this.table.getItem(x, this.lookAhead.getType());
                if(production==null) {
                    System.err.println("≥ˆœ÷¥ÌŒÛ");
                    break;
                }else {
                    System.out.print(step+" "+"[");
                    for(TokenType s:this.stack) {
                        System.out.print(s+",");
                    }
                    System.out.print(x+"");
                    System.out.print("] "+this.lookAhead.getType()+" ");
                    for(int i=0;i<production.length;i++) {
                        if(i==0) {
                            System.out.print(production[i]);
                        }
                        else {
                            System.out.print(","+production[i]);
                        }
                    }
                    System.out.print("\n");

                    for(int i=production.length-1;i>=0;i--) {
                        if(production[i]!=TokenType.Epsilon) {
                            this.stack.push(production[i]);
                        }
                    }
                }
            }
            step++;
        }

        /****************end*******************/

    }
    private String array2String(TokenType[] product) {
        String ret = "";
        for(TokenType type : product) {
            ret += type + ",";
        }
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }
    public boolean isTerminal(TokenType type) {
        if(type.compareTo(TokenType.EOF) <= 0)
            return true;
        else
            return false;
    }
}
