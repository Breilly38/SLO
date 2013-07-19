/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

class TimeSig {
   private int numer;
   private int denom;
   
   public TimeSig( int n, int d ) {
      this.numer = n;
      this.denom = d;
   }
   
   public int getTimeNumerator() { return numer; }
   public int getTimeDenominator() { return denom; }
   
   public void setTimeNumerator( int n ) { this.numer = n; }
   public void setTimeDenominator( int d) { this.denom = d; }
}

/**
 *
 * @author mattvaughan
 */
public class MusicTime {
   
   private int tempo;          // in bpm
   private TimeSig timeSig;    // time signiture
   private long nextMeasure;   // system time in millis of next measure
   private javax.swing.JTextField measureDisplay = null;
   private javax.swing.JTextField beatDisplay = null;
   private javax.swing.JTextField tempoDisplay = null;
   private int currentMeasure = 0;
   private int currentBeat = 1;
   private int measureLength = 4;
   private boolean currentState = false;
   
   public static MusicTime musicTime = new MusicTime( 60, new TimeSig(4,4) );
   
   private MusicTime( int tempo, TimeSig ts ) {
      this.tempo = tempo;
      timeSig = ts;
      nextMeasure = nextMeasureFromTime( System.currentTimeMillis() );
   }
   
    public static MusicTime getInstance() {
        return musicTime;
    }
   
   public long nextMeasureFromTime( long someTime ) {
      long mspb = beatLength(); // millisecond per beat      
      return someTime + (mspb * timeSig.getTimeNumerator() ) ;
   }
   
   public void clearMeasure() {
      nextMeasure = nextMeasureFromTime( System.currentTimeMillis() ); 
   }
   
   public void advanceNextMeasure() {
      nextMeasure = nextMeasureFromTime( nextMeasure ); 
   }
   
   public long getNextMeasure() { return nextMeasure; }
   public int getTempo()        { return tempo; }
   
   public void setTempo( int tempo )   { 
       this.tempo = tempo; 
       if (tempoDisplay != null) {
            tempoDisplay.setText("" + tempo + "");
       }
   }
   public void setTimeSig( TimeSig ts) { 
       this.timeSig = ts;
   }
  
   public TimeSig getTimeSig() {
       return this.timeSig;
   }
   
   public int getTimeNumerator() { 
       return this.timeSig.getTimeNumerator(); 
   }
   
   // returns beat length in milliseconds per beat
   public long beatLength() {
      Double mspbDouble = new Double( (1.0/tempo) * 60 * 1000 ); // milliseconds per beat
      return mspbDouble.longValue();
   }
   public void setCurrentBeat(int bNum) {
       currentBeat = bNum;
       
       if( beatDisplay != null) {
           beatDisplay.setText("" + currentBeat + "");
       }
   }
   
   public int getCurrentBeat() {
       return currentBeat;
   }
   public void setCurrentMeasure(int mNum) {
        currentMeasure = mNum;

        if (measureDisplay != null) {
            measureDisplay.setText("" + currentMeasure + "");
        }
    }

    public int getCurrentMeasure() {
        return currentMeasure;
    }
    
    public boolean getCurrentState() {
        return currentState;
    }
        
    public void setCurrentState(boolean state) {
        currentState = state;
    }
    
        
   public void setDisplays(javax.swing.JTextField mDisplay, javax.swing.JTextField bDisplay, javax.swing.JTextField tDisplay) {
     measureDisplay = mDisplay;
     beatDisplay = bDisplay;
     tempoDisplay = tDisplay;
   }
}

