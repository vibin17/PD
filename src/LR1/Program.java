package LR1;

import java.io.*;
import java.util.*;

public class Program {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(new File("text.txt"));
        Map<String, Integer> words = new HashMap<String, Integer>();
        //in.useDelimiter("\\s*(\\s|,|!|'|\\.)\\s*");
        in.useDelimiter("\\W+");

        while(in.hasNext()){
            String s = in.next().toLowerCase();
            if (words.containsKey(s)){
                words.put(s, words.get(s) + 1);
            } else {
                words.put(s, 1);
            }
        }

        for(Map.Entry<String, Integer> item : words.entrySet()){
            System.out.printf("%s %d \n", item.getKey(), item.getValue());
        }

        in.close();
    }
}