import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;

public class GeneBankSearch {

    private static File BTreeFile;
    private static File queryFile;
    private static int cache;
    private static int cacheSize = 0;
    private static int debugLevel;
    private static Encoder encoder;

    public static void main(String[] args) throws InvalidParameterException{
        if(args.length < 3 || args.length > 5){
            System.out.println("Incorrect number of arguments.");
            printUsage();
            throw new InvalidParameterException();
        }
        try{
            if(Integer.parseInt(args[0]) != 0 && Integer.parseInt(args[0]) != 1){
                System.out.println("Input argument <with/without Cache> could not be parsed as a 0 or 1.");
                printUsage();
                throw new InvalidParameterException();
            }
            else{
                cache = Integer.parseInt(args[0]);
            }
            if(args.length > 3) {
                if (Integer.parseInt(args[3]) < 1) {
                    System.out.println("Cache size must be greater than 0.");
                    printUsage();
                    throw new InvalidParameterException();
                } else {
                    cacheSize = Integer.parseInt(args[3]);
                }
                if(args.length > 4){
                    if(Integer.parseInt(args[4])!=0 && Integer.parseInt(args[4])!=1) {
                        System.out.println("Input argument <debug level> could not be parsed as 0 or 1.");
                        printUsage();
                        throw new InvalidParameterException();
                    }
                    else{
                        debugLevel = Integer.parseInt(args[4]);
                    }
                }
            }
        }
        catch (NumberFormatException e){
            System.out.println("Input argument cannot be read as a number");
            printUsage();
            System.out.println(e);
        }
        BTreeFile = new File(args[1]);
        queryFile = new File(args[2]);
        if (BTreeFile == null || queryFile == null) {
            System.out.println("Input file could not be found, please verify that it exists");
            printUsage();
        }
        try {
            BTree bTree = new BTree(args[1]);

        } catch (Exception e) {
            System.out.println("Error loading BTreeFile");
        }

    }

    private static void printUsage(){
        System.out.println("Incorrect input. Please run this command with the following arguments:");
        System.out.println("java GeneBankSearch <0/1 (without/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        System.out.println("With/without Cache: Specify whether this search instance of the given BTree with the given query file will utilize a chache.");
        System.out.println("BTree file: Provide a file with associated with a BTree, be sure that the GeneBankCreateBtree has already been successfully run and compiled.");
        System.out.println("Query file: Provide a file with associated list of queries for the given search, be sure that the sequence length of the query matches the given BTree");
        System.out.println("Cache size(optional): Specified the desired Cache size, the default cache size will be set to optimize the cache.");
        System.out.println("Debug level(optional): Specify 0: standard debug level, will output any error messages. Specify 1: create a dump file with a list of frequencies for each query.");
        System.exit(0);
    }
}
