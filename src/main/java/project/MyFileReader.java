package project;

import org.ahocorasick.trie.Trie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class MyFileReader {

    private static final int CHUNK_SIZE = 10000;
    private final Matcher matcher;

    private final Aggregator aggregator = new Aggregator();

    public MyFileReader(Collection<String> words) {
        matcher = new Matcher(Trie.builder().onlyWholeWords().addKeywords(words).build());
    }

    public void read(String filename) throws Exception {
        List<Future<Map<String, List<Match>>>> futures = new ArrayList<>();

        FileInputStream fileInputStream= new FileInputStream(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        long lineOffset = 0;
        List<String> linesChunk;
        do {
            linesChunk = getLinesChunk(bufferedReader);
            futures.add(matcher.submit(linesChunk, lineOffset));
            lineOffset += linesChunk.size();
        } while (linesChunk.size() == CHUNK_SIZE);

        System.out.println(aggregator.collect(futures));
        matcher.shutDown();
        bufferedReader.close();
    }

    private static List<String> getLinesChunk(BufferedReader br) throws IOException {
        List<String> chunkList = new ArrayList<>();
        for(int i = 0; i < CHUNK_SIZE; ++i) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            chunkList.add(line);
        }
        return chunkList;
    }


}
