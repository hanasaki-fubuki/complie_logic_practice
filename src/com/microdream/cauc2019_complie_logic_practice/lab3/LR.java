package com.microdream.complie_logic.lab3;

import java.util.*;
import com.microdream.complie_logic.lab1.*;

public class LR {
    private BlockLexer lexer = null;
    private Token lookAhead = null;
    private Stack<Integer> stateStack;
    private Stack<TokenType> symbolStack;
    private int length1;
    public void doParse(String filePath){
        this.stateStack = new Stack<Integer>();
        this.symbolStack = new Stack<TokenType>();
        this.lexer = new BlockLexer(filePath);
        this.parse();
    }
    public void parse() {

        /****************begin*******************/

        this.stateStack.push(0);
        length1++;
        this.symbolStack.push(TokenType.EOF);
        this.lookAhead=this.lexer.nextToken();
        int step=1;
        StringBuffer buf=new StringBuffer();
        buf.append("²½Öè\t×´Ì¬Õ»\t·ûºÅÕ»\tµ±Ç°·ûºÅ\tAction\tGoto\n");
        while(true) {
            buf.append(step++ + "\t");
            buf.append(this.stateStack + "\t");
            buf.append(this.symbolStack + "\t");
            buf.append(this.lookAhead.getType() + "\t");
            int state=this.stateStack.peek();
            LRTableEntry entry=LRTable.get(state, this.lookAhead.getType());
            if(entry==null) {
                buf.append("error");
                break;
            }
            if(entry.getAction()=='a') {
                buf.append("acc");
                break;
            }
            if(entry.getAction()=='s') {
                buf.append(entry.toString());
                buf.append('\n');
                this.stateStack.push(entry.getState());
                length1++;
                this.symbolStack.push(this.lookAhead.getType());

                this.lookAhead=this.lexer.nextToken();
            }
            if(entry.getAction()=='r') {
                buf.append(entry.toString());
                buf.append('\t');
                Integer[] state1=new Integer[10];
                TokenType[] symbol1=new TokenType[10];
                for(int i=0;i<length1;i++) {
                    state1[i]=this.stateStack.pop();

                }
                for(int i=length1-1;i>=0;i--) {
                    this.stateStack.push(state1[i]);
                }
				TokenType[] right=Grammar.productions.get(entry.getState()).getRight();

                int length3=right.length;
                for(int i=0;i<length3;i++) {
                    this.symbolStack.pop();
                }
                Integer count=0;
                int flag=0;
                int count2=0;
                this.symbolStack.push(Grammar.productions.get(entry.getState()).getLeft());
                for(int k=0;k<length1;k++) {
                    LRTableEntry entry1=LRTable.get(state1[k],Grammar.productions.get(entry.getState()).getLeft());
                    if(entry1!=null) {
                        count=entry1.getState();
                        break;
                    }
                }
                for(int i=0;i<length1;i++) {
                    if(state1[i]==count) {
                        flag=1;
                        count2=i;
                    }
                }
                if(flag==1) {
                    for(int i=1;i<=count2;i++) {
                        this.stateStack.pop();
                        length1--;
                    }
                }
                else {
                    this.stateStack.pop();
                    this.stateStack.push(count);
                }
                buf.append(count);
                buf.append('\n');
            }
        }
        System.out.print(buf);

        /****************end*********************/

    }
}
