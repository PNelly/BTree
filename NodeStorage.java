import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

public final class NodeStorage {

    private static int objSize;
    private static int MaxKeys;
    private static int MaxChildren;
    private static File file;

    public static void setConfig(int degree, String filepath) {
        file = new File(filepath);
        nodeSize(degree);
        if (file.length() == 0)
            setMetaData();
    }

    public static int nodeSize(int degree) {
        MaxKeys = (degree*2)-1;
        MaxChildren = degree*2;
        objSize = 4;                            //Parent pointer        (int, 4 bytes)
        objSize += (MaxKeys * 8);               //Keys                  (long, 8 bytes)
        objSize += (MaxKeys * 4);               //Frequency of each key (int, 4 bytes)
        objSize += (MaxChildren * 4);           //Child pointers        (int, 4 bytes)
        return objSize;
    }

    private static void setMetaData() {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.write(ByteBuffer.allocate(4).putInt(4).array());
            raf.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int nextWritePos() {
        return (int)file.length();
    }

    public static void updateNode(BTreeNode n) {
        writeAtOffset(n.getbyteOffset(), n);
    }

    public static int saveNode(BTreeNode n) {
        return writeAtOffset(-1, n);
    }

    public static BTreeNode loadNode(int byteAddress) {
        return readAtOffset(byteAddress);
    }

    public static void dumpTree() {
        File f = new File("C:\\Users\\Moosejaw\\Desktop\\BTree\\dump");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for(int i = 4; i < file.length(); i += objSize) {
                bw.write(readAtOffset(i).toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static BTreeNode readLast() {
        return readAtOffset(-1);
    }

    public static int writeAtOffset(long offset, BTreeNode o) {
        try {
            byte[] parentByte;
            byte[][] treeObjectBytes;
            byte[] childBytes;

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if(offset == -1)
                offset = raf.length();
            raf.seek(offset);

            BTreeNode parent = o.getParent();
            if (parent == null) {
                parentByte = ByteBuffer.allocate(4).putInt(-1).array();
            } else {
                parentByte = ByteBuffer.allocate(4).putInt(o.getParent().getbyteOffset()).array();
            }

            treeObjectBytes = treeObjectToByte(o.getTreeObjects());
            childBytes = intArrToByte(o.getChildList());

            if ((parentByte.length + treeObjectBytes[0].length + treeObjectBytes[1].length + childBytes.length)
                != objSize) {
                System.out.println("Unexpected node size, exiting.");
                System.exit(0);
            }

            raf.write(parentByte);
            raf.write(treeObjectBytes[0]);
            raf.write(treeObjectBytes[1]);
            raf.write(childBytes);
            raf.close();
            return (int)offset;
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

    public static BTreeNode readAtOffset(long offset) {
        try {
            byte[] parentByte = new byte[4];
            byte[][] treeObjectBytes = new byte[2][];
            treeObjectBytes[0] = new byte[8*MaxKeys];
            treeObjectBytes[1] = new byte[4*MaxKeys];
            byte[] childBytes = new byte[4*MaxChildren];

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            if (offset == -1)
                offset = raf.length()-objSize;
            raf.seek(offset);

            raf.read(parentByte);
            raf.read(treeObjectBytes[0]);
            raf.read(treeObjectBytes[1]);
            raf.read(childBytes);

            if ((parentByte.length + treeObjectBytes[0].length + treeObjectBytes[1].length + childBytes.length)
                    != objSize) {
                System.out.println("Parent expected: 4");
                System.out.println("Received:        "+parentByte.length);
                System.out.println("TreeKey expected: "+MaxKeys*4);
                System.out.println("Received:         "+treeObjectBytes[0].length);
                System.out.println("TreeFreq expected: "+MaxKeys*4);
                System.out.println("Received:          "+treeObjectBytes[1].length);
                System.out.println("ChildLen expected: "+MaxChildren*4);
                System.out.println("Received:          "+childBytes.length);
                System.out.println("Unexpected node size, exiting.");
                System.exit(0);
            }

            int parentAddr = ByteBuffer.wrap(parentByte).getInt();
            long[] keys = toLongArr(treeObjectBytes[0]);
            int[] freq = toIntArr(treeObjectBytes[1]);
            int[] children = toIntArr(childBytes);

            TreeObject[] tObjArr = new TreeObject[MaxKeys];
            for (int i = 0; keys[i] != 0; i++) {
                tObjArr[i] = new TreeObject(keys[i], freq[i]);
            }
            raf.close();
            return new BTreeNode(parentAddr, tObjArr, children, (int)offset);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    //Convert out TreeObjects into usable byte arrays
    private static byte[][] treeObjectToByte(TreeObject[] arr) {
        //Two bytebuffers: One for our longs, one for our ints (key/freq, respectively)
        ByteBuffer lbBuffer = ByteBuffer.allocate(arr.length * 8);
        ByteBuffer ibBuffer = ByteBuffer.allocate(arr.length * 4);
        LongBuffer longBuffer = lbBuffer.asLongBuffer();
        IntBuffer intBuffer = ibBuffer.asIntBuffer();
        for (int i = 0; i < arr.length-1 && arr[i] != null; i++) {
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
