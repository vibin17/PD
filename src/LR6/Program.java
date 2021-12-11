package LR6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Program {

    public static void main(String[] args) {
        BlockingQueue<String> tasks = new ArrayBlockingQueue<>(100, true);
        BlockingQueue<Map<String, Set<String>>> maps = new ArrayBlockingQueue<>(100, true);
        for(int i = 0; i < 4; i++){
            new Thread(new MyRunnable(tasks, maps)).start();
        }
        FutureTask<Map<String, Set<String>>> future = new FutureTask<>(new CollectMap(maps));
        new Thread(future).start();
        try (Stream<Path> filePathStream = Files.walk(Paths.get("src/LR2/data"))) {
            List<Path> listPath = filePathStream.filter(Files::isRegularFile).collect(Collectors.toList());
            listPath.forEach(file -> {
                try {
                    tasks.put(file.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            for(int i = 0; i < 4; i++){
                tasks.put("close");
            }
            Map<String, Set<String>> map = future.get();
            map.forEach((x,y)-> System.out.println(x + " базовый для " + y.size()));
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
