/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.edu.gtu.cse.gte;

import java.util.ArrayList;

/**
 *
 * @author burak
 */
public class Parser {
    private final String CONTINUE="continue";
    private final String VOLATILE="volatile";
    private final String REGISTER="register";
    private final String UNSIGNED="unsigned";
    private final String TYPEDEF="typedef";
    private final String DEFAULT="default";
    private final String DOUBLE="double";
    private final String SIZEOF="sizeof";
    private final String SWITCH="switch";
    private final String RETURN="return";
    private final String EXTERN="extern";
    private final String STRUCT="struct";
    private final String STATIC="static";
    private final String SIGNED="signed";
    private final String WHILE="while";
    private final String BREAK="break";
    private final String UNION="union";
    private final String CONST="const";
    private final String FLOAT="float";
    private final String SHORT="short";
    private final String ELSE="else";
    private final String CASE="case";
    private final String LONG="long";
    private final String ENUM="enum";
    private final String AUTO="auto";
    private final String VOID="void";
    private final String CHAR="char";
    private final String GOTO="goto";
    private final String FOR="for";
    private final String INT="int";
    private final String IF="if";
    private final String DO="do";
    
    private ArrayList<String> keywords;
    private ArrayList<String> variables;
    
    public Parser(){
        this.keywords=new ArrayList<String>();
        this.variables=new ArrayList<String>();
    }
    
    ArrayList<String> getKeywords(){return this.keywords;}
    ArrayList<String> getVariables(){return this.variables;}
    void setKeywords(ArrayList<String> val){this.keywords=val;}
    void setVariables(ArrayList<String> val){this.variables=val;}
    
    /**
     * Parametre olarak line alır
     * @param text 
     */
    public void parse(String text){
        String[] tokens = text.split("\\W");
        for(String iter:tokens){
            switch(iter){
                case CONTINUE:keywords.add("continue");break;
                case VOLATILE:keywords.add("volatile");break;
                case REGISTER:keywords.add("register");break;
                case UNSIGNED:keywords.add("unsigned");break;
                case TYPEDEF:keywords.add("typedef");break;
                case DEFAULT:keywords.add("default");break;
                case DOUBLE:keywords.add("double");break;
                case SIZEOF:keywords.add("sizeof");break;
                case SWITCH:keywords.add("switch");break;
                case RETURN:keywords.add("return");break;
                case EXTERN:keywords.add("extern");break;
                case STRUCT:keywords.add("struct");break;
                case STATIC:keywords.add("static");break;
                case SIGNED:keywords.add("signed");break;
                case WHILE:keywords.add("while");break;
                case BREAK:keywords.add("break");break;
                case UNION:keywords.add("union");break;
                case CONST:keywords.add("const");break;
                case FLOAT:keywords.add("float");break;
                case SHORT:keywords.add("short");break;
                case ELSE:keywords.add("else");break;
                case CASE:keywords.add("case");break;
                case LONG:keywords.add("long");break;
                case ENUM:keywords.add("enum");break;
                case AUTO:keywords.add("auto");break;
                case VOID:keywords.add("void");break;
                case CHAR:keywords.add("char");break;
                case GOTO:keywords.add("goto");break;
                case FOR:keywords.add("for");break;
                case INT:keywords.add("int");break;
                case IF:keywords.add("if");break;
                case DO:keywords.add("do");break;
                default:variables.add(iter);break;
            }
        }
    }
    
    public static void main(String args[]){
        Parser obj=new Parser();
        obj.parse("a");
    
    }
}
