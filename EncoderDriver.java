import com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord;

import java.io.File;
import java.util.Random; // for testing

public class EncoderDriver {

	public static void main(String[] args){
		BTreeWithStorageTest(args[0]);
		//readWriteTest(args[0]);
		//encoderTest();
		//fileParseTest(args[0]);
	}

	private static void BTreeWithStorageTest(String filepath) {
		NodeStorage.setFile(filepath);
		BTreeTest btt = new BTreeTest(5, 1000);
		btt.runBTreeTest();
	}

	private static void readWriteTest(String filepath) {
		Random rand = new Random();
		NodeStorage.setSize(5);
		NodeStorage.setFile(filepath);
		NodeStorage.setMetaData();
		BTreeNode btn = new BTreeNode(5);
		long l = rand.nextLong();
		btn.insertKey(l);
		btn.insertKey(l);
		btn.insertKey(l);
		l = rand.nextLong();
		btn.insertKey(l);
		btn.insertKey(l);
		btn.insertKey(rand.nextLong());
		System.out.println("Root Before Read/Write:");
		System.out.println(btn.toString());
		btn.setbyteOffset((int)NodeStorage.writeNext(btn));
		BTreeNode btn2 = new BTreeNode(5, btn.getByteOffset());
		btn2.insertKey(rand.nextLong());
		btn2.insertKey(rand.nextLong());
		btn2.setbyteOffset((int)NodeStorage.writeNext(btn2));
		btn2.setParent(btn.getByteOffset());
		btn.insertChild(btn2);
		System.out.println("Child Before Read/Write:");
		System.out.println(btn2.toString());
		System.out.println();
		System.out.println("Root After Read/Write:");
		NodeStorage.readAt(btn.getByteOffset());
		System.out.println("Child After Read/Write:");
		NodeStorage.readAt(btn2.getByteOffset());
		System.out.println("Call parent from child:");
		NodeStorage.readAt(btn2.getParent());
		System.out.println("Call child from parent:");
		NodeStorage.readAt(btn.getChild(0));

	}

	private static void fileParseTest(String filepath) {
		FileParser fp = new FileParser(7);
		File f = new File(filepath);
		System.out.println(f);
		fp.getSequences(f);
	}

	private static void encoderTest() {
		int numChars = 31;
		int numTests = 1000;
		int numSuccess = 0;
		Encoder encoder = new Encoder(numChars);

		StringBuilder builder;
		Random RNG = new Random();
		int rand;
		String testInput, testOutput;
		Long testLong;

		for(int i=0;i<numTests;i++){

			// construct random test string
			builder = new StringBuilder();

			for(int j=0;j<numChars;j++){
				rand = RNG.nextInt(4);
				switch(rand){
					case 0:
						builder.append('a');
						break;
					case 1:
						builder.append('t');
						break;
					case 2:
						builder.append('c');
						break;
					case 3:
						builder.append('g');
						break;
				}
			}

			// encode, decode, test eq
			testInput = builder.toString();
			testLong = new Long(encoder.encode(testInput));
			testOutput = encoder.decode(testLong.longValue());

			System.out.println(testInput);
			System.out.println(Long.toBinaryString(testLong));
			System.out.println(testOutput);
			if(testInput.equals(testOutput)){
				System.out.println("Test Success!\n");
				numSuccess++;
			} else
				System.out.println("TEST FAILED\n");
		}

		System.out.println(numSuccess+" of "+numTests+" Tests Successful");

		return;
	}

}