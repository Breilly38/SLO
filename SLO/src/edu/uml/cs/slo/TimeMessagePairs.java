/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.util.Vector;

/**
 *
 * @author mattvaughan
 */
public class TimeMessagePairs {
   
   private static Vector<TimeMessagePair> pairs = new Vector<TimeMessagePair>();
   
   // a getter... for good messure
   public static Vector<TimeMessagePair> getPairs() { return pairs; }
   
   // synchronized... one at a time please
   synchronized public static void addPair( TimeMessagePair newPair ) {
      pairs.addElement( newPair );
   }
   
   // We only need one instance of time message pair
   private TimeMessagePairs () {}
}