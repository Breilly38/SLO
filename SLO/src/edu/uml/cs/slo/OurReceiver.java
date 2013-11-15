/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;

/**
 *
 * @author mattvaughan
 */
public class OurReceiver {
   
   // singleton pattern
   private static OurReceiver ourReceiver    = new OurReceiver();
   public static OurReceiver getInstance()   { return ourReceiver; }
   
   private Receiver rcvr;
   
   private OurReceiver() {
      try {
         rcvr = MidiSystem.getReceiver();
      } catch (Exception e) {
         System.err.println("OurReceiver.OurReceiver - Error getting receiver");
         System.exit(1);
      }
   }
   
   // getter
   public Receiver getReceiver() { return rcvr; }
}