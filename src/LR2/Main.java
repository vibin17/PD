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

public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, ArrayList<String>> classes = new HashMap<>();
        Stream<Path> paths = Files.walk(Paths.get("src/LR2/input"));
        paths.filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(f -> f.endsWith(".java"))
                .forEach(f -> {
                    String data = "";
                    try (Scanner in = new Scanner(new File(f))) {
                        data = in.useDelimiter("\\A").next();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    Pattern pattern = Pattern.compile("(class|interface) +([A-Za-z]\\w*) *(<\\w+>)? *" +
                            "(extends +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(data);
                    while (matcher.find()) {
                        var name = matcher.group(2);
                        classes.put(name, classes.getOrDefault(name, new ArrayList<>()));
                        var parentClass = matcher.group(5);
                        if (parentClass != null) {
                            classes.put(parentClass, classes.getOrDefault(parentClass, new ArrayList<>()));
                            var parentHeirs = classes.getOrDefault(parentClass, new ArrayList<>());
                            parentHeirs.add(name);
                        }
                        var parentInterface = matcher.group(7);
                        if (parentInterface != null) {
                            classes.put(parentInterface, classes.getOrDefault(parentInterface, new ArrayList<>()));
                            var parentHeirs = classes.getOrDefault(parentInterface, new ArrayList<>());
                            parentHeirs.add(name);
                        }
                    }
                });
        System.out.println(classes);
    }
}
