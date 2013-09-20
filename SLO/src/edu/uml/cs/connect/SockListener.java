/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author mattvaughan
 */
public class SockListener implements Runnable {
      
   private Thread t;
   private ServerSocket serverSocket;
   
   public SockListener () {
      try {
         serverSocket = new ServerSocket( 40111 );
      }
      catch ( IOException e ) {
         System.err.println("Error occured Opening Socket");
      }
      
      t = new Thread( this, "SocketListener" );
      t.start();
   
   }
   
   
   @Override
   public void run() {
         
      while( true ) {
         
         Socket clientSocket = null;
      
         try {
            clientSocket = serverSocket.accept();
         }
         catch ( IOException e ) {
            System.err.println("WSSListener.run(): Error accepting connection");
         }
      
      
         if ( clientSocket != null ) {
            new ClientConnection(clientSocket);
         }
      } // end while
   }
}