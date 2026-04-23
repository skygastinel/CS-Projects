package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.*;
import java.util.stream.Collectors;

public class HyponymsHandler extends NgordnetQueryHandler {
    int startYear;
    int endYear;
    int k;
    String source;
    String target;
    NGramMap nGramMap;
    List<Node> matchingNodes = new ArrayList<>();
    List<String> queryWords = new ArrayList<>();
    List<String> resultWords = new ArrayList<>();

    public HyponymsHandler(String sourceWord, String targetWord, NGramMap nGramMapInstance) {
        source = sourceWord;
        target = targetWord;
        nGramMap = nGramMapInstance;
    }

    @Override
    public String handle(NgordnetQuery query) {
        startYear = query.startYear();
        endYear = query.endYear();
        k = query.k();
        resultWords.clear();
        matchingNodes.clear();
        queryWords = query.words();
        Graph graph = new Graph(source, target);

        List<Set<String>> hyponymSets = new ArrayList<>();

        for (String word : queryWords) {
            matchingNodes.clear();
            for (Node node : graph.words.values()) {
                for (String nodeWord : node.words) {
                    if (nodeWord.equals(word)) {
                        matchingNodes.add(node);
                        break;
                    }
                }
            }
            resultWords.clear();
            for (Node node : matchingNodes) {
                exploreHyponyms(node);
            }

            Set<String> hyponymsSet = new HashSet<>(resultWords);
            hyponymSets.add(hyponymsSet);
        }

        Set<String> commonHyponyms = new HashSet<>(hyponymSets.get(0));

        for (int i = 1; i < hyponymSets.size(); i++) {
            commonHyponyms.retainAll(hyponymSets.get(i));
        }

        if (k != 0) {
            return formatResults(getTopKHyponyms(commonHyponyms));
        }

        return formatResults(new ArrayList<>(commonHyponyms));
    }

    private ArrayList<String> getTopKHyponyms(Set<String> commonHyponyms) {
        Map<String, Double> wordFrequencyMap = new HashMap<>();

        for (String word : commonHyponyms) {
            TimeSeries timeSeries = nGramMap.countHistory(word, startYear, endYear);
            if (timeSeries != null && !timeSeries.isEmpty()) {
                double totalFrequency = timeSeries.values().stream().
                        mapToDouble(Double::doubleValue).
                        sum();
                if (totalFrequency > 0) {
                    wordFrequencyMap.put(word, totalFrequency);
                }
            }
        }

        return wordFrequencyMap.entrySet().stream().
                sorted(Map.Entry.<String, Double>comparingByValue().reversed().
                        thenComparing(Map.Entry.comparingByKey())).
                limit(k).
                map(Map.Entry::getKey).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public void exploreHyponyms(Node node) {
        for (String word : node.words) {
            resultWords.add(word);
        }
        for (Node hyponym : node.hyponyms) {
            exploreHyponyms(hyponym);
        }
    }

    public boolean isHyponym(Node parentNode, Node childNode) {
        if (parentNode.hyponyms.contains(childNode)) {
            return true;
        }
        for (Node hyponym : parentNode.hyponyms) {
            if (isHyponym(hyponym, childNode)) {
                return true;
            }
        } return false;
    }

    public static String formatResults(ArrayList<String> list) {
        Set<String> uniqueSet = new HashSet<>(list);
        ArrayList<String> resultList = new ArrayList<>(uniqueSet);
        Collections.sort(resultList);
        return resultList.toString();
    }

}

