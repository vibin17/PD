package LR6;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectMap implements Callable<Map<String, Set<String>>> {
    private BlockingQueue<Map<String, Set<String>>> maps;
    Map<String, Set<String>> map = new HashMap<>();

    public CollectMap(BlockingQueue<Map<String, Set<String>>> maps) {
        this.maps = maps;
    }

    @Override
    public Map<String, Set<String>> call() throws Exception {
        int count = 0;
        while (count != 4) {
            Map<String, Set<String>> tempMap = maps.take();
            if(tempMap.containsKey("poison")) {
                count++;
                continue;
            }
            map = Stream.concat(map.entrySet().stream(), tempMap.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> Stream.concat(e1.stream(), e2.stream())
                                    .collect(Collectors.toSet())
                    ));
        }
        return map;
    }
}

