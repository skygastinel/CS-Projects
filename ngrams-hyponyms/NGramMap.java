package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.Collection;
//import java.util.HashMap;
import java.util.TreeMap;

import static ngrams.TimeSeries.MAX_YEAR;
import static ngrams.TimeSeries.MIN_YEAR;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    private TreeMap<String, TimeSeries> wordFreq;
    //frequency of words over time
    private TimeSeries wordCountTS;
    //total word count for that year in a timeseries
    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {

        wordFreq = new TreeMap<String, TimeSeries>();
        wordCountTS = new TimeSeries();
        //word counts TS is total count

        In inWordsFile = new In(wordsFilename);
        //this opens the file!!
        while (inWordsFile.hasNextLine()) {
            String[] lineData = inWordsFile.readLine().split("\t");

            String word = lineData[0];
            int year = Integer.parseInt(lineData[1]);
            long frequency = Long.parseLong(lineData[2]);
            //0 1 2 splits the file

            TimeSeries timeSeries = wordFreq.getOrDefault(word, new TimeSeries());

            timeSeries.put(year, (double) frequency);

            wordFreq.put(word, timeSeries);
        }

        In countsFile = new In(countsFilename);
        while (countsFile.hasNextLine()) {
            String[] lineData = countsFile.readLine().split(",");

            int year = Integer.parseInt(lineData[0]);
            long totalCount = Long.parseLong(lineData[1]);

            wordCountTS.put(year, (double) totalCount);
        }
        //finds total word count
    }


    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        //if empty... return empty

        int startY = startYear;
        int endY = endYear;

        if (!wordFreq.containsKey(word)) {
            return new TimeSeries();
        }

        if (startY < MIN_YEAR) {
            startY = MIN_YEAR;
        }
        if (endY > MAX_YEAR) {
            endY = MAX_YEAR;
        }

        if (startY > endY) {
            return new TimeSeries();
        }

        TimeSeries fullTS = wordFreq.get(word);

        return new TimeSeries(fullTS, startY, endY);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        //if empty... return empty
        if (!wordFreq.containsKey(word)) {
            return new TimeSeries();
        }

        TimeSeries fullTS = wordFreq.get(word);
        return new TimeSeries(fullTS);
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        return new TimeSeries(wordCountTS);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        //if empty... return empty
        if (!wordFreq.containsKey(word)) {
            return new TimeSeries();
        }

        if (startYear < MIN_YEAR) {
            startYear = MIN_YEAR;
        }

        if (endYear > MAX_YEAR) {
            endYear = MAX_YEAR;
        }

        TimeSeries wordHisTS = countHistory(word, startYear, endYear);
        TimeSeries relFreqTS = new TimeSeries();

        //relative frequencies of words
        for (Integer year : wordHisTS.keySet()) {
            if (wordCountTS.containsKey(year)) {
                double totalWordsDub = wordCountTS.get(year);
                double relativeFrequencyDub = wordHisTS.get(year) / totalWordsDub;
                relFreqTS.put(year, relativeFrequencyDub);
            }
        }

        return relFreqTS;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        //if empty... return empty
        if (!wordFreq.containsKey(word)) {
            return new TimeSeries();
        }


        TimeSeries wordHistTS = countHistory(word);
        TimeSeries relFreqTS = new TimeSeries();


        for (Integer year : wordHistTS.keySet()) {
            if (wordCountTS.containsKey(year)) {
                double totalW = wordCountTS.get(year);
                if (totalW != 0) {
                    double relFrequency = wordHistTS.get(year) / totalW;
                    //fancy rounding for test purposes .. . idek if this is necessary tbh
                    //relFrequency = Math.round(relFrequency * 1E10) / 1E10;
                    relFreqTS.put(year, relFrequency);
                }
            }
        }

        return relFreqTS;

    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words, int startYear, int endYear) {
        TimeSeries summedReturn = new TimeSeries();

        for (String word : words) {
            TimeSeries wordRel = weightHistory(word, startYear, endYear);

            //summing rel frequency per yearrrr
            for (Integer year : wordRel.keySet()) {
                double currentWeight = wordRel.get(year);
                double newWeight = summedReturn.getOrDefault(year, 0.0) + currentWeight;
                //fancy rounding... still not sure how necessary this is
                //newWeight = Math.round(newWeight * 1E10) / 1E10;
                summedReturn.put(year, newWeight);
            }
        }

        return summedReturn;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries sumRel = new TimeSeries();

        for (String word : words) {
            TimeSeries wordRelativeFreq = weightHistory(word);
            for (Integer year : wordRelativeFreq.keySet()) {
                sumRel.put(year, sumRel.getOrDefault(year, 0.0) + wordRelativeFreq.get(year));
            }
        }

        return sumRel;
    }

}
