/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Kieran Pichai
 */
public class TextCompressor {
    final private static int CODE_LENGTH = 12;

    private static void compress() {
        String text = BinaryStdIn.readString();
        TST codes = new TST();
        for (int i = 0; i < 255; i++) {
            codes.insert("" + (char)i, i);
        }
        int startCode = 257;
        String prefix;
        String longestPrefix;
        for (int i = 0; i < text.length(); i++) {
            prefix = "" + text.charAt(i);
            longestPrefix = codes.getLongestPrefix(text, i);
            if (i + longestPrefix.length() < text.length()) {
                codes.insert(prefix + text.charAt(i + longestPrefix.length()), startCode++);
                i += longestPrefix.length();
            }
            BinaryStdOut.write(codes.lookup(prefix), CODE_LENGTH);
        }
        BinaryStdOut.write(256, CODE_LENGTH);
        BinaryStdOut.close();
    }

    private static void expand() {

        // TODO: Complete the expand() method

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
