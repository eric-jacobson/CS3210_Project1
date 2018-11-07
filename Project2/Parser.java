/*
    This class provides a recursive descent parser 
    for Corgi (a simple calculator language),
    creating a parse tree which can be interpreted
    to simulate execution of a Corgi program
*/

import java.util.*;
import java.io.*;

public class Parser {

   private Lexer lex;

   public Parser( Lexer lexer ) {
      lex = lexer;
   }
   //perfect now
   public Node parseProgram() {
      System.out.println("-----> parsing <program>:");

      Node first = parseFuncCall();

      // Look ahead to see if a funcDef follows the funcCall
      Token token = lex.getNextToken();
      
      if(token.isKind("eof")){
        return new Node("prgrm", first, null, null);
      } else {
        lex.putBackToken(token);
        Node second = parseFuncDefs();
        return new Node("prgrm", first, second, null);
      }
   }
   //perfect now
   private Node parseFuncCall(){
      System.out.println("-----> parsing <funcCall>:");

      Token token = lex.getNextToken();

      String function = token.getDetails();
      
      token = lex.getNextToken();
      errorCheck( token, "single", "(" );
      token = lex.getNextToken();
      //change
      if(token.matches("single",")")){

        return new Node("fcall", function, null, null, null);

      } else {
        lex.putBackToken(token);
        Node first = parseArgs();
        token = lex.getNextToken();
        errorCheck(token, "single", ")");
        return new Node("fcall", function, first, null, null);
      }
          
   }
  //perfect now
   private Node parseFuncDefs(){
      System.out.println("-----> parsing <funcDefs>:");

      Node first = parseFuncDef();

      // Look ahead to see if there are more funcDefs
      Token token = lex.getNextToken();

      if(token.isKind("eof")){
        return new Node("fdefs", first, null, null);
      } else {
          lex.putBackToken(token);
          Node second = parseFuncDefs();
          return new Node("fdefs", first, second, null);
      }

   }
   //perfect now
   private Node parseFuncDef(){
      System.out.println("-----> parsing <params>:");
      
      Token token = lex.getNextToken();

      errorCheck( token, "var", "def" );
      token = lex.getNextToken();
      String function = token.getDetails();
      token = lex.getNextToken();
      errorCheck( token, "single", "(" );
      token = lex.getNextToken();

      if(token.matches("single", ")")){
         token = lex.getNextToken();

         if(token.getDetails() == "end"){
            return new Node("fdef", function, null, null, null);
         } else {
            lex.putBackToken(token);
            Node second = parseStatements();
            return new Node("fdef", function, null, second, null);
         }
      } else {
         lex.putBackToken(token);
         Node first = parseParams();
         token = lex.getNextToken();
         //change
         errorCheck( token, "single", ")" );
         token = lex.getNextToken();


         if(token.getDetails() == "end"){
            return new Node("fdef", function, first, null, null);
         } else {
            lex.putBackToken(token);
            Node second = parseStatements();
            return new Node("fdef", function, first, second, null);
         }
      }
   }
   //perfect now
   private Node parseParams(){
      System.out.println("-----> parsing <params>:");

     

      Token token = lex.getNextToken();
      String param = token.getDetails();
      token = lex.getNextToken();
      //change 
      if(token.isKind("eof")){
        return new Node("params", param, null, null, null);      
      } 
      else {
        errorCheck(token, "single", ",");
        Node first = parseParams();
        return new Node("params", param, first, null, null);
      }
   }
   //perfect now
   private Node parseArgs(){
      System.out.println("-----> parsing <args>:");

      Node first = parseExpr();

      Token token = lex.getNextToken();
      //change
      if(token.isKind("eof")){
        //lex.putBackToken(token);    // TODO: Verify this is functioning properly
        return new Node("args", first, null, null);
      } else {
        errorCheck(token, "single", ",");
        Node second = parseArgs();
        return new Node("args", first, second, null);
      }
   }
  // temporay perfect
   private Node parseStatements() {
      System.out.println("-----> parsing <statements>:");
 
      Node first = parseStatement();
 
      Token token = lex.getNextToken();
 
      if ( token.isKind("eof") || token.matches("var", "else") || token.matches("var", "end")) {
         return new Node( "stmts", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      }
   }// <statements>

   private Node parseStatement() {  // TODO: This method needs to be reworked
      System.out.println("-----> parsing <statement>:");
 
      Token token = lex.getNextToken();

      // --------------->>>   <string>
      if ( token.isKind("string") ){
         return new Node( "print", token.getDetails(), null, null, null );
      }

      else if( token.matches("bif0","nl") ) {
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         return new Node( "nl", null, null, null );
      }

      else if( token.matches("bif1", "print")) { 
         token = lex.getNextToken();
         errorCheck(token, "single", "(");
         Node first = parseExpr();
         token =lex.getNextToken();
         errorCheck(token, "single", ")");
         return new Node("print", first, null, null);
      }

      if( token.isKind("bif1") || token.isKind("bif2")) {
        Node first = parseParams();
        token = lex.getNextToken();
        errorCheck( token, "single", "(" );
        return new Node ( token.getDetails(), first, null, null);
     }

      else if ( token.matches("var","return") ){
         Node first = parseExpr();
         return new Node("return", first, null, null);
      }

      // --------------->>>   <var> = <expr>
      else if ( token.isKind("var") ) {
        String varName = token.getDetails();
        token = lex.getNextToken();
        errorCheck( token, "single", "=" );
        Node first = parseExpr();
        return new Node( "sto", varName, first, null, null );
     }

     // --------------->>>   <all if statements>

      else if ( token.isKind("var") && token.getDetails() == "if" ) {
         Node first = parseExpr();
         token = lex.getNextToken();
         if(token.getDetails() == "else"){
            token = lex.getNextToken();
            if(token.getDetails() == "end"){
               return  new Node("if_else", first, null, null);
            }
            else{
               lex.putBackToken(token);
               Node third = parseStatements();
               //token = lex.getNextToken();
               //errorCheck( token, "var", "end");
               return  new Node("if_else", first, null, third);
            }
         }
         else{
            lex.putBackToken(token);
            Node second = parseStatements();
            token = lex.getNextToken();
            //change
            token = lex.getNextToken();
            if(token.getDetails() == "end"){
               return  new Node("if_else", first, second, null);
            }
            else{
              lex.putBackToken(token);
              Node third = parseStatements();
              return  new Node("if_else", first, second, third);
            }
         }
      }
      // --------------  < funcCall>
      else if( token.isKind("var") && token.getDetails() != "if" && token.getDetails() != "return" ) {
         Token temp = lex.getNextToken();
         //change--- may should not consider this case since it already did <var> = <expr>
         //if(temp.getDetails() == "=") {
            //String var = token.getDetails();
            //Node first = parseExpr();
            //change
            //errorCheck(temp, "single", "=");
            //return new Node("sto", var, first, null, null);
         //}
         if(temp.getDetails() == "(") {
            lex.putBackToken(temp);
            lex.putBackToken(token);
            return parseFuncCall();
         }
         else {
            System.out.println("Statement cannot start with " + token );
            System.exit(1);
            return null;
         }
      }
      //change
      else if( token.isKind("var") && token.getDetails() == "return" ){
         Node first = parseExpr();
         return  new Node("return", first, null, null); 
      else {
         System.out.println("Statement cannot start with " + token );
         System.exit(1);
         return null;
      }
   }// <statement>
   //perfect now
   private Node parseExpr() {
      System.out.println("-----> parsing <expr>");

      Node first = parseTerm();

      Token token = lex.getNextToken();
 
      if ( token.matches("single", "+") ||
           token.matches("single", "-") 
         ) {
         Node second = parseExpr();
         return new Node( token.getDetails(), first, second, null );
      }
      else {
         lex.putBackToken( token );
         return first;
      }

   }// <expr>
   //perfect now
   private Node parseTerm() {
      System.out.println("-----> parsing <term>");

      Node first = parseFactor();

      Token token = lex.getNextToken();
 
      if ( token.matches("single", "*") || token.matches("single", "/")) {
         Node second = parseTerm();
         return new Node( token.getDetails(), first, second, null );
      } else {
         lex.putBackToken( token );
         return first;
      }
      
   }// <term>
   // perfect now
   private Node parseFactor() {
      System.out.println("-----> parsing <factor>");

      Token token = lex.getNextToken();

      if ( token.isKind("num") ) {
         return new Node("num", token.getDetails(), null, null, null );
      }
      
      else if ( token.isKind("var") ) {
        
      
        Token temp = lex.getNextToken();
        if(temp.matches("single", "(")){
          lex.putBackToken(temp);
          lex.putBackToken(token);
          Node first = parseFuncCall();
          return first;
        }
        else {
          lex.putBackToken(temp);
        }
        return new Node("var", token.getDetails(), null, null, null );
      }
      else if ( token.matches("single","(") ) {
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         return first;
      }
      else if ( token.isKind("bif0") ) {
         String bif = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         return new Node( bif, null, null, null );
      }
      else if ( token.isKind("bif1") ) {
         String bif = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );         
         return new Node( bif, first, null, null );
      }
      else if ( token.isKind("bif2") ) {
         String bif = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", "," );
         Node second = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );         
         return new Node( bif, first, second, null );
      }
      else if ( token.matches("single","-") ) {
         Node first = parseFactor();
         return new Node("opp", first, null, null );
      }
      else {
         System.out.println("Factors can not start with " + token );
         System.exit(1);
         return null;
      }
      
   }// <factor>

  private void errorCheck( Token token, String kind ) {
    if( ! token.isKind( kind ) ) {
      System.out.println("Error:  expected " + token + 
                         " to be of kind " + kind );
      System.exit(1);
    }
  }

  private void errorCheck( Token token, String kind, String details ) {
    if( ! token.isKind( kind ) || 
        ! token.getDetails().equals( details ) ) {
      System.out.println("Error:  expected " + token + 
                          " to be kind = " + kind + 
                          " and details = " + details );
      System.exit(1);
    }
  }

}
