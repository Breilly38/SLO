/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author mattvaughan
 */
public class Console implements Runnable {

   Thread t;
   BufferedReader bufferRead; // stdIn

   Console() {

      bufferRead = new BufferedReader(new InputStreamReader(System.in));

      t = new Thread(this, "Console Thread");
      t.start();
   }

   public void run() {

      while (true) {
         String consoleCmd = getIn();

         String value = Interpreter.getInstance().interp( consoleCmd, "Server Console" );
         
         System.out.println("Evaluates to: " + value );
         /*
         long msgTime = System.currentTimeMillis()
                 + (1000 * new Integer(consoleCmd.split(",")[0]).intValue());

         int message = ShortMessage.NOTE_OFF;
         if (consoleCmd.split(",")[1].compareTo("noteOn") == 0) {
            message = ShortMessage.NOTE_ON;
         }
         else {
            message = ShortMessage.NOTE_OFF;
         }

         int channel = new Integer(consoleCmd.split(",")[2]).intValue();
         int noteNumber = new Integer(consoleCmd.split(",")[3]).intValue();
         int velocity = new Integer(consoleCmd.split(",")[4]).intValue();

         System.out.println(msgTime + "," + message + "," + channel + "," + noteNumber + "," + velocity);
         TimeMessagePair newPair = new TimeMessagePair(msgTime, message, channel, noteNumber, velocity);

         TimeMessagePairs.addPair(newPair);
         
         */
      }
   }

   public String getIn() {


      String in = ""; // initialize an empty string
      try {
         in = bufferRead.readLine();   // get input from StdIn
      } catch (IOException ex) {
         System.err.println("Failed to get input");
         System.exit(1);
      }

      return in;
   }
}