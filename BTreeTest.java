/**
 * Test Driver for BTree class
 *
 * @author Patrick Doudy
 * CS321 Summer 2017
 */

import java.util.Random;
import java.util.ArrayList;

public class BTreeTest{

    private BTree tree;
    private Random RNG;
    private ArrayList<Long> insertedKeys;
    private int degree;
    private int iterations;
    private int numSuccessfulSearches;

    public BTreeTest(int degree, int iterations){
		this.degree = degree;
		this.iterations = iterations;
		tree = new BTree(degree);
		RNG = new Random();
		insertedKeys = new ArrayList<Long>();
		numSuccessfulSearches = 0;
    }

    public void runBTreeTest(){
		long item;
		
		for(int i=0; i<iterations; i++){
			System.out.print("                        ");
		    System.out.print("\rinsertion "+i);
		    item = RNG.nextLong();
		    tree.insert(item);
		    //tree.testTraversal();
		    insertedKeys.add(item);
		}
		
		for(int i=0; i<iterations; i++){
		    Long objective = insertedKeys.get(i);
		    Long result    = tree.find(objective);
	        if(result != null){
	        	if(objective.longValue()==result.longValue()){
	        		System.out.print("\rsearch "+i+" successful");
	        		numSuccessfulSearches++;
	        	} else {
	        		System.out.print("\rsearch "+i+" unsuccessful, no match");
	        	}
	        } else {
	        		System.out.print("\rsearch "+i+" unsuccessful, found nothing");
	        }
	        System.out.print("\r                                                    ");
		}
		
		System.out.println(numSuccessfulSearches+" of "+iterations+" successful");
	}


    public static void main(String[] args){
		System.out.println("Beginning BTree Test");
		BTreeTest test = new BTreeTest(2,1000);
		test.runBTreeTest();
		System.out.println("done!");
    }

}