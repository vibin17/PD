package LR6;

import LR5.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class CollectMap implements Callable<Map<String, ArrayList<String>>> {
    private BlockingQueue<ArrayList<Entity>> maps;
    Map<String, ArrayList<String>> map = new HashMap<>();

    public CollectMap(BlockingQueue<ArrayList<Entity>> maps) {
        this.maps = maps;
    }

    @Override
    public Map<String, ArrayList<String>> call() throws Exception {
        int count = 0;

        while (count != 4) {
            ArrayList<Entity> tempMap = maps.take();
            if(tempMap.size() == 0) {
                count++;
                continue;
            } else {
                for (Entity entity : tempMap){
                    map.put(entity.name, map.getOrDefault(entity.name, new ArrayList<>()));
                    if (entity.parentClass != null) {
                        map.putIfAbsent(entity.parentClass, new ArrayList<>());
                        map.get(entity.parentClass).add(entity.name);
                    }
                    if (entity.interfaces != null) {
                        map.putIfAbsent(entity.interfaces, new ArrayList<>());
                        map.get(entity.interfaces).add(entity.name);
                    }
                }
            }
        }
        return map;
    }
}

