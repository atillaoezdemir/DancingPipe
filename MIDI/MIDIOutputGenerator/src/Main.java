import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//import com.google.gson.Gson;


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
        //return 500;
        return (int) (Math.random() * 1500 + 250);
    }

    public static void switchChord(Sequencer sequencer, Sequence sequence) throws InvalidMidiDataException {
        sequencer.stop();
        sequencer.setSequence(sequence);
        sequencer.start();
    }

    public static void main(String[] args) {

        //OrganSequencer organSequencer = new OrganSequencer("sounds");


        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo(); //list of MIDI devices

        List<MidiDevice.Info> outputDeviceInfos = new ArrayList<MidiDevice.Info>();

        int index = 0;
        System.out.println("MIDI Output Devices:");
        for (MidiDevice.Info info : infos) {
            try  {
                    if(MidiSystem.getMidiDevice(info).getClass().getTypeName().equals("com.sun.media.sound.MidiOutDevice")) {
                    System.out.println("[" + index + "] " + info.getName() + " - " + info.getDescription());
                    outputDeviceInfos.add(info);
                    index++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // System.out.println(info.getName() + " - " + info.getDescription());
        }
        Scanner sc = new Scanner(System.in);
        MidiDevice.Info outputDevice = null;
        boolean playing = false;
        int input = 100;

        while(input >= outputDeviceInfos.size() || input < 0) {

            System.out.print("Enter MIDI Device number: ");
            input = sc.nextInt();
            if(input >= outputDeviceInfos.size() || input < 0) {
                System.out.println("ERROR: MIDI Device number out of bounds!");
            }
            else {
                outputDevice = outputDeviceInfos.get(input);
                System.out.println("Playing on " + outputDevice.getName());
                playing = true;
            }
        }





        System.out.println("Enter STOP to stop");
        while (playing) {
            try {
                MidiDevice virtualOutPort = MidiSystem.getMidiDevice(outputDevice); //out
                //MidiDevice virtualInPort = MidiSystem.getMidiDevice(infos[5]); //in


                //Transmitter virtualInPortTransmitter = virtualInPort.getTransmitter();
                Receiver virtualOutPortReceiver = virtualOutPort.getReceiver();

                Sequencer sequencer = MidiSystem.getSequencer(false);
                Transmitter sequencerTransmitter = sequencer.getTransmitter();

                sequencer.open();
                virtualOutPort.open();
                sequencerTransmitter.setReceiver(virtualOutPortReceiver); //sequencerTransmitter sends data to the receiver of the virtual port

                //make different sequences for different chords
                Sequence[]  sequences  = new Sequence[5];
                sequences[0] = MidiSystem.getSequence(new File("sounds/I - C.mid"));
                sequences[1] = MidiSystem.getSequence(new File("sounds/IV - F.mid"));
                sequences[2] = MidiSystem.getSequence(new File("sounds/V - G.mid"));
                sequences[3] = MidiSystem.getSequence(new File("sounds/vi - Am.mid"));
                sequences[4] = MidiSystem.getSequence(new File("sounds/iii - Em.mid"));

                Track[] tracks = sequences[4].getTracks();
                sequences[4].

                sequencer.setSequence(sequences[0]);
                sequencer.setTempoInBPM(250);
                sequencer.setLoopCount(1000);
                sequencer.start();

                int prev = 1;

                while (sequencer.isRunning()) {
                    try {
                        //Thread.sleep(randomMsGenerator());
                        Thread.sleep(1000);

                        int num = randomNumberGenerator(prev);
                        switchChord(sequencer, sequences[num-1]);
                        prev = num;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (System.in.available() > 0) {
                        String stringInput = sc.nextLine();
                        if (stringInput.equalsIgnoreCase("stop")) {
                            playing = false;
                            //stop sequencer and close devices
                            sequencer.stop();
                            sequencer.close();
                            virtualOutPort.close();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



}