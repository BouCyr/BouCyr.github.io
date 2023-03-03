package app.cbo.writings.tooling;

import org.checkerframework.checker.units.qual.A;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CountChar {

    public static void main(String... args){

        try(FileInputStream in = new FileInputStream("c:\\WORK\\JHIGHLIGHT\\pg100.txt")){
            countChars(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void countChars(InputStream inputStream) throws IOException {
        // create an array to store the count of each character
        var charCounts = new HashMap<Character, AtomicLong>();

        // read from the input stream one byte at a time
        int c;
        while ((c = inputStream.read()) != -1) {
            // increment the count for the corresponding character
            if(!charCounts.containsKey((char) c))
                charCounts.put((char) c, new AtomicLong());
            charCounts.get((char)c).incrementAndGet();
        }

       charCounts.entrySet().stream()
               .filter(e -> isInterestingChar(e.getKey()))
               .sorted(Comparator.comparing(e -> -1*e.getValue().get()))
               .map(e -> e.getKey()+":"+e.getValue().get())
               .forEach(System.out::println);
    }

    public static boolean isInterestingChar(char c) {
        // use the Character class to determine if the character is printable
        return Character.isLetterOrDigit(c) ||
                Character.getType(c) == Character.MATH_SYMBOL || Character.getType(c) == Character.CURRENCY_SYMBOL ||
                Character.getType(c) == Character.MODIFIER_SYMBOL || Character.getType(c) == Character.OTHER_SYMBOL;
    }

    public static boolean isPrintableChar(char c) {
        // use the Character class to determine if the character is printable
        return Character.isLetterOrDigit(c) || Character.isWhitespace(c) || Character.getType(c) == Character.CONNECTOR_PUNCTUATION ||
                Character.getType(c) == Character.DASH_PUNCTUATION || Character.getType(c) == Character.START_PUNCTUATION ||
                Character.getType(c) == Character.END_PUNCTUATION || Character.getType(c) == Character.INITIAL_QUOTE_PUNCTUATION ||
                Character.getType(c) == Character.FINAL_QUOTE_PUNCTUATION || Character.getType(c) == Character.OTHER_PUNCTUATION ||
                Character.getType(c) == Character.MATH_SYMBOL || Character.getType(c) == Character.CURRENCY_SYMBOL ||
                Character.getType(c) == Character.MODIFIER_SYMBOL || Character.getType(c) == Character.OTHER_SYMBOL;
    }

}
