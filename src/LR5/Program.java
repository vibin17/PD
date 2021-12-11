package LR5;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;



public class Program {
    public static void main(String[] args) throws IOException {
        Map<String, ArrayList<String>> classesInProject = new HashMap<>();
        Pattern pattern = Pattern.compile("(class|interface) +([A-Za-z]\\w*) *(<\\w+>)? *" +
                "(extends +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
        AtomicInteger childCurrent = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(16);
        ArrayList<Callable<List<Entity>>> executorTasks = new ArrayList<>();

        ArrayList<String> data = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(""))) {
            paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".java"))
                    .forEach(f -> {
                        StringBuilder fileData = new StringBuilder();
                        try (Scanner in = new Scanner(new File(f))) {
                            while (in.hasNextLine()) {
                                fileData.append(in.nextLine()).append(" ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        data.add(fileData.toString());
                    });
        }
        CountDownLatch cdl = new CountDownLatch(data.size());

        data.forEach(file -> {
            executorTasks.add(() -> {
                List<Entity> entityList = new ArrayList<>();
                try {
                    Matcher matcher = pattern.matcher(file);
                    while (matcher.find()) {
                        Entity entity = new Entity();
                        entity.name = matcher.group(2);
                        entity.parentClass = matcher.group(5);
                        entity.interfaces = matcher.group(7);
                        entityList.add(entity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cdl.countDown();
                return entityList;
            });
        });

        List<Future<List<Entity>>> futures = null;
        try {
            futures = executorService.invokeAll(executorTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();


        if (futures != null) {
            futures.forEach(future -> {
                try {
                    List<Entity> result = future.get();
                    for (Entity entity : result){
                        classesInProject.put(entity.name, classesInProject.getOrDefault(entity.name, new ArrayList<>()));
                        if (entity.parentClass != null) {
                            classesInProject.putIfAbsent(entity.parentClass, new ArrayList<>());
                            classesInProject.get(entity.parentClass).add(entity.name);
                        }
                        if (entity.interfaces != null) {
                            classesInProject.putIfAbsent(entity.interfaces, new ArrayList<>());
                            classesInProject.get(entity.interfaces).add(entity.name);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        classesInProject.forEach((key, value) -> {
            System.out.println(key + ": " + value);
            childCurrent.addAndGet(value.size());
        });
        System.out.println("Child current: " + childCurrent);
        System.out.println("Size" + classesInProject.size());
    }
}

public static class Entity {
    public String name = null;
    public String parentClass = null;
    public String interfaces = null;
}}