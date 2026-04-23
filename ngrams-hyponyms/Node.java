package main;

import java.util.ArrayList;
import java.util.List;

public class Node {
    int id;
    String[] words;
    String definition;
    List<Node> hyponyms = new ArrayList<>();
    List<Node> hypernyms = new ArrayList<>();

    public Node(int id, String[] words, String definition) {
        this.id = id;
        this.words = words;
        this.definition = definition;
    }
}
