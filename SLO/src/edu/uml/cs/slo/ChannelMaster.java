// NOTE TO SELF - Drums are on MIDI channel 10
package edu.uml.cs.slo;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Channel Master
 * @author mattvaughan
 */
public class ChannelMaster {
   
   public static ChannelMaster channelMaster = new ChannelMaster();
   
   private int firstAvailable;

   private Map<String,Integer> chanMap = new HashMap<String,Integer>();
   private ChannelMaster() {
      firstAvailable = 1;
   }
   
   synchronized public int getChannel( String caller ) {
      
      if ( chanMap.containsKey(caller) ) {
         return chanMap.get(caller).intValue();
      } else {
         chanMap.put(caller, new Integer( firstAvailable ) );
         firstAvailable++;
         return chanMap.get(caller).intValue();
      }
   }

/*   public void setDrumMode( String caller, boolean drumMode ) throws Exception {
      if ( chanMap.containsKey(caller) ) {
         chanMap.get(caller).setDrumMode(drumMode);
      } else {
         throw new Exception("Key for caller: " + caller + " is not in HashMap");
      }
   }*/
}

/*
class currentChannel {
   private int channel;
   private boolean drumMode;
   
   public currentChannel( int ch ) { channel = ch; drumMode = false; }
   
   public void setDrumMode( boolean dm )  { drumMode = dm; }
   
   public int getCurrentChannel() {
      if ( drumMode ) return 10;
      else return channel;
   }
} */
