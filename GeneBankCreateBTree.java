/**
 * GeneBankCreateBTree
 * Driver class for Assignment 4
 * BTree construction
 *
 * @author Patrick Doudy
 * CS321 Summer 2017
 */

public class GeneBankCreateBTree {

    private static int degree;
    private static String gbkFilename;
    private static int sequenceLength;
    private static int useCache;
    private static int cacheSize;
    private static int debugLevel;
    private static String outputFilename;

    GeneBankCreateBTree(){

    }

    public void processArguments(String[] args){
	try{

	    if(!(args.length >= 4 && args.length <= 6)){
		System.out.println("wrong number of arguments");
		useageQuit();
	    }

	    useCache = Integer.parseInt(args[0]);
	    degree   = Integer.parseInt(args[1]);
	    gbkFilename  = args[2];
	    sequenceLength = Integer.parseInt(args[3]);

	    if(useCache != 0 && useCache != 1){
		System.out.println("cache indicator must be 0 or 1");
		useageQuit();
	    }

	    if(!(degree >= 1)){
		System.out.println("degree must be >= 1");
		useageQuit();
	    }

	    if(!(sequenceLength >= 1 && sequenceLength <= 31)){
		System.out.println("sequence length must be between 1 and 31");
		useageQuit();
	    }

    	    if(useCache == 1){

	        if(args.length == 4){
		    System.out.println("no cache size supplied");
		    useageQuit();
		}
		cacheSize      = Integer.parseInt(args[4]);
      		
		if(!(cacheSize >= 1)){
		    System.out.println("cache size must be a positive number");
		    useageQuit();
		}
		if(args.length == 6){
		    debugLevel = Integer.parseInt(args[5]);
		    if(!(debugLevel == 0 || debugLevel == 1)){
			System.out.println("debug level must be 0 or 1");
			useageQuit();
		    }
		}
		return;
	    }

	    if(useCache == 0){
		if(args.length == 5){
		    debugLevel = Integer.parseInt(args[4]);
		    if(!(debugLevel == 0 || debugLevel == 1)){
			System.out.println("debug level must be 0 or 1");
			useageQuit();
		    }
		}
		return;
	    }

	    useageQuit();

      	} catch (NumberFormatException e){
	    System.out.println("Incorrect number formatting");
	    useageQuit();
	}
    }
    
    public void useageQuit(){
	System.out.print("Useage: GeneBankCreateBTree <0/1(no/yes cache)> <degree>");
	System.out.print(" <gbk file> <sequence length> [<cache size>] [<debug level>]\n");
	System.exit(0);
    }

    public static void main(String[] args){
	GeneBankCreateBTree driver = new GeneBankCreateBTree();
	driver.processArguments(args);
	System.out.println("starting BTree construction");
	outputFilename = gbkFilename+".btree.data."+sequenceLength+"."+degree;
	NodeStorage.setConfig(degree, outputFilename);
	FileParser parser = new FileParser(degree, gbkFilename, sequenceLength);
	NodeStorage.dumpTree();
	System.out.println("\ndone!");
    }

}