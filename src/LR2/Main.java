package LR2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Map<String, ArrayList<String>> classes = new HashMap<String, ArrayList<String>>();
        try  {
            Stream<Path> paths = Files.walk(Paths.get("D:\\Fork\\OmGTU-Parallel-and-Distributed-Programming\\src\\files_directory"));
            paths.filter(Files::isRegularFile)
                    .map(p -> p.toString())
                    .filter(s -> s.endsWith(".java"))
                    .forEach(s -> {
                        try {
                            Scanner in = new Scanner(new File(s));
                            String data = in.useDelimiter("\\A").next();
                            in.close();
                            System.out.println(data);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        }
        catch (Exception e) {
            System.out.println("ERROR");
        }
    }
}
