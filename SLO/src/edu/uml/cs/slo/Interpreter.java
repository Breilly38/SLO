/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author mattvaughan
 */
public class Interpreter {

   // provide debug output?
   public static boolean DEBUG = false;
   
   // Singleton interpreter
   private static Interpreter interpreter    = new Interpreter();
   public static Interpreter getInstance()   { return interpreter; }
   
   
   // our DeferredSubst which holds our environmnet
   private static DeferredSubst defSubst = DeferredSubst.getInstance(); 

   // private constructor, to enforce singleton pattern
   private Interpreter() {}

   // prepare statement for tokenization
   // returns an statement with spaces before and after '(' and ')'
   public String prepare(String statement) {
      return statement.replace("(", " ( ").replace(")", " ) ");
   }
   
   // here's where the magic happens
   //  This is the interpreter function.
   //  Each Expression returned by the recurive interp is evaluated (executed) here
   public String interp( String statement, String caller ) {

      // get a string tokenizer for our statement
      StringTokenizer st = new StringTokenizer(prepare(statement));
      
      // and the tokenizer to the buildAST function, which gives us back a tree of strings...
      Tree<String> tree = buildAST(st);
      Tree.Node<String> node = tree.getNode();
      
      // print the AST (if we're in debug mode)
      if ( DEBUG ) { printVisit( node, 0 ); }
      
      // gets the child of 'root'
      // each child is one executable statement
      List<Tree.Node<String>> children = node.getChildren();
      
      // evaluate each child and return the LAST one as the result
      //  if there were no children, then return void
      //  obiously, if there was only ONE statement then return that expression as the result
      String result = "#<void>"; 
      for( int i = 0; i < children.size(); ++i ) {
            result = interp( children.get(i), caller ).show(defSubst, caller);
      }
      
      return result;
   }
   
   // recursive interp, called on node of a tree built by interp( string )
   public Expression interp( Tree.Node<String> node, String caller ) {
      
      // it's a function application
      if ( node.getData().compareTo("@") == 0 ) {
         List<Tree.Node<String>> children = node.getChildren();
         List<Expression> args = new ArrayList<Expression>();
         
         for ( int i = 1; i < children.size(); ++i ) {
            args.add( interp( children.get(i), caller ) );
         }

         // app of an app!
         if ( children.get(0).getData().compareTo("@") == 0 ) {
            return new Application( interp(children.get(0), caller  ), args, caller );
         }
         // app of an anonymous user funciton
         if ( children.get(0).getData().compareTo("lambda") == 0  ) {
            return new Application( (Function) interp( children.get(0), caller  ), args );
         }
         // app of a named user function
         else if ( defSubst.hasID( children.get(0).getData()) ) {
            return new Application(
                    (Function) defSubst.lookup( children.get(0).getData() ), 
                    args );
         }
         // app of a core function
         else {
            return new Application( children.get(0).getData(), args );
         }
      }
      // it's a user lambda function
      else if ( node.getData().compareTo("lambda") == 0 ) {
         List<Tree.Node<String>> children = node.getChildren();
         
         if ( children.size() != 2 ) {
            System.err.println("lambda expects a quotelist and expression!");
            System.exit(1);
         }
         
         Expression args = interp( children.get(0), caller  );
         if ( args.getType().compareTo("quotelist") == 0 ) {
            //return new Function( (QuoteList) args, interp( children.get(1) ) );
            return new Function( (QuoteList) args, children.get(1) );
         }
         else {
            System.err.println("Lambda expects a quotelist as first argument!");
            System.exit(1);
            return new Void(); // should never get here
         }
      }
      // it's a void
      else if ( node.getData().compareTo("#<void>") == 0 ) {
         return new Void();
      }
      // it's a list
      else if ( node.getData().compareTo("'") == 0 ) {
         List<Tree.Node<String>> children = node.getChildren();
         List<Expression> args = new ArrayList<Expression>();
         
         for ( int i = 0; i < children.size(); ++i ) {
            args.add( interp( children.get(i), caller ) );
         }
         
         return new QuoteList( args );
      }
      // it's a number (integer or decimal
      else if (   node.getData().matches( "-?\\d+") || 
                  node.getData().matches( "-?\\d*.\\d+" ) ) {
         return new Value( node.getData() );
      }
      // it's a string
      else if ( node.getData().startsWith("\"") && node.getData().endsWith("\"") ) {
         return new Value( node.getData() );
      }
      // it's a boolean
      else if ( node.getData().compareTo("#t") == 0 || node.getData().compareTo("#f") == 0 ) {
         return new Value( node.getData() );
      }
      // otherwise... it must be an identifier!
      else {
         return new ID( node.getData() );
      }
   }
   
   // Used for debuging the tree
   public void printVisit( Tree.Node<String> node, int depth ) {
      
      for ( int i = 0; i < depth; ++i ) {
         System.out.print("-");
      }
      System.out.println( node.getData() );
      
      List<Tree.Node<String>> children = node.getChildren();
      
      for( int i = 0; i < children.size(); ++i ) {
         printVisit( children.get(i), depth+1 );
      }
   } 
   
   // builds an abstract syntax tree
   //  This is called by interp
   public Tree<String> buildAST( StringTokenizer st ) {
      
      // make our tree... the root is the string "root"
      //  if a node has children then it is an application
      //  if not then it is a value
      Tree<String> tree = new Tree<String>("root");
      Tree.Node<String> node = tree.getNode();
            
      
      String token = "";               // used for while loop below
      boolean advanceOnNext = true;    // used for the loop below
      
      // go through each token
      while( st.hasMoreTokens()) {
                  
         if ( advanceOnNext ) {
            token = st.nextToken().trim();
         }
         advanceOnNext = true;
         
         // if our token is a '('
         if ( token.compareTo("(") == 0 ) {
            
            // get our next token... to see if we have a lambda
            if ( st.hasMoreTokens() ) {
               token = st.nextToken().trim();
               advanceOnNext = false;
            }
            
            // it's either a lambda or an application...
            if ( token.compareTo("lambda") == 0 ) {
               node = node.addChild( new Tree<String>("lambda").getNode() );
               advanceOnNext = true;
            } else {
               node = node.addChild( new Tree<String>("@").getNode() );
            }
         }
         // end lists, apps, and lambdas
         else if ( token.compareTo(")") == 0 ) {
            node = node.getParent();
         }
         // lists
         else if ( token.compareTo("'") == 0 ) {
            
            //get our next token... to see if its '('
            if ( st.hasMoreTokens() ) {
               token = st.nextToken().trim();
               advanceOnNext = false;
               
               // if the next token is a '('
               if ( token.compareTo("(") == 0  ) {
                  node = node.addChild( new Tree<String>("'").getNode() );
                  advanceOnNext = true;
               }
            }                    
         }
         // otherwise... (ID's value's...)
         else {
            node.addChild( new Tree<String>( token ).getNode() );
         }
      }
      
      return tree;
   }
}
