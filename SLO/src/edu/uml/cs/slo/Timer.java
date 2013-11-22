/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author mattvaughan
 */
public class Timer implements Runnable {

    Thread t;

    Timer() {
        t = new Thread(this, "Timing Thread");
        t.start();
    }
    
    public void run() {
        int mNum = 1;
        MusicTime.score.initBeats();
        //long beatTwo = 0, beatThree = 0, beatFour = 0;

        while (true) {
            
            if (MusicTime.score.getCurrentState()) {  // Our On/Off boolean
                int numerator = MusicTime.score.getTimeNumerator();
                              
                // First measure through gets beat values
                // Has to be here instead of in init because of time dependency
                if(MusicTime.score.isFirstMeasure()) {
                    MusicTime.score.setBeats(numerator, MusicTime.score.beatLength(), System.currentTimeMillis());
                    MusicTime.score.setFirstMeasure(false);
                }
                
                // Numerator and denominator stuff for the GUI
                for( int j = numerator - 1; j > 0; j-- ) {
                     if( (MusicTime.score.getBeat(j) <= System.currentTimeMillis()) && (MusicTime.score.getCurrentBeat() < j+1)) {
                        MusicTime.score.setCurrentBeat(j+1);
                     break;
                  }
                }
                
                // This is where we play the notes (if it's time)
                // The other synchronized() is in TimeMessagePairs. 
                // We don't want to change the vector while it's being iterated...
                // note... this part of the loop is linear on the number of elements in the vector
                synchronized ( TimeMessagePairs.TimeVecLock ) {
                  Iterator<TimeMessagePair> it = TimeMessagePairs.getPairs().iterator();
                  while ( it.hasNext() ) {

                    TimeMessagePair timeMsg = it.next();
                    if ( System.currentTimeMillis() >= timeMsg.getTime() ) {
                       timeMsg.sendMessage();
                       it.remove();
                    }  
                  } // end while
                } // end synchronized()

                // if we're past the next measure, advance the next measure
                if (MusicTime.score.getNextMeasure() <= System.currentTimeMillis()) {
                    MusicTime.score.advanceNextMeasure();
                    MusicTime.score.setCurrentMeasure(++mNum);
                    MusicTime.score.setCurrentBeat(1);
                    numerator = MusicTime.score.getTimeNumerator();
                    
                    MusicTime.score.setBeats(numerator, MusicTime.score.beatLength(), System.currentTimeMillis());
                    //beatTwo = beatLength + System.currentTimeMillis();
                    //beatThree = beatLength + beatTwo;
                    //beatFour = beatLength + beatThree;
                }
            } else {
                if (MusicTime.score.getCurrentBeat() != 1) {
                    MusicTime.score.setCurrentBeat(1);
                    
                    MusicTime.score.initBeats();
                }
                if ((MusicTime.score.getCurrentMeasure()) != 1) {
                   mNum = 1; 
                   MusicTime.score.setCurrentMeasure(1);
                    
                }
            } // end else
        } // end while(true)
    } // end run()
}