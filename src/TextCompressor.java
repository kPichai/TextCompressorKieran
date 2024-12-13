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
        int codesLen = (int)Math.pow(2, CODE_LENGTH);
        for (int i = 0; i < 256; i++) {
            codes.insert("" + (char)i, i);
        }
        int startCode = 257;
        String prefix;
        for (int i = 0; i < text.length();) {
            prefix = codes.getLongestPrefix(text, i);
            int associatedCode = codes.lookup(prefix);
            BinaryStdOut.write(associatedCode, CODE_LENGTH);
            System.err.println("Writing: " + (associatedCode));
            if (i + prefix.length() < text.length() && startCode < codesLen) {
                char next = text.charAt(i + prefix.length());
                codes.insert(prefix + next, startCode);
                System.err.println("Adding Code: " + (prefix + next) + " Code: " + startCode);
                startCode++;
            }
            i += prefix.length();
        }
        BinaryStdOut.write(256, CODE_LENGTH);
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] codes = new String[(int) Math.pow(2, CODE_LENGTH)];
        int codesLen = (int) Math.pow(2, CODE_LENGTH);
        for (int i = 0; i <= 256; i++) {
            codes[i] = "" + (char) i;
            if (i == 256) {
                codes[i] = "";
            }
        }
        int maxCode = 257;
        int valCode = BinaryStdIn.readInt(CODE_LENGTH);
        String val = codes[valCode];
        BinaryStdOut.write(val);
        int nextCode;
        while (true) {
            nextCode = BinaryStdIn.readInt(CODE_LENGTH);
            if (nextCode == 256) break;
            String entry;
            if (nextCode < maxCode && codes[nextCode] != null) {
                entry = codes[nextCode];
            } else {
                entry = val + val.charAt(0);
            }
            BinaryStdOut.write(entry);
            if (maxCode < codesLen) {
                codes[maxCode++] = val + entry.charAt(0);
            }
            val = entry;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}