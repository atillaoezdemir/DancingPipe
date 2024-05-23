package de.thws;

import javax.sound.midi.Sequence;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RandomSequenceGenerator {
    OrganSequencer sequencer;

    public static void runRandomTestSequence(OrganSequencer organSequencer) throws OrganSequencerException {

        Scanner sc = new Scanner(System.in);

        organSequencer.setTempoInBPM(70);
        organSequencer.setLoopCount(200);
        organSequencer.open();
        organSequencer.changeSequence(randomSequenceGenerator());

        boolean isPlaying = true;
        System.out.println("Enter STOP to stop");
        while (isPlaying) {
                try {
                    Thread.sleep(randomMsGenerator()
                    );
                    String newSequence = randomSequenceGenerator();
                    organSequencer.changeSequence(newSequence);
                    System.out.println(newSequence);
                }

                catch(InterruptedException e) {
                    e.printStackTrace();
                    isPlaying = false;
                }


            try {
                if (System.in.available() > 0) {
                    String stringInput = sc.nextLine();
                    if (stringInput.equalsIgnoreCase("stop")) {
                        isPlaying = false;
                        organSequencer.stopPlaying();
                        break;
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
        if(organSequencer.isOpen()) {
            organSequencer.stopPlaying();
        }

        System.out.println("we're out");

    }

    private static String randomSequenceGenerator () {
        List<String> midiFilesNames = new ArrayList<String>();
        midiFilesNames.add("I - C.mid");
        midiFilesNames.add("iii - Em.mid");
        midiFilesNames.add("IV - F.mid");
        midiFilesNames.add("V - G.mid");
        midiFilesNames.add("vi - Am.mid");

        int rand = (int)(Math.random() * midiFilesNames.size());
        return midiFilesNames.get(rand);
    }

    private static int randomMsGenerator() {
        //return 500;
        return (int) (Math.random() * 1500 + 250);
    }
}
