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
        long beatLength = MusicTime.musicTime.beatLength();
        long beatTwo = 0, beatThree = 0, beatFour = 0;

        while (true) {

            if (MusicTime.musicTime.getCurrentState()) {  // Our On/Off boolean

                Vector<TimeMessagePair> ourVec = TimeMessagePairs.getPairs();
                for (int i = 0; i < ourVec.size(); ++i) {

                    if (beatFour <= System.currentTimeMillis() && (MusicTime.musicTime.getCurrentBeat() < 4)) {
                        MusicTime.musicTime.setCurrentBeat(4);
                    } else if (beatThree <= System.currentTimeMillis() && (MusicTime.musicTime.getCurrentBeat() < 3)) {
                        MusicTime.musicTime.setCurrentBeat(3);
                    } else if (beatTwo <= System.currentTimeMillis() && (MusicTime.musicTime.getCurrentBeat() < 2)) {
                        MusicTime.musicTime.setCurrentBeat(2);
                    }

                    // if it's time, send the message and remove the timeMessagePair from the vector
                    if (ourVec.get(i).getTime() <= System.currentTimeMillis()) {
                        ourVec.get(i).sendMessage();
                        ourVec.remove(i);
                        --i;
                    }
                }

                if (beatFour <= System.currentTimeMillis() && (MusicTime.musicTime.getCurrentBeat() < 4)) {
                    MusicTime.musicTime.setCurrentBeat(4);
                } else if (beatThree <= System.currentTimeMillis() && (MusicTime.musicTime.getCurrentBeat() < 3)) {
                    MusicTime.musicTime.setCurrentBeat(3);
                } else if (beatTwo <= System.currentTimeMillis() && (MusicTime.musicTime.getCurrentBeat() < 2)) {
                    MusicTime.musicTime.setCurrentBeat(2);
                }

                // if we're past the next measure, advance the next measure
                if (MusicTime.musicTime.getNextMeasure() <= System.currentTimeMillis()) {
                    MusicTime.musicTime.advanceNextMeasure();
                    MusicTime.musicTime.setCurrentMeasure(mNum++);
                    MusicTime.musicTime.setCurrentBeat(1);
                    beatTwo = beatLength + System.currentTimeMillis();
                    beatThree = beatLength + beatTwo;
                    beatFour = beatLength + beatThree;
                }
            } else {
                if (MusicTime.musicTime.getCurrentBeat() != 1) {
                    MusicTime.musicTime.setCurrentBeat(1);
                    beatTwo = 0; 
                    beatThree = 0; 
                    beatFour = 0;
                }
                if (MusicTime.musicTime.getCurrentMeasure() != 1) {
                    MusicTime.musicTime.setCurrentMeasure(1);
                    mNum = 1;
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