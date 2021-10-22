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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, ArrayList<String>> entities = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get("src/LR2/input"))) {
            paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".java"))
                    .forEach(f -> {
                        StringBuilder data = new StringBuilder();
                        try (Scanner in = new Scanner(new File(f))) {
                            while (in.hasNextLine()) {
                                data.append(in.nextLine()).append(" ");
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        Pattern pattern = Pattern.compile("(class|interface) +([A-Za-z]\\w*) *(<\\w+>)? *" +
                                "(extends +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
                        Matcher matcher = pattern.matcher(data.toString());
                        while (matcher.find()) {
                            var name = matcher.group(2);
                            var parentClass = matcher.group(5);
                            var parentInterface = matcher.group(7);
                            entities.put(name, entities.getOrDefault(name, new ArrayList<>()));
                            Consumer<String> action = (parent) -> {
                                if (parent != null) {
                                    entities.put(parent, entities.getOrDefault(parent, new ArrayList<>()));
                                    var children = entities.getOrDefault(parent, new ArrayList<>());
                                    children.add(name);
                                }
                            };
                            action.accept(parentClass);
                            action.accept(parentInterface);
                        }
                    });
        }
        System.out.println(entities);
    }
}
