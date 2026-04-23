# CS-Projects

# NGordnet: Ngrams + Wordnet Explorer

A Java web application built for UC Berkeley's CS 61B (Data Structures) that combines Google's NGrams dataset with Princeton's WordNet lexical database to analyze word frequencies over time and explore semantic word relationships.

## What It Does

**Word Frequency Analysis (NGrams)**
- Looks up how often any word appeared in books over a given time range using Google's NGrams dataset
- Calculates relative word frequency (how common a word was compared to all words that year)
- Generates time series charts comparing multiple words side by side

**Hyponym Explorer (WordNet)**
- Given one or more words, finds all their *hyponyms* — words that are more specific versions of the input (e.g. "jump" and "parachuting" are hyponyms of "descent")
- When given multiple words, returns only the hyponyms **common to all** of them
- Supports a `k` parameter to return only the `k` most frequently occurring hyponyms in a given time range, ranked by total count across the NGrams dataset

## How It Works

The app parses WordNet's synset and hyponym files to build a **directed graph**, where each node is a synset (a set of synonymous words) and edges point from hypernyms to hyponyms. To find all hyponyms of a word, the app performs a graph traversal (DFS) from every node containing that word, collecting all reachable words.

When `k != 0`, results are filtered using the NGrams dataset to return only the top `k` words by total occurrence count in the specified year range.

## Project Structure

```
├── browser/
│   ├── NgordnetQuery.java           # Represents a query to the Ngordnet server
│   ├── NgordnetQueryHandler.java    # Base class for handling queries
│   └── NgordnetServer.java          # Local web server setup (Spark Java)
├── demo/
│   ├── DummyHistoryHandler.java     # Demo handler for history queries
│   ├── DummyHistoryTextHandler.java
│   └── FileReadDemo.java            # Demo for reading data files
├── main/
│   ├── AutograderBuddy.java         # Helper for autograder testing
│   ├── Graph.java                   # Directed graph of WordNet synset relationships
│   ├── HyponymsHandler.java         # Handles hyponym queries, including multi-word and k != 0 cases
│   ├── Main.java                    # Entry point — starts the local web server
│   └── Node.java                    # Represents a synset node with hyponyms and hypernyms
├── ngrams/
│   ├── NGramMap.java                # Loads and queries the NGrams dataset
│   └── TimeSeries.java              # Maps years to word frequency values
└── plotting/
    └── Plotter.java                 # Generates and encodes time series charts using XChart
```

## How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/skygastinel/cs-projects.git
   cd cs-projects/ngrams-hyponyms
   ```

2. **Add the data files** (not included due to size — available via the course):
   ```
   data/ngrams/top_49887_words.csv
   data/ngrams/total_counts.csv
   data/wordnet/synsets.txt
   data/wordnet/hyponyms.txt
   ```

3. **Install dependencies** (requires `algs4.jar` and `xchart` via Maven or Gradle)

4. **Run the server**
   ```bash
   java main.Main
   ```

5. Open `static/ngordnet.html` in your browser and use the Hyponyms button to query the dataset

## Technologies Used

- Java
- [Princeton Algorithms 4 library (algs4)](https://algs4.cs.princeton.edu/) for file I/O
- [XChart](https://knowm.org/open-source/xchart/) for chart generation
- Spark Java for the local web server
- Google NGrams dataset
- Princeton WordNet lexical database
