/*
 * Matthew Vaughan
 * 
 * Got some code from http://muhdazmilug.blogspot.com/2011/12/string-to-sha1-hash-base64.html
 * for the base64 encoding of the hash...
 */


package edu.uml.cs.connect;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Encoder;

import edu.uml.cs.slo.Interpreter;

/**
 *
 * @author mattvaughan
 */
public class ClientConnection implements Runnable {

   public static boolean DEBUG = false; // should we print debug output?
  
   
   private Thread t;
   private Socket client; 
   
   public ClientConnection(Socket cs) {

      client = cs;
      
      t = new Thread(this, "Client: " + client.toString() );
      t.start();
   }
   
   public void run() {
      
      try { 
         BufferedReader in          = new BufferedReader(new InputStreamReader(client.getInputStream()));
         
         // get input from socket forever...
         while ( true ) {
            
            String message = in.readLine();
            
            System.out.println(message);
            
            // invoke the interpreter
            String value = Interpreter.getInstance().interp(message, t.getName() );
            System.out.println("Evaluated to " + value);

         }
         
      } catch (Exception e) {
         System.err.println("There was an exception in ClientConnection.run()");
         System.err.println("Someone probably disconnected");
         //System.exit(1);
      }
      
   }
   
/* OLD WEBSOCKET VERSION
 * 
   public void run() {

      try {
         BufferedReader in          = new BufferedReader(new InputStreamReader(client.getInputStream()));
         DataInputStream in_binary  = new DataInputStream(new BufferedInputStream(client.getInputStream()));
         
         handshake( in );

         while ( true) {
            
            byte[] theBytes = new byte[4096];               // the whole message (type, length, mask, message)
            byte[] mask = new byte[4];                      // stores our bitmask 
            int numBytesRead = in_binary.read(theBytes);    // read from our socket
            int indexToMask = 2;                            // index into 'theBytes' where the mask starts (default is 2)
            int indexToData = 6;                            // index into 'theBytes' where the data starts (defualt is 6)
            
            
            int length = theBytes[1] & 0x7F; // the the length (by masking out the left most bit)
            
            // There are two special cases for length 126 and 127 (after masking).
            //  126 means the next two bytes are for length   (because the length wouldn't fit in one byte)
            //  127 meant the next eight bytes are for length (becuase the length wouldn't fit in two bytes)
            // With this in mind we now find the index into the data
            if ( length == 126 ) {
               indexToMask = 4;
               indexToData = 8;
            }
            if ( length == 127 ) {
               indexToMask = 10;
               indexToData = 14;
            }
            
            if ( DEBUG ) {
               System.out.println("Read in " + numBytesRead + " byte(s)" );
               System.out.println("The value of the first byte was " + theBytes[0] );  // should be -127 (129)
               System.out.println("The second byte (which is the length) is " + length );
            }
            
            mask[0] = theBytes[indexToMask];
            mask[1] = theBytes[indexToMask+1];
            mask[2] = theBytes[indexToMask+2];
            mask[3] = theBytes[indexToMask+3];
            
            String message = "";
            // now we XOR the mask and data to get the decoded message 
            for( int i = 0; i < (numBytesRead - indexToData); ++i ) {
               
               // what we really want to do is make a new 'byte[]' that size is 'numBytesRead - indexToData'
               // and fill it with the new data
               message = message + String.valueOf( Character.toChars( theBytes[indexToData + i] ^ mask[ i % 4 ] ) );    
            }
            
               System.out.println(message);
               // invoke the interpreter
               String value = Interpreter.getInstance().interp(message, t.getName() );
               System.out.println("Evaluated to " + value);
         }
      }
      catch ( IOException e ) {
         System.err.println("WSSClientConnection.run(): Error with client thread");
      }
      catch ( NoSuchAlgorithmException e ) {
         System.err.println("HandShake Failed: NoSuchAlgorithmException");
      }
      
   }
   
   private void handshake( BufferedReader in ) throws IOException, NoSuchAlgorithmException {

      MessageDigest sha1 = MessageDigest.getInstance("SHA1");              // our sha-1, for hashing the input key       
      PrintWriter out = new PrintWriter(client.getOutputStream(), true );  // output to the socket

      char[] cStr = new char[4096];
      in.read(cStr);
      String header = new String(cStr);
      
      if ( DEBUG ) { System.out.println( "From handshake:\n" + header ); }
      
      String key = header.split("Sec-WebSocket-Key: ")[1].split( "\r\n" )[0].trim();      
      
      if ( DEBUG ) { System.out.println(key); }
      
      key = key.concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11"); // adds magic string (globally unique identifier)
            
      // Hash and 64-bit encode our key to get our reponse
      byte[] bytes = key.getBytes(("UTF-8"));
      sha1.update(bytes);
      byte[] digest = sha1.digest();
      String hash = (new BASE64Encoder()).encode(digest);
      
      if ( DEBUG ) { System.out.println( hash ); }
           
      // build our reply to the client
      String reply = "HTTP/1.1 101 Switching Protocols\r\n" +
                     "Upgrade: websocket\r\n" +
                     "Connection: Upgrade\r\n" +
                     "Sec-WebSocket-Accept: " + hash + "\r\n\r\n";
      
      if ( DEBUG ) { System.out.println("*****Reply**********\n"+reply); } 
      
      // send our reply to complete the handshake!
      out.print(reply);
      out.flush();
   } 
   
   */
}
