import project.MyFileReader;

import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        Set<String> keyWords = Set.of("James","John","Robert","Michael","William","David","Richard","Charles","Joseph","Thomas",
                "Christopher","Daniel","Paul","Mark", "Donald","George","Kenneth","Steven","Edward","Brian",
                "Ronald","Anthony","Kevin","Jason","Matthew","Gary","Timothy","Jose","Larry","Jeffrey",
                "Frank","Scott","Eric","Stephen","Andrew","Raymond","Gregory","Joshua","Jerry","Dennis",
                "Walter","Patrick","Peter","Harold","Douglas","Henry","Carl","Arthur","Ryan","Roger");
        new MyFileReader(keyWords).read("src/main/resources/big.txt");
    }
}