import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class Construction {


    private final RandomAccessFile rawindex = new RandomAccessFile(new File("/afs/kth.se/misc/info/kurser/DD2350/adk22/labb1/rawindex.txt").getCanonicalPath(), "r");
    private RandomAccessFile lazyindex;

    private final Hash hash = new Hash();

    private long bound;

    private boolean flag = true;


    public static void main(String[] args) throws IOException {
        Instant inst1 = Instant.now();
        Construction construction = new Construction();
        construction.createLazyIndex();
        Instant inst2 = Instant.now();
        System.out.println("Duration (in seconds):");
        System.out.println(Duration.between(inst1, inst2).toSeconds());
    }

    public Construction() throws IOException {}

    public void ifExists() throws IOException {
        Files.deleteIfExists(Path.of("lazyindex.txt"));
        lazyindex = new RandomAccessFile(new File("lazyindex.txt").getCanonicalPath(), "rw");
    }

    public void createLazyIndex() throws IOException {
        ifExists(); // 

        String lastword = "";
        String l;
        int i = 0;
        bound = rawindex.length();
        while (flag) { // flag / kan också sättas till i < X för körning på delar av index
            l = getUnique(lastword); 
            lastword = l;
            long p = rawindex.getFilePointer();
            writeLazyIndex(p, hash.getHash(l));
            getNextInterval(l, p);
            i++;
        }
    }

    // Returns a pointer to a location which is known to be at most 5000 bytes away from the next unique word
    private long getNextInterval(String word, long start) throws IOException {
        long end = start + (long) 5000;
        if (start == bound) {
            flag = false;
            return 0;
        } else if (end > bound) {
            return start;
        }

        rawindex.seek(end);
        rawindex.readLine();

        String l = rawindex.readLine();
        l = l.substring(0, Math.min(3, l.indexOf((char) 32)));


        if (l.equals(word)) {
            return getNextInterval(word, rawindex.getFilePointer());
        }
            rawindex.seek(start);
        return start;
    }

    // Returns the new unique word with the pointer standing just before its appearance.
    private String getUnique(String word) throws IOException {
        try {
            String l = rawindex.readLine();
            int lLength = l.length();
            l = l.substring(0, Math.min(3, l.indexOf((char) 32)));

            while (l.equals(word)) {
                l = rawindex.readLine();
                lLength = l.length();
                l = l.substring(0, Math.min(3, l.indexOf((char) 32)));
            }
            rawindex.seek(rawindex.getFilePointer() - lLength - 1);
            return l;
        } catch (NullPointerException e) {
            return word;
        }
    }

    private void writeLazyIndex(long p, int v) throws IOException {
        lazyindex.seek(v);
        lazyindex.writeLong(p);
    }
}