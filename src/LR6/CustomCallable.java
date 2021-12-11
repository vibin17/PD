package LR6;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class CustomCallable implements Callable<Map<String, ArrayList<String>>> {
    BlockingQueue<List<Main.Entity>> fragmentedEntities;
    Main.entityOperation action;
    int ThreadsNumber;
    public CustomCallable(BlockingQueue<List<Main.Entity>> fragmentedEntities, Main.entityOperation action, int ThreadsNumber) {
        this.fragmentedEntities = fragmentedEntities;
        this.action = action;
        this.ThreadsNumber = ThreadsNumber;
    }
    @Override
    public Map<String, ArrayList<String>> call() throws InterruptedException {
        var poisonCount = ThreadsNumber;
        Map<String, ArrayList<String>> entitiesMap = new HashMap<>();
        while (poisonCount != 0) {
            List<Main.Entity> result = fragmentedEntities.take();
            if (Objects.equals(result.get(0).name, "poison"))
            {
                poisonCount--;
            }
            else {
                for (Main.Entity entity : result) {
                    entitiesMap.put(entity.name, entitiesMap.getOrDefault(entity.name, new ArrayList<>()));
                    action.addChildren(entitiesMap, entity.name, entity.parentClass);
                    action.addChildren(entitiesMap, entity.name, entity.interfaces);
                }
            }
        }
        return entitiesMap;
    }

}
