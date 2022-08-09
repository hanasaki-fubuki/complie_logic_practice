package com.microdream.complie_logic.lab1;

public enum TokenType{
    /** 忽略的词法单位 **/
    IGNORE,

    /** 变量 **/
    IDENTIFIER,     //标识符

    /** 常量 **/
    NUMBER_LITERAL, //整形常量
    BOOL_TRUE, //true
    BOOL_FALSE, //false

    /** 保留字 */
    KEY_INT, //int
    KEY_BOOLEAN,//boolean
    KEY_WHILE, //while
    KEY_IF, //if
    KEY_ELSE, //else

    /** 算术运算符 */
    PLUS, //+
    PLUSPLUS, //++
    PLUSEQUAL, //+=
    MINUS, //-
    MINUSMINUS,//--
    MINUSEQUAL,//-=
    TIMES, //*
    TIMESEQUAL, //*=
    DIVIDE, ///
    DIVIDEEQUAL,//*=
    REMAINDER, //%
    REMAINDEREQUAL, //%=

    /** 位运算符 */
    LEFTSHIFT, //<<
    RIGHTSHIFT, //>>

    /** 关系运算符 */
    LESS, //<
    GREATER, //>
    LESS_EQUAL, //<=
    GREATER_EQUAL, //>=
    NOT_EQUAL, //!=
    EQUAL, //==

    /** 逻辑运算符 */
    LOGICAL_NOT,//!
    LOGICAL_AND, //&&
    LOGICAL_OR, //||

    /** 赋值符号 */
    ASSIGN, //=

    /** 括号 */
    LPAREN, //(
    RPAREN, //)
    LBRACKET, //{
    RBRACKET, //}

    /** 界符 */
    COMMA, //逗号,
    SEMICOLON, //分号;
    DOT, //圆点.

    // /** 文件结尾符 */
    EOF, //end of file

    /** 非终结符号以及一些特殊的符号，语法分析时使用 */
    Epsilon, //空
    Start, //总的开始符号
    Simpleblock, //{****}
    Sequence, //语句序列
    assignmentStatement,//赋值语句

    Expression, //E
    Expression_1, //E'
    Term, //T
    Term_1, //T'
    Factor, //F
    Boolexpression, //布尔表达式
    Boolexpression_1, Boolterm, Boolterm_1, Boolfactor, relationalExpression, //关系表达式
    relationalOperator, //关系运算符

    ifStatement, //if 语句
    OptionalElse, //else 语句（可选）
    whileStatement //while 语句
}
