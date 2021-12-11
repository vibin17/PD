package LR6;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyRunnable implements Runnable {
    private BlockingQueue<ArrayList<Entity>> maps;
    private BlockingQueue<String> tasks;
    private String poison = "close";

    public MyRunnable(BlockingQueue<String> tasks, BlockingQueue<ArrayList<Entity>> maps) {
        this.tasks = tasks;
        this.maps = maps;
    }

    @Override
    public void run() {
        try {
            String file;
            Pattern pattern = Pattern.compile("(class|interface) +([A-Za-z]\\w*) *(<\\w+>)? *" +
                    "(extends +([A-Za-z]\\w*))? *(implements +([A-Za-z]\\w*))?", Pattern.MULTILINE);
            while (!((file = tasks.take()).equals(poison))) {
                StringBuilder fileData = new StringBuilder();
                try (Scanner in = new Scanner(new File(file))) {
                    while (in.hasNextLine()) {
                        fileData.append(in.nextLine()).append(" ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
             ArrayList<Entity> result = new ArrayList<>();
             try {
                   Matcher matcher = pattern.matcher(fileData.toString());
                      while (matcher.find()) {
                          Entity entity = new Entity();
                          entity.name = matcher.group(2);
                          entity.parentClass = matcher.group(5);
                          entity.interfaces = matcher.group(7);
                          result.add(entity);
                      }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
             if (result.size() != 0){
                 maps.put(result);
             }
            }
            ArrayList<Entity> a = new ArrayList();
            Entity b = new Entity();
            b.name = "poison";
            a.add(b);
            maps.put(a);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Entity {
    public String name = null;
    public String parentClass = null;
    public String interfaces = null;
}

