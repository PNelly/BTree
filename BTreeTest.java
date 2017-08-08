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
		int i = 0;
		try {
			long startTime = System.nanoTime();
			for (i = 0; i < iterations; i++) {
				//System.out.println("insertion " + (i + 1));
				//item = (long) RNG.nextInt(100);
				item = RNG.nextLong();
				tree.insert(item);
				insertedKeys.add(item);
				//tree.insert(i);
				//insertedKeys.add((long)i);
			}
			//NodeStorage.dumpTree();
			long endTime = System.nanoTime();
			long duration = (endTime - startTime)/1000000;
			System.out.println("Inserted "+iterations+" iterations in "+duration+"ms");
			startTime = System.nanoTime();
			for (i = 0; i < iterations; i++) {
				Long objective = insertedKeys.get(i);
				TreeObject result = tree.find(objective);

				if (result != null && objective.longValue() == result.getKey().longValue())
					numSuccessfulSearches++;

				System.out.print("\r" + numSuccessfulSearches + " of " + iterations + " searches successful");
			}
			endTime = System.nanoTime();
			duration = (endTime - startTime)/1000000;
			System.out.println("Searched "+iterations+" iterations in "+duration+"ms");
			System.out.println();
		} catch (Exception e) {
			System.out.println(i);
			System.out.println(e);
		}
    }


    public static void main(String[] args){
		System.out.println("Beginning BTree Test");
		BTreeTest test = new BTreeTest(10,10000);
		test.runBTreeTest();
		System.out.println("done!");
    }

}