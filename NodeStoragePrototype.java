import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Use RandomAccessFile to read and write NodeData to a file
 * In the interest of optimizing size, we pull individual attributes and convert to bytes
 * Object Serialization is far simpler, but gives about 3x the size desired per node
 *
 * @author Michael Burke
 * CS321 Summer 2017
 */
public class NodeStoragePrototype {

    private int size;
    private int objSize;
    private long fileLength;
    private File file;

    // -- // Constructors // -- //

    public NodeStoragePrototype(int size, String filepath) {
        this.size = size;
        try {
            file = new File(filepath);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Exiting program...");
            System.exit(0);
        }
        objSize = calculateObjSize(size);
    }

    // -- // Public Methods // -- //

    public void writeNext(BTreeNode o) {
        writeAtOffset(-1, o);
    }

    public void readLast() {
        readAtOffset(-1);
    }

    public void writeAtOffset(long offset, BTreeNode o) {
        try {
            byte[] parentByte;
            byte[][] treeObjectBytes; //First array is keys, second array is frequency, matched by index
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if(offset == -1)
                offset = raf.length();
            raf.seek(offset);

            parentByte = ByteBuffer.allocate(4).putInt(o.getParent()).array();
            treeObjectBytes = treeObjectToByte(o.getTreeObjects());
            raf.write(parentByte);          //Metadata is only parent for now
            raf.write(treeObjectBytes[0]);  //Write our keys
            raf.write(treeObjectBytes[1]);  //Write key frequencies
            //TODO: Need to convert childList in BTreeNode to byte references instead of obj references

            fileLength = raf.length();
            System.out.println(fileLength);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public BTreeNode readAtOffset(long offset) {
        try {
            byte[] parentByte = new byte[4];
            byte[][] treeObjectBytes = new byte[2][]; //First array is keys, second array is frequency, matched by index
            //TODO: Probably a way to do this on init?
            treeObjectBytes[0] = new byte[8 * size];
            treeObjectBytes[1] = new byte[4 * (size+1)];
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if (offset == -1)
                offset = raf.length()-objSize;

            raf.seek(offset);
            raf.read(parentByte);
            raf.read(treeObjectBytes[0]);
            raf.read(treeObjectBytes[1]);
            int p = ByteBuffer.wrap(parentByte).getInt();
            long[] keys = toLongArr(treeObjectBytes[0]);
            int[] freq = toIntArr(treeObjectBytes[1]);
            TreeObject[] tObjArr = new TreeObject[size];
            for (int i = 0; keys[i] != 0; i++) {
                tObjArr[i] = new TreeObject(keys[i], freq[i]);
            }
            BTreeNode o = new BTreeNode(p, tObjArr, null);
            System.out.println(o.toString());
            return o;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    // -- // Private Methods // -- //

    private int calculateObjSize(int size) {
        int objsize = 0;
        objsize += 4;               //Parent pointer
        objsize += size * 12;       //TreeNode Objects
        //TODO: Include child pointers
        //objsize += (size+1) * 4;    //Children Pointers
        return objsize;
    }

    //Convert out TreeObjects into usable byte arrays
    private byte[][] treeObjectToByte(TreeObject[] arr) {
        //Two bytebuffers: One for our longs, one for our ints (key/freq, respectively)
        ByteBuffer lbBuffer = ByteBuffer.allocate(arr.length * 8);
        ByteBuffer ibBuffer = ByteBuffer.allocate(arr.length * 4);
        LongBuffer longBuffer = lbBuffer.asLongBuffer();
        IntBuffer intBuffer = ibBuffer.asIntBuffer();
        for (int i = 0; arr[i] != null; i++) {
            longBuffer.put(arr[i].getKey());
            intBuffer.put(arr[i].getFrequency());
        }
        return new byte[][] {lbBuffer.array(), ibBuffer.array()};
    }

    private byte[] intArrToByte(int[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);

        return byteBuffer.array();
    }

    private long[] toLongArr( byte[] bytes ) {
        LongBuffer longBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asLongBuffer();
        long[] array = new long[longBuf.remaining()];
        longBuf.get(array);
        return array;
    }

    private int[] toIntArr( byte[] bytes ) {
        IntBuffer intBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }

    //TODO: Object serialization is infinitely simpler, but we get node sizes about 3x larger than optimal

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
