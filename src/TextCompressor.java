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
    // Length of codes for our compression and expansion, 12 as default
    final private static int CODE_LENGTH = 12;
    final private static int EOF = 256;
    final private static int FIRST_CODE = 257;

    // Compress algorithm reads in a string and compresses it using LZW compression
    private static void compress() {
        // Reads in string and initializes TST used for storing all codes
        String text = BinaryStdIn.readString();
        TST codes = new TST();
        int codesLen = 1 << CODE_LENGTH;
        // Assigns all initial values in the TST as well as our starting code
        for (int i = 0; i < EOF; i++) {
            codes.insert("" + (char)i, i);
        }
        int startCode = FIRST_CODE;
        String prefix;
        // Loops through our text
        for (int i = 0; i < text.length();) {
            // Reads in prefix and its associated code
            prefix = codes.getLongestPrefix(text, i);
            int associatedCode = codes.lookup(prefix);
            // Writes the code to the output file
            BinaryStdOut.write(associatedCode, CODE_LENGTH);
            // Printing statements for testing
//            System.err.println("Writing: " + (associatedCode));
            // Checks to see we aren't going out of bounds or have run out of codes
            if (i + prefix.length() < text.length() && startCode < codesLen) {
                char next = text.charAt(i + prefix.length());
                // Inserts our next code into the TST for future lookups
                codes.insert(prefix + next, startCode);
//                System.err.println("Adding Code: " + (prefix + next) + " Code: " + startCode);
                startCode++;
            }
            // Increments our spot in the text string according to how much we wrote out in our prefix
            i += prefix.length();
        }
        // Writes EOF and closes file
        BinaryStdOut.write(EOF, CODE_LENGTH);
        BinaryStdOut.close();
    }

    // Expands code via a compressed txt file
    private static void expand() {
        // Map used to access codes and their corresponding string values
        int codesLen = 1 << CODE_LENGTH;
        String[] codes = new String[codesLen];
        // Fills it with known values (ASCII)
        for (int i = 0; i <= EOF; i++) {
            codes[i] = "" + (char) i;
            if (i == EOF) {
                codes[i] = "";
            }
        }
        // Reads in and initializes all necessary values to expand based off of current and future values
        int maxCode = FIRST_CODE;
        int valCode = BinaryStdIn.readInt(CODE_LENGTH);
        String val = codes[valCode];
        BinaryStdOut.write(val);
        int nextCode;
        // Infinite loop to be broken when EOF is seen
        while (true) {
            // Checks next code to see if we will exit then
            nextCode = BinaryStdIn.readInt(CODE_LENGTH);
            if (nextCode == EOF) {
                break;
            }
            // Makes our entry string which represents what we will write out to our BinaryStdOut file
            String entry;
            if (nextCode < maxCode && codes[nextCode] != null) {
                entry = codes[nextCode];
            } else {
                // Special case where we don't know where our current code to add, use know val instead
                entry = val + val.charAt(0);
            }
            BinaryStdOut.write(entry);
            // Checks if space left and if so adds more to codes
            if (maxCode < codesLen) {
                codes[maxCode++] = val + entry.charAt(0);
            }
            // Continues along codes by moving to next
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