import javax.sound.midi.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import com.google.gson.Gson;


public class Main {
    public static int randomNumberGenerator(int prev) {
        int rand;
        do {
            rand = (int) (Math.random() * 5 + 1);
        }
        while (rand == prev);
        return rand;
    }

    public static int randomMsGenerator() {
        return (int) (Math.random() * 3000 + 250);
    }

    public static void switchChord(Sequencer sequencer, Sequence sequence) throws InvalidMidiDataException {
        sequencer.stop();
        sequencer.setSequence(sequence);
        sequencer.start();
    }

    public static void main(String[] args) {

        //OrganSequencer organSequencer = new OrganSequencer("sounds");

        FileMapper fileMapper = new FileMapper(new File("sounds/I - C.mid"), 1, 5);
        String filename = "mapper.json";

        try
        {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeChars(new Gson().toJSON(fileMapper));

            out.close();
            file.close();

            System.out.println("Object has been serialized");

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }





        /*
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter STOP to stop");
        boolean playing = true;

        while (playing) {

            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            try {
                MidiDevice virtualOutPort = MidiSystem.getMidiDevice(infos[4]); //out
                MidiDevice virtualInPort = MidiSystem.getMidiDevice(infos[5]); //in

                //this implementation only works with one virtual port opened
                //and may be computer depending

                Transmitter virtualInPortTransmitter = virtualInPort.getTransmitter();
                Receiver virtualOutPortReceiver = virtualOutPort.getReceiver();

                Sequencer sequencer = MidiSystem.getSequencer(false);
                Transmitter sequencerTransmitter = sequencer.getTransmitter();

                sequencer.open();
                virtualOutPort.open();
                sequencerTransmitter.setReceiver(virtualOutPortReceiver); //sequencerTransmitter sends data to the receiver of the virtual port

                //make different sequences for different chords
                Sequence[]  sequences  = new Sequence[5];
                sequences[0] = MidiSystem.getSequence(new File("sounds/I - C.mid"));
                sequences[1] = MidiSystem.getSequence(new File("IV - F.mid"));
                sequences[2] = MidiSystem.getSequence(new File("V - G.mid"));
                sequences[3] = MidiSystem.getSequence(new File("vi - Am.mid"));
                sequences[4] = MidiSystem.getSequence(new File("iii - Em.mid"));


                sequencer.setSequence(sequences[0]);
                sequencer.setTempoInBPM(70);
                sequencer.setLoopCount(1000);
                sequencer.start();

                int prev = 1;

                while (sequencer.isRunning()) {
                    try {
                        Thread.sleep(randomMsGenerator());

                        int num = randomNumberGenerator(prev);
                        switchChord(sequencer, sequences[num-1]);
                        prev = num;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (System.in.available() > 0) {
                        String input = sc.nextLine();
                        if (input.equalsIgnoreCase("stop")) {
                            playing = false;
                            //stop sequencer and close devices
                            sequencer.stop();
                            sequencer.close();
                            virtualInPort.close();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

         */
    }



}