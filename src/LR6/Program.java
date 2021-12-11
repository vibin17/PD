package LR6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Program {

    public static void main(String[] args) {
        BlockingQueue<String> tasks = new ArrayBlockingQueue<>(100, true);
        BlockingQueue<ArrayList<Entity>> maps = new ArrayBlockingQueue<>(100, true);
        AtomicInteger childCurrent = new AtomicInteger();
        for(int i = 0; i < 4; i++){
            new Thread(new MyRunnable(tasks, maps)).start();
        }
        FutureTask<Map<String, ArrayList<String>>> future = new FutureTask<>(new CollectMap(maps));
        new Thread(future).start();
        try (Stream<Path> filePathStream = Files.walk(Paths.get(""))) {
            filePathStream.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".java"))
                    .forEach(file -> {
                try {
                    tasks.put(file);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            for(int i = 0; i < 4; i++){
                tasks.put("close");
            }
            Map<String, ArrayList<String>> map = future.get();
            map.forEach((key, value) -> {
                System.out.println(key + ": " + value);
                childCurrent.addAndGet(value.size());
            });
            System.out.println("Child current: " + childCurrent);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}