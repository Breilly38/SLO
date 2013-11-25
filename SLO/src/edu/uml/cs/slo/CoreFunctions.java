/*
 * Hello! The core functions are stored here
 * All other functions are defined using the core function 'define'
 * 'define'
 */
package edu.uml.cs.slo;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * Singleton set of core functions
 *
 * @author mattvaughan
 */
public class CoreFunctions {

   // singleton pattern...
   private static CoreFunctions core            = new CoreFunctions();
   public static CoreFunctions getInstance()    { return core; }
   
   // our environmnent
   private DeferredSubst defSubst;
   
   // our hashmap for functions
   private Map<String, Command>  coreFunctionMap = new HashMap<String, Command>();
   
   // our Command interface
   public interface Command { Expression invoke( List<Expression> args, String caller ); }

   // the lookup function
   // first it
   public Expression lookup(Application app, DeferredSubst df, String caller) {
      
      defSubst = df;
      if ( coreFunctionMap.containsKey( app.getFunctionName()) ) {
         return coreFunctionMap.get( app.getFunctionName() ).invoke( app.getArgs(), caller );
      }
      else {
         return new Value("Function '" + app.getFunctionName() + "' not found!");
      }  
   }

   // our private constructor
   private CoreFunctions() {
      
      // default it the global environment
      defSubst = DeferredSubst.getInstance();
      
      coreFunctionMap.put("drumOn", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return noteOn( args, caller );
         }
      });
      coreFunctionMap.put("drumOff", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return noteOff( args, caller );
         }
      });
      coreFunctionMap.put("noteOn", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return noteOn( args, caller );
         }
      });
      coreFunctionMap.put("noteOff", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return noteOff( args, caller );
         }
      });
      coreFunctionMap.put("define", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return define( args, caller );
         }
      });
      coreFunctionMap.put("put", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return put( args, caller );
         }
      });
      coreFunctionMap.put("+", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return plus( args, caller );
         }
      });
      coreFunctionMap.put("-", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return minus( args, caller );
         }
      });
      coreFunctionMap.put("*", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return times( args, caller );
         }
      });
      coreFunctionMap.put("=", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return equal( args, caller );
         }
      });
      coreFunctionMap.put("if", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return ifStatement( args, caller );
         }
      });
      coreFunctionMap.put("or", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return orStatement( args, caller );
         }
      });
      coreFunctionMap.put("and", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return andStatement( args, caller );
         }
      });
      coreFunctionMap.put("not", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return notStatement( args, caller );
         }
      });
      coreFunctionMap.put("first", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return first( args, caller );
         }
      });
      coreFunctionMap.put("head", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return first( args, caller );
         }
      });
      coreFunctionMap.put("rest", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return rest( args, caller );
         }
      });
      coreFunctionMap.put("tail", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return rest( args, caller );
         }
      });
      coreFunctionMap.put("cons", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return cons( args, caller );
         }
      });
      coreFunctionMap.put("nextMeasure", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return nextMeasure( args, caller );
         }
      });
      coreFunctionMap.put("nm", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return nextMeasure( args, caller );
         }
      });
      coreFunctionMap.put("beat", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return beat( args, caller );
         }
      });
      coreFunctionMap.put("myChannel", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return myChannel( args, caller );
         }
      });
      coreFunctionMap.put("changeInst", new Command() {
         @Override
         public Expression invoke( List<Expression> args, String caller ) {
            return changeInstrument( args, caller );
         }
      });
   }
   
   // (drumOn time noteNumber velocity)
   private Expression drumOn(List<Expression> args, String caller ) {
      // check to make sure we got the right number of arguments
      if ( args.size() != 3 ) {
         System.err.println("drumOn expected 3 arguments and got " + args.size() );
         return new Void();
      } 
      
      long msgTime = System.currentTimeMillis() + new Double( args.get(0).show(defSubst, caller) ).longValue();
      int message = ShortMessage.NOTE_ON;
      int noteNumber = new Integer( args.get(1).show(defSubst, caller) ).intValue();
      int velocity   = new Integer( args.get(2).show(defSubst, caller) ).intValue();
      
      if ( Interpreter.DEBUG ) {
         System.out.println(msgTime + "," + message + "," + noteNumber + "," + velocity);
      }
      
      TimeMessagePair newPair = new TimeMessagePair(msgTime, message, 10, noteNumber, velocity);
      TimeMessagePairs.addPair(newPair);
      
      return new Void();
   }
   
   // (drumOff time noteNumber)
   private Expression drumOff(List<Expression> args, String caller ) {
      // check to make sure we got the right number of arguments
      if ( args.size() != 2 ) {
         System.err.println("drumOff expected 2 arguments and got " + args.size() );
         return new Void();
      } 
      
      long msgTime = System.currentTimeMillis() + new Double( args.get(0).show(defSubst, caller) ).longValue();
      int message = ShortMessage.NOTE_OFF;
      int noteNumber = new Integer( args.get(1).show(defSubst, caller) ).intValue();
      
      if ( Interpreter.DEBUG ) {
         System.out.println(msgTime + "," + message + "," + noteNumber + "," + 60);
      }
      
      TimeMessagePair newPair = new TimeMessagePair(msgTime, message, 10, noteNumber, 60);
      TimeMessagePairs.addPair(newPair);
      
      return new Void();
   }
   
   // (noteOn time channel noteNumber velocity)
   private Expression noteOn(List<Expression> args, String caller) {
      
      // check to make sure we got the right number of arguments
      if ( args.size() != 4 ) {
         System.err.println("noteOn expected 4 arguments and got " + args.size() );
         return new Void();
      } 
      
      long msgTime = System.currentTimeMillis() + new Double( args.get(0).show(defSubst, caller) ).longValue();
      int message = ShortMessage.NOTE_ON;
      int channel    = new Integer( args.get(1).show(defSubst, caller) ).intValue();
      int noteNumber = new Integer( args.get(2).show(defSubst, caller) ).intValue();
      int velocity   = new Integer( args.get(3).show(defSubst, caller) ).intValue();

      if ( Interpreter.DEBUG ) {
         System.out.println(msgTime + "," + message + "," + channel + "," + noteNumber + "," + velocity);
      }
      
      // the side effect of this function is what's important
      TimeMessagePair newPair = new TimeMessagePair(msgTime, message, channel, noteNumber, velocity);
      TimeMessagePairs.addPair(newPair);

      // return void
      return new Void();
   }
   
   // (noteOn time channel noteNumber velocity
   private Expression noteOff(List<Expression> args, String caller) {

      // check to make sure we got the right number of arguments
      if ( args.size() != 4 && args.size() != 3) {
         System.err.println("noteOff expected 3 or 4 arguments and got " + args.size() );
         return new Void();
      } 
      
      long msgTime = System.currentTimeMillis() + new Double( args.get(0).show(defSubst, caller) ).longValue();
      int message = ShortMessage.NOTE_OFF;
      int channel    = new Integer( args.get(1).show(defSubst, caller) ).intValue();
      int noteNumber = new Integer( args.get(2).show(defSubst, caller) ).intValue();
      int velocity   = 0;  // ignore fourth argument... velocity doesn't matter

      if ( Interpreter.DEBUG ) {
      System.out.println(msgTime + "," + message + "," + channel + "," + noteNumber + "," + velocity);
      }
      
      // the side effect of this function is what's important
      TimeMessagePair newPair = new TimeMessagePair(msgTime, message, channel, noteNumber, velocity);
      TimeMessagePairs.addPair(newPair);

      // return void
      return new Void();
   }
   
   private Expression define( List<Expression> args, String caller ) {
      
      // must have two arguemnts
      if ( args.size() == 2 ) {
         
         // first argument must be an id
         // second argument can be any expression
         if (  args.get(0).getType().compareTo("id") == 0 ) {
            
           
            // add our new deferred substitution
            // we checked to make sure it was an ID so casting shouldn't cause a problem
            defSubst.addID( (ID) args.get(0), args.get(1).eval(defSubst, caller) );
         }
      } else {
         System.err.println("define expected 2 arguments and got " + args.size() );
      }
      
      //return void
      return new Void();
   }
   
   // writes something out to the console
   private Expression put( List<Expression> args, String caller ) {
      
      // must have one arguemnts
      if ( args.size() == 1 ) {
         String output = args.get(0).show(defSubst, caller);
         System.out.println(output);
      }
      return new Void();
   }
   
   // (= something something ...)
   private Expression equal( List<Expression> args, String caller ) {
      
      if ( args.size() != 2 ) {
         System.err.println("= expected 2 arguments and got " + args.size() );
         System.exit(1);
      }
      
      DeferredSubst df = DeferredSubst.getInstance();
      // if the values are the same
      if ( args.get(0).show( df, caller ).compareTo( args.get(1).show(df, caller)) == 0 ) {
            return new Value("#t");
      }
      
      // values weren't the same
      return new Value("#f");
   }
   
   // (+ somenumber somenumber ...)
   private Expression plus( List<Expression> args, String caller ) {
      
      boolean ints = true;
      boolean intsOrDecimals = true;
      
      for ( int i = 0; i < args.size(); ++i ) {
         
         // if it's not an int
         if ( ! args.get(i).show(defSubst, caller).matches("\\d+") ) {
            ints = false;
         }
         
         // if it's not an int or a decimal
         if ( ! args.get(i).show(defSubst, caller).matches("\\d+") &&
              ! args.get(i).show(defSubst, caller).matches("\\d*.\\d+") ) {
            intsOrDecimals = false;
         }
      }
      
      // integer addition
      if ( ints ) {
         //System.err.println("INT ARTH");
         Long result = new Long(0);
         for ( int i = 0; i < args.size(); ++i ) {
            result += new Long( args.get(i).show(defSubst, caller) );
         }
         return new Value(result.toString());
      }
      // double precision floating point addition
      else if ( intsOrDecimals ) {
         //System.err.println("DOUBLE ARTH");
         Double result = new Double(0.0);
         for ( int i = 0; i < args.size(); ++i ) {
            result += new Double( args.get(i).show(defSubst, caller) );
         }
         return new Value(result.toString());
      }
      // error condition... 
      else {
         System.err.println("Error: + expected only numbers as arguments");
         return new Void();
      }
   }
   
   private Expression times( List<Expression> args, String caller ) {
      
      boolean ints = true;
      boolean intsOrDecimals = true;
      
      if ( args.size() < 2 ) {
         System.err.println("* expected at least two arguments and got " + args.size() );
         return new Void();
      }
      
      for( int i = 0; i < args.size(); ++i ) {
         
         // if the numner is not an integer
         if ( args.get(i).eval(defSubst, caller).getType().compareTo("integer") != 0 ) {
            ints = false;
            
            // and is not a decimal
            if ( args.get(i).eval(defSubst, caller).getType().compareTo("decimal") != 0 ) {
               intsOrDecimals = false;
            } // end if
         } // end if 
      } // end for
      
      if ( ints ) {
         Long result = new Long(1);
         
         for ( int i = 0; i < args.size(); i++ ) {
            result = result * new Long( args.get(i).show(defSubst, caller) );
         }
         return new Value( result.toString() );
         
      } else if ( intsOrDecimals ) {
         Double result = new Double(1.0);
         
         for( int i = 0; i < args.size(); ++i ) {
            result = result * new Double( args.get(i).show(defSubst, caller) );
         }
         return new Value( result.toString() );
      } else {
         System.err.println("* expected only numbers as arguments");
         return new Void();
      }
   }
   
   // (+ somenumber somenumber ...)
   private Expression minus( List<Expression> args, String caller ) {
      
      boolean ints = true;
      boolean intsOrDecimals = true;
      
      if ( args.size() < 2 ) {
         System.err.println("- expected at least two arguments and got " + args.size() );
         return new Void();
      }

      for ( int i = 0; i < args.size(); ++i ) {
      
         // first evaluate the argument
         args.add(i, args.remove(i).eval(defSubst, caller));
         
         // if it's not an int
         if ( ! args.get(i).show(defSubst, caller).matches("\\d+") ) {
            ints = false;
         }
         
         // if it's not an int or a decimal
         if ( ! args.get(i).show(defSubst, caller).matches("\\d+") &&
              ! args.get(i).show(defSubst, caller).matches("\\d*.\\d+") ) {
            intsOrDecimals = false;
         }
      }
      
      // integer addition
      if ( ints ) {
         //System.err.println("INT ARTH");
         Long result = new Long( args.get(0).show(defSubst, caller) );
         for ( int i = 1; i < args.size(); ++i ) {
            result -= new Long( args.get(i).show(defSubst, caller) );
         }
         return new Value(result.toString());
      }
      // double precision floating point addition
      else if ( intsOrDecimals ) {
         //System.err.println("DOUBLE ARTH");
         Double result = new Double( args.get(0).show(defSubst, caller ) );
         for ( int i = 1; i < args.size(); ++i ) {
            result -= new Double( args.get(i).show(defSubst, caller) );
         }
         return new Value(result.toString());
      }
      // error condition... 
      else {
         System.err.println("Error: - expected only numbers as arguments");
         return new Void();
      }
   }
   
   // (if condition trueExpr falseExpr)
   private Expression ifStatement( List<Expression> args, String caller ) {
      
      if ( args.size() != 3 ) {
         System.err.println("if expected 3 arguments and got " + args.size() );
         System.exit(1);
      }
      
      // if it's false return the second, any other value gets the first
      if ( args.get(0).show(defSubst, caller).compareTo("#f") == 0) {
         return args.get(2).eval(defSubst, caller);
      } else {
         return args.get(1).eval(defSubst, caller);
      }
   }
   
   // (or arg1 arg2 ...)
   private Expression orStatement( List<Expression> args, String caller ) {
      
      // return the first non-false value
      for( int i = 0; i < args.size(); ++i ) {
         if ( args.get(i).show(defSubst, caller).compareTo("#f") != 0 ) {
            return args.get(i).eval(defSubst, caller);
         }
      }
      
      return new Value("#f");      
   }
   
   // (and arg1 arg2 ...)
   private Expression andStatement( List<Expression> args, String caller ) {
      
      // return true by default
      Expression result = new Value("#t");
      
      for( int i = 0; i < args.size(); ++i ) {
         
         result = args.get(i).eval(defSubst, caller);
         
         if ( result.show(defSubst, caller).compareTo("#f") == 0 ) {
            return new Value("#f");
         }
      }      
      return result;      
   }
   
   // (not argument)
   private Expression notStatement( List<Expression> args, String caller ) {
      
      if ( args.size() != 1 ) {
         System.err.println("not expected exactly one argument and got " + args.size() );
         return new Void();
      }
      
      // returns the logical inverse
      if ( args.get(0).show(defSubst, caller).compareTo("#f") == 0 ) {
         return new Value("#t");
      }
      else {
         return new Value("#f");
      }
   }
   
   private Expression first( List<Expression> args, String caller ) {
      
      if ( args.size() != 1 ) {
         System.err.println("first/head expected exactly one argument and got " + args.size() );
         return new Void();
      }
      
      Expression expr = args.get(0).eval(defSubst, caller);
      
      if ( expr.getType().compareTo("quotelist") != 0 ) {
         System.err.println("first/head expected a quotelist as an argument and got a " + expr.getType() );
         return new Void();
      }
      
      QuoteList ourList = (QuoteList) expr;
      
      // the elements of the list
      List<Expression> elements = ourList.getElements();
      
      // if the list is empty -> error
      // otherwise -> return the first element of the list
      if ( elements.size() == 0 ) {
         //return args.get(0).eval(defSubst); // the empty list
         System.err.println("Can't get first element of an empty list");
         return new Void();
      } else {
         return elements.get(0).eval(defSubst, caller);
      }  
   }
   
   private Expression rest( List<Expression> args, String caller ) {
      
      if ( args.size() != 1 ) {
         System.err.println("rest/tail expected exactly one argument and got " + args.size() );
         return new Void();
      }
      
      Expression expr = args.get(0).eval(defSubst, caller);
      
      if ( expr.getType().compareTo("quotelist") != 0 ) {
         System.err.println("rest/tail expected a quotelist as an argument and got a " + expr.getType() );
         return new Void();
      }
      
      QuoteList ourList = (QuoteList) expr;
      
      // the elements of the list
      List<Expression> elements = ourList.getElements();
      
      // if the list is empty -> error
      // otherwise -> return all but the first element of the list
      if ( elements.size() == 0 ) {
         //return args.get(0).eval(defSubst); // the empty list
         System.err.println("Can't get rest of an empty list");
         return new Void();
      } else {
         elements.remove(0);
         QuoteList newList = new QuoteList(elements);
         return newList.eval(defSubst, caller);
      }  
   }
   
   private Expression cons( List<Expression> args, String caller ) {
      
      if ( args.size() != 2 ) {
         System.err.println("cons expected exactly two argument and got " + args.size() );
         return new Void();
      }
      
      Expression evaledArg = args.get(0).eval(defSubst, caller);
      Expression list = args.get(1).eval(defSubst, caller);
      
      if ( list.getType().compareTo("quotelist") != 0 ) {
         System.err.println("cons expected a quotelist as the second argument and got a " + list.getType() );
         return new Void();
      }
      
      QuoteList ourList = (QuoteList) list;
      
      // the elements of the list
      List<Expression> elements = ourList.getElements();
      
      
      // if the list is empty -> error
      // otherwise -> return all but the first element of the list
      elements.add(0, evaledArg );
      QuoteList newList = new QuoteList(elements);
      return newList.eval(defSubst, caller);
         
   }
   
   private Expression nextMeasure( List<Expression> args, String caller ) {
      Long timeFromNow = new Long (MusicTime.score.getNextMeasure() - System.currentTimeMillis());
      return new Value( timeFromNow.toString() );
   }

   private Expression beat( List<Expression> args, String caller ) {
      Long beatLength = new Long( MusicTime.score.beatLength() );
      return new Value( beatLength.toString() );
   }
   
   private Expression myChannel( List<Expression> args, String caller ) {
      
      return new Value( new Integer (ChannelMaster.channelMaster.getChannel(caller)).toString() );
   }
   
   private Expression changeInstrument( List<Expression> args, String caller ) {
      
      int channel = ChannelMaster.channelMaster.getChannel(caller);
      Receiver rcvr = OurReceiver.getInstance().getReceiver();
      ShortMessage instrumentChange = new ShortMessage();

      if ( args.size() != 1 ) {
         System.err.println("(changeInst) expected one argument");
         return new Void();
      }
      if ( args.get(0).eval(defSubst, caller).getType().compareTo("integer") != 0 ) {
         System.err.println("(changeInst) expected an integer as sole argument");
         return new Void();
      }
      
      int instr = new Integer( args.get(0).show(defSubst, caller) ).intValue();
      
      try {
         instrumentChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, instr,0);
         rcvr.send(instrumentChange, -1);
      }
      catch (Exception e) {
         System.err.println("Error in (changeInstr), CoreFunctions.changeInstrument()");
      }
      return new Void();
   }
   
   
   /* OLD DEPRECATED
   private Expression drumMode( List<Expression> args, String caller ) {
      
      if ( args.size() != 1 ) {
         System.err.println("(drumMode) expected one argument");
         return new Void();
      }
      
      if ( args.get(0).eval(defSubst, caller).getType().compareTo("boolean") != 0 ) {
         System.err.println("(drumMode) expected a boolean as sole argument");
         return new Void();
      }
      
      boolean drumMode = false;
      if ( args.get(0).show(defSubst, caller).contentEquals("#t") ) drumMode = true;

      try {
         ChannelMaster.channelMaster.setDrumMode(caller, drumMode);
      } catch (Exception e) {
         System.err.println("CoreFunctions.drumMode(): Could not set drum mode for caller " + caller + " to " + drumMode);
      }
      
      return new Void();
   } */
}
