import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by Moosejaw on 8/5/2017.
 */
public class NodeStoragePrototype {

    private int size;
    private int objSize;
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
    }

    // -- // Public Methods // -- //

    public void writeNext(String filepath, BTreeNode o) {
        writeAtOffset(-1, filepath, o);
    }

    public void readLast(String filepath) {
        readAtOffset(-1, filepath);
    }

    public void writeAtOffset(long offset, String filepath, BTreeNode o) {
        try {
            byte[] b;
            File f = new File(filepath);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            if(offset == -1)
                offset = raf.length();

            b = ByteBuffer.allocate(4).putInt(o.getParent()).array();
            raf.seek(offset);
            raf.write(b);
            b = intArrToByte(o.getKeys());
            raf.write(b);
            b = intArrToByte(o.getChildren());
            raf.write(b);
            fileSize = raf.length();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void readAtOffset(long offset, String filepath) {
        try {
            byte[] b;
            File f = new File(filepath);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            if (offset == -1)
                offset = raf.length()-objSize;
            raf.seek(offset);
            b = new byte[4];
            raf.read(b);
            int p = ByteBuffer.wrap(b).getInt();
            b = new byte[20];
            raf.read(b);
            int[] k = toIntArr(b);
            b = new byte[24];
            raf.read(b);
            int[] c = toIntArr(b);
            BTreeNode o = new BTreeNode(p, k, c);
            System.out.println(o.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // -- // Private Methods // -- //

    private byte[] intArrToByte(int[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);

        return byteBuffer.array();
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
