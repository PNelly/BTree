/**
 * BTree class for DNA parsing processing assignment
 * Depends on BTreeNode class
 *
 * @author Patrick Doudy
 * CS321 Summer 2017
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class BTree {

	// -- // Fields // -- //

	private int degree;
	private int maxKeys;
	private int sequenceLength;
	private BTreeNode root;

	private File inOrderDumpFile;
	private BufferedWriter inOrderBufferedWriter;
	private FileWriter inOrderFileWriter;
	private PrintWriter inOrderPrintWriter;
	private String inOrderDumpFileName = "dump";
	private Encoder inOrderEncoder;
	// -- // Constructor // -- //

	public BTree(int degree, int sequenceLength){
		this.degree  = degree;
		this.maxKeys = degree*2 -1;
		this.sequenceLength = sequenceLength;
		this.root    = new BTreeNode(maxKeys);

	}

	public BTree(String filename, int degree, int cacheSize) throws IOException {
		this.degree = degree;
		maxKeys = degree*2 -1;
		NodeStorage.setConfig(degree, filename, cacheSize);
		root = NodeStorage.loadNode(NodeStorage.getRoot());
	}

	public BTree(String filename, int degree) throws IOException {
		this.degree = degree;
		maxKeys = degree*2 -1;
		NodeStorage.setConfig(degree, filename);
		root = NodeStorage.loadNode(NodeStorage.getRoot());
	}

	// -- // Public Methods // -- //

	public void insert(Long key){
		recursiveInsert(key, root);
	}

	public void insert(long key){
		insert(new Long(key));
	}

	public TreeObject find(Long key){
		return recursiveSearch(key, root);
	}

	public TreeObject find(long key){
		return find(new Long(key));
	}

	public void inOrderDump() throws IOException {

		inOrderDumpFile = new File(inOrderDumpFileName);
		if(inOrderDumpFile.exists()){
			inOrderDumpFile.delete();
			inOrderDumpFile.createNewFile();
		}
		inOrderFileWriter = new FileWriter(inOrderDumpFile);
		inOrderBufferedWriter = new BufferedWriter(inOrderFileWriter);
		inOrderPrintWriter = new PrintWriter(inOrderBufferedWriter);
		inOrderEncoder = new Encoder(sequenceLength);

		recursiveDump(root);

		inOrderPrintWriter.flush();
		inOrderPrintWriter.close();

	}


	// -- // Private Methods // -- //

	private BTreeNode split(BTreeNode node){

		BTreeNode parent, right;
		parent = node.getParent();

		if(parent == null){
			parent = new BTreeNode(maxKeys);
			parent.insertChild(node);
			parent.setbyteOffset(NodeStorage.nextWritePos());
			node.setParent(parent.getbyteOffset());
			root   = parent;
		}

		int medianKeyIndex = degree -1;
		parent.insertKey(node.getTreeObject(medianKeyIndex));
		right = new BTreeNode(parent.getbyteOffset(),
				node.rightOf(medianKeyIndex),
				node.getRightChildList(medianKeyIndex),
				NodeStorage.nextWritePos());

		parent.insertChild(right);

		if(root.getbyteOffset() == parent.getbyteOffset()) {
			NodeStorage.setRootLocation(root.getbyteOffset());
			root = parent;
		}
		NodeStorage.saveManyNodes(new BTreeNode[] {node, parent, right}, right.getChildList(), right.getbyteOffset());

		return parent;
	}

	private void recursiveInsert(Long key, BTreeNode node){

		if(node.isFull()){
			node = split(node);
		}

		if(!node.isLeaf())
			recursiveInsert(key, node.getChild(key));
		else {
			node.insertKey(key);
			NodeStorage.updateNode(node);
		}

	}

	private TreeObject recursiveSearch(Long key, BTreeNode node){

		if(node == null)
			return null;

		TreeObject objective = node.findKey(key);

		if(objective != null)
			return objective;
		if(!node.isLeaf())
			return recursiveSearch(key, node.getChild(key));
		return null;

	}

	private void recursiveDump(BTreeNode node){

		if(node==null) return;

		int i;
		int c = node.numChildren();

		for(i=0; (i<c && i<degree); i++){
			recursiveDump(node.getChild(i));
		}

		int k = node.numKeys();
		int frequency;
		long key;
		String sequence;
		TreeObject t;

		for(i=0; i<k; i++){
			t = node.treeObjectByIndex(i);
			frequency = t.getFrequency();
			sequence = inOrderEncoder.decode(t.getKey());
			inOrderPrintWriter.print(frequency);
			inOrderPrintWriter.print("\t");
			inOrderPrintWriter.print(sequence);
			inOrderPrintWriter.println();
		}

		for(i=degree; i<c; i++){
			recursiveDump(node.getChild(i));
		}
	}

}