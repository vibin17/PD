package LR2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Program {

    static void addElem(String a, String b, String c, Map<String, ArrayList<String>> classesInProject) {
        var array = classesInProject.getOrDefault(c + " " + b + " ", new ArrayList<>());
        array.add(a.trim());
        classesInProject.put(c + " " + b + " ", array);
    }

    public static void main(String[] args) throws IOException {
        Map<String, ArrayList<String>> classesInProject = new HashMap<>();
        Pattern pattern = Pattern.compile("(class +[A-Za-z]\\w* *(<\\w+>)? * |interface +[A-Za-z]\\w*)" + "((extends) +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
        Stream<Path> paths = Files.walk(Paths.get("src/LR2/data"));
        paths.filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(f -> f.endsWith(".java"))
                .forEach(f -> {
                    try (Scanner in = new Scanner(new File(f))) {
                        String data = in.useDelimiter("\\A").next();
                        Matcher matcher = pattern.matcher(data);
                        while (matcher.find()){
                            if (!classesInProject.containsKey(matcher.group(1))){
                                classesInProject.put(matcher.group(1),new ArrayList<>());
                            }
                            if (matcher.group(4) != null){
                                addElem(matcher.group(1), matcher.group(5), "class", classesInProject);
                            }
                            if (matcher.group(6) != null){
                                addElem(matcher.group(1), matcher.group(7), "interface", classesInProject);
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        classesInProject.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
    }
}