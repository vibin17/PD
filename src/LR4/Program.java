package LR4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Program {
    public static void main(String[] args) throws IOException {
        Map<String, ArrayList<String>> classesInProject = new HashMap<>();
        Pattern pattern = Pattern.compile("(class +[A-Za-z]\\w* *(<\\w+>)? * |interface +[A-Za-z]\\w*)" + "((extends) +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
        Stream<Path> paths = Files.walk(Paths.get("../spring-framework-main"));
        AtomicInteger childCurrent = new AtomicInteger();
        Stream<Path> paths_for_count = Files.walk(Paths.get("../spring-framework-main"));
        CountDownLatch cdl = new CountDownLatch((int) paths_for_count.filter(Files::isRegularFile).map(Path::toString).filter(f -> f.endsWith(".java")).count());
        paths.filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(f -> f.endsWith(".java"))
                .forEach(f -> new Thread(() -> {
                    try (Scanner in = new Scanner(new File(f))) {
                        String data = in.useDelimiter("\\A").next();
                        Matcher matcher = pattern.matcher(data);
                        while (matcher.find()){
                            synchronized (classesInProject) {
                                if (!classesInProject.containsKey(matcher.group(1))){
                                    classesInProject.put(matcher.group(1),new ArrayList<>());
                                }
                                if (matcher.group(4) != null){
                                    var array = classesInProject.getOrDefault("class" + " " + matcher.group(5) + " ", new ArrayList<>());
                                    array.add(matcher.group(1).trim());
                                    classesInProject.put("class" + " " + matcher.group(5) + " ", array);
                                }
                                if (matcher.group(6) != null){
                                    var array = classesInProject.getOrDefault("interface" + " " + matcher.group(7) + " ", new ArrayList<>());
                                    array.add(matcher.group(1).trim());
                                    classesInProject.put("interface" + " " + matcher.group(7), array);
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    cdl.countDown();
                }).start());
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        classesInProject.forEach((key, value) -> {
            System.out.println(key + ": " + value);
            childCurrent.addAndGet(value.size());
        });
        System.out.println("Child current: " + childCurrent);
    }
}