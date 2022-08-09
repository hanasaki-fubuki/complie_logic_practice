package com.microdream.complie_logic.lab4;

import com.microdream.complie_logic.lab1.*;
import com.microdream.complie_logic.lab3.*;
import java.util.*;

public class LRTAC {
    private BlockLexer lexer = null;
    private Token lookAhead = null;
    private Stack<Integer> stateStack;
    private Stack<TokenType> symbolStack;
    private Stack<Attributes> ATCStack;
    private static int CNT;
    public void doParse(String filePath){
        this.stateStack = new Stack<Integer>();
        this.symbolStack = new Stack<TokenType>();
        this.ATCStack = new Stack<Attributes>();
        this.lexer = new BlockLexer(filePath);
        this.CNT = 0;
        this.parse();
    }
    //生成临时变量
    private String newTemp() {
        CNT++;
        return "T" + CNT;
    }
    /****************begin*******************/
    public void parse() {
        this.stateStack.push(0);
        this.symbolStack.push(TokenType.EOF);

        this.lookAhead=this.lexer.nextToken();

        int step=1;
        while(true) {
            int state=this.stateStack.peek();
            LRTableEntry entry=LRTable.get(state, this.lookAhead.getType());
            if(entry.getAction()=='a') {
                break;
            }else if(entry.getAction()=='s') {
                this.symbolStack.push(this.lookAhead.getType());
                this.stateStack.push(entry.getState());
                this.ATCStack.push(new Attributes(this.lookAhead.getLexeme(), null));
                this.lookAhead=this.lexer.nextToken();
            }else if(entry.getAction()=='r') {
                int j=entry.getState();
                Production p=Grammar.productions.get(j);
                if(p==null)
                {
                    System.out.println("ERROR");
                    break;
                }
                TokenType left=p.getLeft();
                TokenType[] right=p.getRight();
                LRTableEntry entry2=LRTable.get(this.stateStack.get(this.stateStack.size()-right.length-1), left) ;
                for(int i=0;i<right.length;i++) {
                    this.stateStack.pop();
                    this.symbolStack.pop();
                }
                this.symbolStack.push(left);
                this.stateStack.push(entry2.getState());
                this.ATCStack(j);
            }
        }
        System.out.println(this.ATCStack.peek().getCode());
    }

    public void ATCStack(int j) {
        Attributes attributes=null;
        Attributes E=null;
        Attributes T=null;
        Attributes F=null;
        String code="";
        switch (j) {
            case 0: {
                attributes=this.ATCStack.pop();
                break;
            }
            case 1:{
                T=this.ATCStack.pop();
                this.ATCStack.pop();
                E=this.ATCStack.pop();
                attributes =new Attributes();
                attributes.setName(this.newTemp());
                if(E.getCode()!=null&&E.getCode()!="")code=E.getCode()+'\n';
                if(T.getCode()!=null&&T.getCode()!="")code=code+T.getCode()+'\n';
                code=code+attributes.getName()+"="+E.getName()+"+"+T.getName();
                attributes.setCode(code);
                this.ATCStack.push(attributes);
                break;
            }
            case 2:
            {
                T=this.ATCStack.pop();
                attributes=new Attributes();
                attributes.setName(T.getName());
                if(T.getCode()!=null)
                    attributes.setCode(T.getCode());
                this.ATCStack.push(attributes);
                break;
            }
            case 3:{
                F=this.ATCStack.pop();
                this.ATCStack.pop();
                T=this.ATCStack.pop();
                attributes=new Attributes();
                attributes.setName(this.newTemp());
                if(T.getCode()!=null&&T.getCode()!="")code=T.getCode()+'\n';
                if(F.getCode()!=null&&F.getCode()!="")code=code+F.getCode()+'\n';
                code=code+attributes.getName()+"="+T.getName()+"*"+F.getName();
                attributes.setCode(code);
                this.ATCStack.push(attributes);
                break;
            }
            case 4:{
                F=this.ATCStack.pop();
                attributes=new Attributes();
                attributes.setName(F.getName());
                if(F.getCode()!=null)
                    attributes.setCode(F.getCode());
                this.ATCStack.push(attributes);
                break;
            }
            case 5:{
                this.ATCStack.pop();
                E=this.ATCStack.pop();
                this.ATCStack.pop();
                attributes =new Attributes();
                attributes.setName(E.getName());
                if(E.getCode()!=null)
                    attributes.setCode(E.getCode());
                this.ATCStack.push(attributes);
                break;
            }
            case 6:{
                attributes =new Attributes();
                attributes.setName(this.ATCStack.pop().getName());
                attributes.setCode("");
                this.ATCStack.push(attributes);
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + j);
        }
    }
    /****************end*********************/
}