/**
 * GeneBankCreateBTree
 * Driver class for Assignment 4
 * BTree construction
 *
 * @author Patrick Doudy
 * CS321 Summer 2017
 */

import java.io.File;
import java.io.IOException;

public class GeneBankCreateBTree {

	private static int degree;
	private static String gbkFilename;
	private static int sequenceLength;
	private static int useCache;
	private static int cacheSize;
	private static int debugLevel;
	private static String outputFilename;
        private static final int defaultDegree = 128;
        private static final boolean devDebug = false;
        private static BTree btree;

	GeneBankCreateBTree(String[] args){
	    processArguments(args);
	}

	public void processArguments(String[] args){
		try{

			if(!(args.length >= 4 && args.length <= 6)){
				System.out.println("wrong number of arguments");
				usageQuit();
			}

			useCache = Integer.parseInt(args[0]);
			degree   = Integer.parseInt(args[1]);
			gbkFilename  = args[2];
			sequenceLength = Integer.parseInt(args[3]);

			File gbkFileTest = new File(gbkFilename);
			if(!gbkFileTest.exists()){
			    System.out.println(gbkFilename+" does not exist");
			    usageQuit();
			} else {
			    if(!gbkFilename.contains(".gbk")){
				    System.out.println("input file does contain .gbk extension");
				    usageQuit();
			    }
			}

			if(useCache != 0 && useCache != 1){
				System.out.println("cache indicator must be 0 or 1");
				usageQuit();
			}

			if(!(degree >= 0)){
				System.out.println("degree must be >= 0");
				usageQuit();
			}

			if(degree==0){
			    degree = defaultDegree;
			    System.out.println("using degree "+defaultDegree+" assuming disk block of 4096 bytes");
			}

			if(!(sequenceLength >= 1 && sequenceLength <= 31)){
				System.out.println("sequence length must be between 1 and 31");
				usageQuit();
			}

			if(useCache == 1){
				if(args.length == 4){
					System.out.println("no cache size supplied");
					usageQuit();
				}
				cacheSize = Integer.parseInt(args[4]);
				if(!(cacheSize >= 1)){
					System.out.println("cache size must be a positive number");
					usageQuit();
				}
				if(args.length == 6){
					debugLevel = Integer.parseInt(args[5]);
					if(!(debugLevel == 0 || debugLevel == 1)){
						System.out.println("debug level must be 0 or 1");
						usageQuit();
					}
				}
				return;
			}

			if(useCache == 0){
				if(args.length == 5){
					debugLevel = Integer.parseInt(args[4]);
					if(!(debugLevel == 0 || debugLevel == 1)){
						System.out.println("debug level must be 0 or 1");
						usageQuit();
					}
				}
				return;
			}

			usageQuit();

		} catch (NumberFormatException e){
			System.out.println("Incorrect number formatting");
			usageQuit();
		}
	}

	public void usageQuit(){
		System.out.print("Useage: GeneBankCreateBTree <0/1(no/yes cache)> <degree>");
		System.out.print(" <gbk file> <sequence length> [<cache size>] [<debug level>]\n");
		System.exit(0);
	}

	public static void main(String[] args){
		GeneBankCreateBTree driver = new GeneBankCreateBTree(args);
       		System.out.println("starting BTree construction");
		outputFilename = gbkFilename+".btree.data."+sequenceLength+"."+degree;
		if (useCache == 1)
			NodeStorage.setConfig(degree, outputFilename, cacheSize);
		else
			NodeStorage.setConfig(degree, outputFilename);
		btree = new BTree(degree, sequenceLength);
		FileParser parser = new FileParser(degree, gbkFilename, sequenceLength, btree);
		NodeStorage.dumpTree(devDebug);
		if(debugLevel==1){
		    try{
			System.out.println("creating dump file...");
			btree.inOrderDump();
			System.out.println("\nfinished dump file");
		    }catch (IOException e){
			System.out.println("inorder dump file create failed");
		    }
		}
      		System.out.println("\ndone!");
	}

}