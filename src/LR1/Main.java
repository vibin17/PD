package LR1;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File("LR1_input.txt"));
            Map<String, Integer> words = new HashMap<>();
            scanner.useDelimiter("\\W+");
            while (scanner.hasNext())
            {
                var curWord = scanner.next().toLowerCase();
                var value = words.get(curWord);
                words.put(curWord, value != null? ++value : 1);
            }
            scanner.close();
            var count = 0;
            for (var word : words.keySet()) {
                System.out.println(word + " - " + words.get(word));
                count += words.get(word);
            }
            System.out.println("total words - " + count);
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
