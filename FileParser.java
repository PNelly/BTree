import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * File Parsing class to read DNA sequences from a file
 * and pull sequences of specified lengths out
 *
 * @author Michael Burke
 * CS321 Summer 2017
 */
public class FileParser {

    private int sequenceLength;

    public FileParser(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    //TODO: Send the sequence off somewhere? We didnt discuss flow control really.
    public void getSequences(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            Encoder e = new Encoder(sequenceLength);
            String line, sequence;
            line = br.readLine();
            while(line != null) {
                while (!line.trim().equals("ORIGIN")) {
                    line = br.readLine();

                    //Probably a cleaner way to break out at EOF
                    if (line == null)
                        return;
                }

                //Get the first line outside of the loop so we can concat easier
                line = br.readLine();
                while (line.charAt(line.length() - 1) != '/') {
                    line = line.replaceAll("[^gatc]", "");
                    for (int i = 0; i <= line.length() - sequenceLength; i++) {
                        sequence = line.substring(i, i + sequenceLength);
                        System.out.println(sequence);
                        //TODO: Send the sequence off somewhere
                    }
                    line = line.substring(line.length() - sequenceLength + 1) + br.readLine();
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
