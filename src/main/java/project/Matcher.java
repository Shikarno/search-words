package project;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class Matcher {

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private final Trie trie;

    public Matcher(Trie trie) {
        this.trie = trie;
    }

    public Future<Map<String, List<Match>>> submit(List<String> chunkLines, long startOffset) {
        return forkJoinPool.submit(
           new MatcherTask(chunkLines, startOffset));
    }

    public void shutDown() {
        forkJoinPool.shutdown();
    }

    private class MatcherTask extends RecursiveTask<Map<String, List<Match>>> {


        private static final int LIST_SIZE_THRESHOLD = 1000;

        private final List<String> list;

        private final long startOffset;

        public MatcherTask(List<String> list, long startOffset) {
            this.list = list;
            this.startOffset = startOffset;
        }

        @Override
        protected Map<String, List<Match>> compute() {
            if (list.size() > LIST_SIZE_THRESHOLD) {
                Map<String, List<Match>> result = new HashMap<>();
                ForkJoinTask.invokeAll(createSubTasks())
                        .stream()
                        .map(ForkJoinTask::join)
                        .flatMap(map -> map.entrySet().stream())
                        .forEach(entry -> result.computeIfAbsent(
                                entry.getKey(),
                                k -> new ArrayList<>()
                        ).addAll(entry.getValue()));
                return result;
            } else {
                return executeSearch(list, startOffset);
            }
        }

        private Map<String, List<Match>> executeSearch(List<String> chunkLines, long startOffset) {
            Map<String, List<Match>> result = new HashMap<>();
            long lineOffset = startOffset;
            for (String line : chunkLines) {
                Collection<Emit> emits = trie.parseText(line);
                for (Emit emit : emits) {
                    result.computeIfAbsent(emit.getKeyword(), K -> new ArrayList<>()).add(new Match(lineOffset, emit.getStart()));
                }
                lineOffset++;
            }
            return result;
        }

        protected List<MatcherTask> createSubTasks() {
            List<MatcherTask> subtasks = new ArrayList<>();
            List<String> partOne = list.subList(0, list.size() / 2);
            List<String> partTwo = list.subList(list.size() / 2, list.size());
            subtasks.add(new MatcherTask(partOne, startOffset));
            subtasks.add(new MatcherTask(partTwo, startOffset + list.size() / 2));
            return subtasks;
        }


    }
}