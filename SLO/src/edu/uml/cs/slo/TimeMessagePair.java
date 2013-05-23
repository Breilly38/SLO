/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * 
 * @author mattvaughan
 */
public class TimeMessagePair {
      
   private long timeInMillis;             // time to send this message
   private ShortMessage midiMessage;      // midi message
   
   public TimeMessagePair( long timeInMillis, int message, int channel, int noteNumber, int velocity ) {
      
      midiMessage = new ShortMessage();
      this.timeInMillis = timeInMillis;
      
      try {
         midiMessage.setMessage(message, channel, noteNumber, velocity);
      } catch (InvalidMidiDataException ex) {
         System.err.println("TimeMessagePair.TimeMessagePair: Error making message");
      } catch ( Exception e ) {
         System.err.println("TimeMessagePair.TimeMessagePair: Unexpected Error");
      } 
   }
   
   public long getTime()         { return timeInMillis; }
   
   public void sendMessage() {
      
      // the midi receiver to which we will send our message
      Receiver rcvr = OurReceiver.getInstance().getReceiver();
      
      sendMessage( rcvr );
   }
   
   public void sendMessage( Receiver rcvr ) {
      
      try {
         rcvr.send(midiMessage, -1);   // -1 timestamp means 'do it now'
      }
      catch ( Exception e ) {
         System.err.println("TimeMessagePair.sendMessage(Receiver): Error sendingMessage");
      }
   }
}

class OurReceiver {
   
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