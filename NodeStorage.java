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
public final class NodeStorage {

    private static int size;
    private static int objSize;
    private static long fileLength;
    private static File file;

    // -- // Constructors // -- //
    public static void setSize(int n) {
        size = n;
        objSize = calculateObjSize(size);
        System.out.println(objSize);
    }

    public static void setFile(String filepath) {
        try {
            file = new File(filepath);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Exiting program...");
            System.exit(0);
        }
    }

    public static void setMetaData() {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.write(ByteBuffer.allocate(4).putInt(4).array());
            System.out.println(raf.getFilePointer());
        } catch (Exception e) {

        }
    }

    // -- // Public Methods // -- //

    public static long writeNext(BTreeNode o) {
        return writeAt(-1, o);
    }

    public static void readLast() {
        readAt(-1);
    }

    public static long writeAt(long offset, BTreeNode o) {
        try {
            byte[] parentByte;
            byte[][] treeObjectBytes; //First array is keys, second array is frequency, matched by index
            byte[] childBytes;
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if(offset == -1)
                offset = raf.length();
            raf.seek(offset);

            parentByte = ByteBuffer.allocate(4).putInt(o.getParent()).array();
            treeObjectBytes = treeObjectToByte(o.getTreeObjects());
            childBytes = intArrToByte(o.getChildPointers());

            raf.write(parentByte);          //Metadata is only parent for now
            raf.write(treeObjectBytes[0]);  //Write our keys
            raf.write(treeObjectBytes[1]);  //Write key frequencies
            raf.write(childBytes);
            return (raf.getFilePointer() - objSize);
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

    public static BTreeNode readAt(long offset) {
        try {
            byte[] parentByte = new byte[4];
            byte[][] treeObjectBytes = new byte[2][]; //First array is keys, second array is frequency, matched by index
            byte[] childrenBytes = new byte[4*(size+1)];
            //TODO: Probably a way to do this on init?
            treeObjectBytes[0] = new byte[8 * size];
            treeObjectBytes[1] = new byte[4 * size];
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if (offset == -1)
                offset = raf.length()-objSize;

            raf.seek(offset);
            raf.read(parentByte);
            raf.read(treeObjectBytes[0]);
            raf.read(treeObjectBytes[1]);
            raf.read(childrenBytes);
            int p = ByteBuffer.wrap(parentByte).getInt();
            long[] keys = toLongArr(treeObjectBytes[0]);
            int[] freq = toIntArr(treeObjectBytes[1]);
            int[] children = toIntArr(childrenBytes);
            TreeObject[] tObjArr = new TreeObject[size];
            for (int i = 0; keys[i] != 0; i++) {
                tObjArr[i] = new TreeObject(keys[i], freq[i]);
            }
            BTreeNode o = new BTreeNode(p, tObjArr, children);
            System.out.println(o.toString());
            return o;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    // -- // Private Methods // -- //

    private static int calculateObjSize(int size) {
        int objsize = 0;
        objsize += 4;               //Parent pointer
        objsize += size * 12;       //TreeNode Objects
        objsize += (size+1) * 4;    //Children Pointers
        return objsize;
    }

    //Convert out TreeObjects into usable byte arrays
    private static byte[][] treeObjectToByte(TreeObject[] arr) {
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

    private static byte[] intArrToByte(int[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);

        return byteBuffer.array();
    }

    private static long[] toLongArr( byte[] bytes ) {
        LongBuffer longBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asLongBuffer();
        long[] array = new long[longBuf.remaining()];
        longBuf.get(array);
        return array;
    }

    private static int[] toIntArr( byte[] bytes ) {
        IntBuffer intBuf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }

    //TODO: Object serialization is infinitely simpler, but we get node sizes about 3x larger than optimal

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
