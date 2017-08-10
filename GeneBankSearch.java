import java.io.*;
import java.security.InvalidParameterException;


/**
 * Driver Class for searching a Gene Bank generated BTree for
 * a list of query sequences.
 *
 * @author Ben Etcheverry
 * CS321 Summer 2017
 */
public class GeneBankSearch {

    private static File BTreeFile;
    private static File queryFile;
    private static int cache;
    private static int cacheSize = 0;
    private static int debugLevel;
    private static BTree tree;
    private static Encoder encoder;

    public static void main(String[] args) throws InvalidParameterException {
        if (args.length < 3 || args.length > 5) {
            System.err.println("Incorrect number of arguments.");
            printUsage();
            throw new InvalidParameterException();
        }
        try {
            if (Integer.parseInt(args[0]) != 0 && Integer.parseInt(args[0]) != 1) {
                System.err.println("Input argument <with/without Cache> could not be parsed as a 0 or 1.");
                printUsage();
                throw new InvalidParameterException();
            } else {
                cache = Integer.parseInt(args[0]);
            }

	    BTreeFile = new File(args[1]);
	    if(!BTreeFile.exists()){
		BTreeFile.delete();
		System.err.println("Tree file "+args[1]+" does not exist");
		printUsage();
		throw new InvalidParameterException();
	    } else {
		if(!args[1].contains(".gbk.btree.data.")){
		    System.err.println(args[1]+" is not a valid BTree file");
		    printUsage();
		    throw new InvalidParameterException();
		}
	       
	    }
	    queryFile = new File(args[2]);
	    if(!queryFile.exists()){
		    queryFile.delete();
		    System.err.println("query file "+args[2]+" does not exist");
		    printUsage();
		    throw new InvalidParameterException();
	    }
	    

            if (args.length > 3) {
                if (Integer.parseInt(args[0]) == 1) {
                    if (Integer.parseInt(args[3]) < 1) {
                        System.err.println("Cache size must be greater than 0.");
                        printUsage();
                        throw new InvalidParameterException();
                    } else {
                        cacheSize = Integer.parseInt(args[3]);
                    }
                } else {
                    if (Integer.parseInt(args[3]) != 0 && Integer.parseInt(args[3]) != 1) {
                        System.err.println("Input argument <debug level> could not be parsed as 0 or 1.");
                        printUsage();
                        throw new InvalidParameterException();
                    } else {
                        debugLevel = Integer.parseInt(args[3]);
                    }
                }
                if (args.length > 4) {

                    if (Integer.parseInt(args[0]) == 0) {
                        System.err.println("Cannot pass cache size when cache setting argument is set to 0");
                        printUsage();
                        throw new InvalidParameterException();
                    }

                    if (Integer.parseInt(args[4]) != 0 && Integer.parseInt(args[4]) != 1) {
                        System.err.println("Input argument <debug level> could not be parsed as 0 or 1.");
                        printUsage();
                        throw new InvalidParameterException();
                    } else {
                        debugLevel = Integer.parseInt(args[4]);
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Input argument cannot be read as a number");
            printUsage();
            System.err.println(e);
        }


        try {

            String[] s = args[1].split("\\.");
            if(cache == 1) {
                tree = new BTree(args[1], Integer.parseInt(s[5]), cacheSize);
            } else {
                tree = new BTree(args[1], Integer.parseInt(s[5]));
            }
            queryFile = new File(args[2]);
            BufferedReader br = new BufferedReader(new FileReader(queryFile));
            encoder = new Encoder(Integer.parseInt(s[4]));
            TreeObject tobj = null;
            BufferedWriter bw = null;

	    if(debugLevel == 1){
		File dump = new File("searchDump");
		FileWriter fw = new FileWriter(dump);
		bw = new BufferedWriter(fw);
	    }

            String line = br.readLine();

            if ((tobj = tree.find(encoder.encode(line))) != null)
            {
		System.out.println(line+" found "+tobj.getFrequency());
                
      		if(debugLevel==1)
		    bw.write(tobj.getFrequency()+"\t"+line+"\n");
            }
            while ((line = br.readLine()) != null) {
                if ((tobj = tree.find(encoder.encode(line))) != null)
                {
		    System.out.println(line+" found "+tobj.getFrequency());

		    if(debugLevel==1)
			bw.write(tobj.getFrequency()+"\t"+line+"\n");
                }
            }

	    if(debugLevel==1){
		bw.flush();
		bw.close();
	    }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void printUsage(){
        System.err.println("Incorrect input. Please run this command with the following arguments:");
        System.err.println("java GeneBankSearch <0/1 (without/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        System.err.println("With/without Cache: Specify whether this search instance of the given BTree with the given query file will utilize a chache.");
        System.err.println("BTree file: Provide a file with associated with a BTree, be sure that the GeneBankCreateBtree has already been successfully run and compiled.");
        System.err.println("Query file: Provide a file with associated list of queries for the given search, be sure that the sequence length of the query matches the given BTree");
        System.err.println("Cache size(optional): Specified the desired Cache size, the default cache size will be set to optimize the cache.");
        System.err.println("Debug level(optional): Specify 0: standard debug level, will output any error messages. Specify 1: create a dump file with a list of frequencies for each query.");
        System.exit(1);
    }
}
