package LR6;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomRunnable implements Runnable {
    BlockingQueue<String> tasks;
    BlockingQueue<List<Main.Entity>> fragmentedEntities;
    String poisonCode = "close";
    Pattern pattern;
    public CustomRunnable(BlockingQueue<String> tasks, BlockingQueue<List<Main.Entity>> fragmentedEntities, Pattern pattern) {
        this.tasks = tasks;
        this.fragmentedEntities = fragmentedEntities;
        this.pattern = pattern;
    }
    @Override
    public void run() {
        try {
            String fileContent;
            while (!((fileContent = tasks.take()).equals(poisonCode))) {
                List<Main.Entity> entityList = new ArrayList<>();
                Matcher matcher = pattern.matcher(fileContent);
                while (matcher.find()) {
                    Main.Entity entity = new Main.Entity();
                    entity.name = matcher.group(2);
                    entity.parentClass = matcher.group(5);
                    entity.interfaces = matcher.group(7);
                    entityList.add(entity);
                }
                if (entityList.size() != 0) {
                    fragmentedEntities.put(entityList);
                }
            }
            var poisonEntity = new Main.Entity();
            poisonEntity.name = "poison";
            fragmentedEntities.put(List.of(poisonEntity));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
