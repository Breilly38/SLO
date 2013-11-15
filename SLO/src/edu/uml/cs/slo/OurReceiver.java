/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import javax.sound.midi.*;
import java.util.*;

/**
 *
 * @author ZacharyRobichaud
 */
class OurReceiver {
   
   // singleton pattern
   private static OurReceiver ourReceiver    = new OurReceiver();
   public static OurReceiver getInstance()   { return ourReceiver; }
  
   private int synthNum = 10;
   private Receiver rcvr;
   private static Synthesizer synth;
   private Instrument[] inst;
   private List<Instrument> instList;
   private static List<Instrument> pianoList , guitarList, bassList, 
                    stringList, voiceList, drumList;
   
   
   private OurReceiver() {
     pianoList = new LinkedList();
     guitarList = new LinkedList();
     bassList = new LinkedList();
     stringList = new LinkedList();
     voiceList = new LinkedList();
     drumList = new LinkedList();
     chooseSynth();
   }
   
   // getter
   public Receiver getReceiver() { return rcvr; }
   
   
   public void chooseSynth() {
      try {
         synth = MidiSystem.getSynthesizer();
         synth.open();
         inst = synth.getDefaultSoundbank().getInstruments();
         synth.loadInstrument(inst[9]);
         MidiChannel[] chan = synth.getChannels();
         Patch patch = inst[synthNum].getPatch();
         chan[1].programChange(patch.getBank(), patch.getProgram());
         //System.out.println(chan[0].getProgram());
         rcvr = synth.getReceiver();
    
      } catch (Exception e) {
         System.err.println("OurReceiver.OurReceiver - Error getting receiver");
         System.exit(1);
      }
   }
   
   public void setSynthToInstWithNum(int num) {
       synthNum = num;
   }
   
   /*
   Sets the instrument with name passed in from scratch.
   @param instName = name of instrument
   */
   public static void setInstrument(String instName)  {
       
       System.out.println(instName);
    
       List<Instrument> instList = getInstrumentsAsList();
       
       Iterator<Instrument> instItr = instList.iterator();
       int j = 0;

       while (instItr.hasNext()) {
           Instrument foundInst = instItr.next();
           String testName = foundInst.getName();
           
           if (testName.contains(instName)) {
               //System.out.println("Found a match " + testName + " with " + instName);
               //System.out.println("with number" + foundInst.getPatch().getProgram());
               
              
                switch (instName) {
               
                    case "Piano": 
                        System.out.println(j);
                        j++;
                        pianoList.add(foundInst);
                        break;
                    case "Guitar":
                        guitarList.add(foundInst);
                        break;
                    case "Bass":
                        bassList.add(foundInst);
                        break;
                    case "Strings":
                        stringList.add(foundInst);
                        break;
                    case "Voice":
                        voiceList.add(foundInst);                   
                        break;
                    case "Drums":
                        drumList.add(foundInst);
                        break;
                    default:
                        System.out.println("No instrument found");
                    }
            }
       }

       for (Instrument i : guitarList) {
           System.out.println(i.getName());
       }
   }
   
   
   /*
   Once we have the list of instruments in a list we can use it to compare 
   the desired instrument sent from scratch.
   */
   // TODO : shouldn't be static but needs to be for now
   public static List<Instrument> getInstrumentsAsList () {
       
       Instrument[] tmpInst = synth.getDefaultSoundbank().getInstruments();
       List<Instrument> tmpList = new LinkedList();
       int size = tmpInst.length;
       
       for (int i = 0 ; i < size ; i ++) {
           
           tmpList.add(tmpInst[i]);
       }
       
       /*for (int i = 0 ; i < tmpList.size() ; i ++) {
           
           System.out.println(tmpList.get(i));
       }
       */
       return tmpList;
   }
}
