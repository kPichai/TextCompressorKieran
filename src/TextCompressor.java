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

import java.util.ArrayList;

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
        String prefix = "";
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
        BinaryStdOut.write(codes.lookup(prefix), CODE_LENGTH);
        BinaryStdOut.write(256, CODE_LENGTH);
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] codes = new String[(int)Math.pow(2, CODE_LENGTH)];
        for (int i = 0; i <= 256; i++) {
            codes[i] = "" + (char)i;
            if (i == 256) {
                codes[i] = "";
            }
        }
        int maxCode = 257;
        String val = codes[BinaryStdIn.readInt(12)];
        BinaryStdOut.write(val);
        int nextCode;
        while (!val.equals("")) {
            nextCode = BinaryStdIn.readInt(CODE_LENGTH);
            if (nextCode < maxCode) {
                BinaryStdOut.write(codes[nextCode]);
            } else {
                BinaryStdOut.write(val + codes[nextCode].charAt(0));
            }
            if (maxCode < codes.length && nextCode != 256) {
                codes[maxCode++] = val + codes[nextCode].charAt(0);
            }
            val = codes[nextCode];
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
