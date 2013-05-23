/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mattvaughan
 */
public abstract class Expression {
   
   // every Expression has a type...
   protected String type; 
   public String getType()    { return type; }
   
   public String evalType( DeferredSubst df, String caller ) {
      return getType();
   }
   
   // get the expr as a string 
   public String show( DeferredSubst df, String caller ) {
      return this.eval(df, caller).show(df, caller);      // the right thing for non-bottoms
   }
  
   // evaluate to a bottom expression... bottoms return themselves.
   public Expression eval( DeferredSubst df, String caller ) { return this; }
}

class Void extends Expression {
   
   public Void() {
      type = "void";
   }
   
   @Override
   public String show( DeferredSubst df, String caller ) {
      return "#<void>";
   }
   
}

// Variables!!! Yeah!
class ID extends Expression {
   
   private String name;
   
   public ID( String name ) {
      this.name = name;
      type = "id";
   }
   
   public String getName() {
      return name;
   }
   
   @Override
   public String evalType( DeferredSubst df, String caller ) {
      return df.lookup(name).getType();
   }
      
   @Override
   public Expression eval( DeferredSubst df, String caller ) {

      // finds our deferred Substition in the environment

      // if the ID's value is another ID
      // then look it up in the next scope up!
      if ( df.lookup(name).getType().compareTo("id") == 0 ) {
         String innerID = ((ID) df.lookup(name)).getName();
         
         // DEBUG OUTPUT
         if ( Interpreter.DEBUG ) {
            System.out.println("My name was "+ name + "inner ID was" + innerID );
         }
         
         // lookup the ID value (another ID name) in the parent of this deferredSubstitution
         //   which is the next scope up...
         if ( df.getParent() == null ) {
            System.err.println("Failed lookup in ID.getValue() because df.getParent() is null...");
            System.err.println("This means we're in the root scope we've encountered a ID with an ID as it's value.");
            System.err.println("This is an unbound ID exception. Exiting.");
            System.exit(1);
         }
         return df.lookup(innerID).eval( df.getParent(), caller );
      }
      return df.lookup(name).eval(df, caller);
   }
}

class QuoteList extends Expression {
 
   private List<Expression> elements;
   
   public QuoteList( List<Expression> elements ) {
      /*
      for( int i = 0; i < elements.size(); ++i ) { 
         Expression newElem = elements.get(i).eval( DeferredSubst.getInstance() );
         elements.set(i, newElem);
      }*/
      
      this.elements = elements;
      type = "quotelist";
   }
   
   public List<Expression> getElements() {
      return elements;
   }
  
   @Override
   public String show(DeferredSubst df, String caller) {
      String result = "'(";
      
      for ( int i = 0; i < elements.size(); ++i ) {
         result = result.concat( elements.get(i).show(df, caller) );
         
         if ( i != elements.size() - 1 ) {
            result = result.concat(" ");
         }
      }
      result = result.concat(")");
      
      return result;
   }
   
}

class Value extends Expression {
   
   private String value;
   
   public Value( String strVal ) {
      value = strVal;
      
      // test to see which type of value it is
      if       ( value.compareTo("#t") == 0 || value.compareTo("#f") == 0) {
         type = "boolean";
      }
      else if  ( value.matches("-?\\d+") ) {
         type = "integer";
      }
      else if  ( value.matches("-?\\d*.\\d+") ) {
         type = "decimal";
      }
      else if  ( value.startsWith("\"") && value.endsWith("\"") ) {
         type = "string";
      }
      else {
         // you should never get here if called from interp()
         System.err.println("Error in Value Constructor: Unknown Type '" + strVal + "'");
         type = "undefined";
      }
   }
  
   @Override
   public String show(DeferredSubst df, String caller) {
      return value;
   }
}

class Function extends Expression {
   
   private QuoteList arguments;
   private Tree.Node<String> body;
   
   public Function( QuoteList args, Tree.Node<String> preInterpBody ) {
      type = "function";
      arguments = args;
      this.body = preInterpBody;
   }

   @Override
   public Expression eval(DeferredSubst df, String caller) {
      return this;
   }
   
   @Override
   public String show(DeferredSubst df, String caller) {
      return "#<function>";
   }
   
   public QuoteList getArgs() {
      return arguments;
   }
   
   public Tree.Node<String> getBody() {
      return body;
   }
}

class Application extends Expression {
   
   private String functionName;           // for core functions
   private Function function;             // for user lambda functions
   private List<Expression> arguments;
   private boolean isCore;                // true for core, false for user lambda
   
   public List<Expression> evalArgs( List<Expression> args, String caller ) {
      List<Expression> newArgs = new ArrayList<Expression>();
      for( int i = 0; i < args.size(); ++i ) {
         newArgs.add( args.get(i).eval(DeferredSubst.getInstance(), caller) );
      }
      return newArgs;
   }
   
   // contructor for core function call
   public Application( String fun, List<Expression> args ) {
      functionName = fun;
      arguments = args;
      type = "app";
      isCore = true;
   }
   
   // constructor for user lambda function call
   public Application( Function fun, List<Expression> args ) {
      function = fun;
      arguments = args;
      type = "app";
      isCore = false;
   }
   
   // constructor had an inner app or other expr as function
   public Application( Expression expr, List<Expression> args, String caller ) {
      
      Expression fun = expr.eval(DeferredSubst.getInstance(), caller);
      
      if ( fun.getType().compareTo("function") != 0) {
         System.err.println("Error in function application constructor: " 
                 + expr.show(DeferredSubst.getInstance(), caller) + " is not a funciton.");
         System.exit(1);
      }
      
      function = (Function) fun;
      arguments = args;
      type = "app";
      isCore = false;
   }
   
   @Override
   public Expression eval(DeferredSubst df, String caller) {
      
      if ( isCore ) {
         // calls function, see CoreFunctions class
         return CoreFunctions.getInstance().lookup(this, df, caller);
      } else {
         
         List<Expression> functionArgs = function.getArgs().getElements();
         Expression body = Interpreter.getInstance().interp( function.getBody(), caller );
         
         DeferredSubst newScope = df.newScope();
         DeferredSubst oldScope = DeferredSubst.setScope( newScope ); // set newScope returns currentScope
         
         if ( functionArgs.size() != arguments.size() ) {
            System.err.println("Function expected " + functionArgs.size() + " arguments and got " + arguments.size());
            System.exit(2);
         }
         
         for ( int i = 0; i < functionArgs.size(); ++i ) {
            // be eager... lazyness doesn't work well with my scoping system
            Expression arg = arguments.get(i).eval(newScope, caller); // this is eager
            newScope.addID( (ID) functionArgs.get(i) , arg );
         }
         
         Expression result = body.eval(newScope, caller);          // get result in local scope
       
         DeferredSubst.setScope(oldScope); // to support equality operations and be otherwise correct
         
         return result;
      }
   }
      
   // a getter for functionName
   public String getFunctionName() {
      return functionName;
   } 
   
   // a getter for arguments
   public List<Expression> getArgs() { 
      return arguments; 
   }
}

