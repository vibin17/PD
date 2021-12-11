package LR6;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Pattern pattern = Pattern.compile("(class|interface) +([A-Za-z]\\w*) *(<\\w+>)? *" +
                "(extends +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
        Integer ThreadsNumber = 4;
        entityOperation action = (map, entity, parent) -> {
            if (parent != null) {
                map.put(parent, map.getOrDefault(parent, new ArrayList<>()));
                var children = map.getOrDefault(parent, new ArrayList<>());
                children.add(entity);
            }
        };
        BlockingQueue<String> tasks = new ArrayBlockingQueue<>(100, true);
        BlockingQueue<List<Entity>> fragmentedEntities = new ArrayBlockingQueue<>(100, true);
        for (int i = 0; i < ThreadsNumber; i++) {
            new Thread(new CustomRunnable(tasks, fragmentedEntities, pattern)).start();
        }
        FutureTask<Map<String, ArrayList<String>>> future = new FutureTask<>(new CustomCallable(fragmentedEntities, action, ThreadsNumber));
        new Thread(future).start();
        var files = readSourceFiles("");
        for (var file : files) {
            tasks.put(file);
        }
        for (int i = 0; i < ThreadsNumber; i++) {
            tasks.put("close");
        }
        var results = future.get();
        AtomicInteger children = new AtomicInteger();
        results.forEach((e, ch) -> children.addAndGet(ch.size()));
        return;
    }
    public static ArrayList<String> readSourceFiles(String path) throws IOException {
        ArrayList<String> data = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".java"))
                    .forEach(file -> {
                        StringBuilder fileData = new StringBuilder();
                        try (Scanner in = new Scanner(new File(file))) {
                            while (in.hasNextLine()) {
                                fileData.append(in.nextLine()).append(" ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        data.add(fileData.toString());
                    });
        }
        return data;
    }
    static class Entity {
        public String name = null;
        public String parentClass = null;
        public String interfaces = null;
    }
    interface entityOperation {
        void addChildren(Map<String, ArrayList<String>> entitiesMap, String entity, String parent);
    }
}

