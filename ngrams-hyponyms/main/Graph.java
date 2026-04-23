package main;

import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

public class Graph {

    Map<Integer, Node> words = new HashMap<>();
    public Graph(String synonymsFile, String hyponymsFile) {

        In synonymReader = new In(synonymsFile);
        while (synonymReader.hasNextLine()) {
            String[] lineParts = synonymReader.readLine().split(",");
            int nodeId = Integer.parseInt(lineParts[0]);
            String[] synonyms = lineParts[1].split(" ");
            Node node = new Node(nodeId, synonyms, lineParts[2]);
            words.put(nodeId, node);
        }

        In hyponymReader = new In(hyponymsFile);
        while (hyponymReader.hasNextLine()) {
            String[] lineParts = hyponymReader.readLine().split(",");
            Node parentNode = words.get(Integer.parseInt(lineParts[0]));

            for (int i = 1; i < lineParts.length; i++) {
                Node hyponymNode = words.get(Integer.parseInt(lineParts[i]));
                parentNode.hyponyms.add(hyponymNode);
                hyponymNode.hypernyms.add(parentNode);
            }
        }
    }
}
