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
                
                Vector<TimeMessagePair> ourVec = TimeMessagePairs.getPairs();
                for (int i = 0; i < ourVec.size(); ++i) {
                    
                    for( int j = numerator - 1; j > 0; j-- ) {
                        if( (MusicTime.score.getBeat(j) <= System.currentTimeMillis()) && (MusicTime.score.getCurrentBeat() < j+1)) {
                            MusicTime.score.setCurrentBeat(j+1);
                            break;
                        }
                    }
                    //if (beatFour <= System.currentTimeMillis() && (MusicTime.score.getCurrentBeat() < 4)) {
                    //    MusicTime.score.setCurrentBeat(4);
                    //} else if (beatThree <= System.currentTimeMillis() && (MusicTime.score.getCurrentBeat() < 3)) {
                    //    MusicTime.score.setCurrentBeat(3);
                    //} else if (beatTwo <= System.currentTimeMillis() && (MusicTime.score.getCurrentBeat() < 2)) {
                    //    MusicTime.score.setCurrentBeat(2);
                    //}

                    // if it's time, send the message and remove the timeMessagePair from the vector
                    if (ourVec.get(i).getTime() <= System.currentTimeMillis()) {
                        ourVec.get(i).sendMessage();
                        ourVec.remove(i);
                        --i;
                    }
                }

                for( int j = numerator - 1; j > 0; j-- ) {
                    if( (MusicTime.score.getBeat(j) <= System.currentTimeMillis()) && (MusicTime.score.getCurrentBeat() < j+1)) {
                        MusicTime.score.setCurrentBeat(j+1);
                        break;
                    }
                }

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
            }
        }
        /*
         * Alas, ConcurrentModificationExceptions are unfriendly with iterators 
         * while (true) { 
         * // lets get an iterator 
         * Iterator<TimeMessagePair> it =
         * TimeMessagePairs.getPairs().iterator();
         *
         * // for each message, check the time while (it.hasNext()) {
         *
         * TimeMessagePair timeMsg = it.next();
         *
         * // if the time is right send the message if (timeMsg.getTime() <=
         * System.currentTimeMillis()) { timeMsg.sendMessage(); it.remove(); } } }
         */
    }
}