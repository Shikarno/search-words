package project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aggregator {

    private final Logger logger = Logger.getLogger(Aggregator.class.getName());

    public Map<String, List<Match>> collect(List<Future<Map<String, List<Match>>>> futures) {
        Map<String, List<Match>> result = new HashMap<>();
        for (Future<Map<String, List<Match>>> future : futures) {
            try {
                future.get().forEach((key, value) -> result.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value));
            } catch (InterruptedException | ExecutionException e) {
                //should not happen
                logger.log(Level.SEVERE, "Exception occured " + e);
            }
        }
        return result;
    }
}
