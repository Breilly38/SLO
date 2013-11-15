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
         return chanMap.get(caller);
      } else {
         chanMap.put(caller, firstAvailable);
         firstAvailable++;
         return chanMap.get(caller);
      }
   }
}
