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
    private int degree;
    private BTree btree;
    private Encoder encoder;
    private String gbkFilename;
    private File gbkFile;
    private int numInsertions;


    public FileParser(int degree, String gbkFilename, int sequenceLength) {
        this.degree = degree;
        this.gbkFilename = gbkFilename;
        this.sequenceLength = sequenceLength;
        gbkFile = new File(gbkFilename);
        encoder = new Encoder(sequenceLength);
        btree = new BTree(degree);
        numInsertions = 0;
        getSequences(gbkFile);
    }

    public void getSequences(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line, sequence;
            line = br.readLine();
            while(line != null) {
                while (!line.trim().equals("ORIGIN")) {
                    line = br.readLine();

                    if (line == null)
                        return;
                }

                //Get the first line outside of the loop so we can concat easier
                line = br.readLine();
                while (line.charAt(line.length() - 1) != '/') {
                    line = line.replaceAll("[^gatc]", "");
                    for (int i = 0; i <= line.length() - sequenceLength; i++) {

                        sequence = line.substring(i, i + sequenceLength);
                        btree.insert( encoder.encode(sequence) );

                        numInsertions++;
                        if (numInsertions % 250 == 0)
                            System.out.print(".");
                    }
                    line = line.substring(line.length() - sequenceLength + 1) + br.readLine();

                }
                System.out.println("\n"+numInsertions+" sequences inserted");
            }
            br.close();
            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
