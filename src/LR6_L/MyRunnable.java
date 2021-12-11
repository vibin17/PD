package LR6_L;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MyRunnable implements Runnable {
    private BlockingQueue<Map<String, Set<String>>> maps;
    private BlockingQueue<String> tasks;
    private String poison = "close";

    public MyRunnable(BlockingQueue<String> tasks, BlockingQueue<Map<String, Set<String>>> maps) {
        this.tasks = tasks;
        this.maps = maps;
    }

    public void fillMap(Matcher matcher, Stream<String> stream, Map<String, Set<String>> result){
        stream.forEach(str1 -> {
            Set<String> set = result.getOrDefault(str1, new HashSet<>());
            set.add(matcher.group(1) + " " + matcher.group(2));
            result.put(str1, set);
        });
    }

    @Override
    public void run() {
        try {
            String file;
            while (!((file = tasks.take()).equals(poison))) {
                Map<String, Set<String>> result = new HashMap<>();
                Files.lines(Paths.get(file), StandardCharsets.ISO_8859_1).forEach(str -> {
                    Pattern pattern = Pattern.compile("(class|interface)\\s+(\\w+(\\s*<.+>)?)\\s+((extends|implements)\\s+(\\w+(,\\s+\\w+)*))\\s*(implements\\s+(\\w+(,\\s+\\w+)*))?\\s*");
                    Matcher matcher = pattern.matcher(str);
                    if (matcher.find()) {
                        if (matcher.group(9) != null) {
                            fillMap(matcher, Stream.concat(Arrays.stream(matcher.group(6).split(",\\s+(?=(?:[^<>]*<[^<>]*>)*[^<>]*$)")), Arrays.stream(matcher.group(9).split(",\\s+(?=(?:[^<>]*<[^<>]*>)*[^<>]*$)"))),result);
                        } else {
                            fillMap(matcher, Arrays.stream(matcher.group(6).split(",\\s+(?=(?:[^<>]*<[^<>]*>)*[^<>]*$)")), result);
                        }
                    }
                });
                maps.put(result);
            }
            Map<String, Set<String>> poisonPill = new HashMap<>();
            poisonPill.put("poison", new HashSet<>());
            maps.put(poisonPill);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

