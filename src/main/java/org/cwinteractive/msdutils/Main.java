package org.cwinteractive.msdutils;

import org.apache.commons.math3.linear.RealMatrix;
import org.cwinteractive.msdutils.models.Song;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static List<String> getFirstThreeLines() throws IOException {
            InputStreamReader inReader = new InputStreamReader(new FileInputStream("/home/dreamchild/projects/hack/payara/mxm_dataset_test.txt"));
            BufferedReader bufferedReader = new BufferedReader(inReader);

            List<String> lines = bufferedReader.lines().skip(20).limit(3).toList();
            return lines;
    }

    private static void vectorizeMSDatasetSongsExample() throws FileNotFoundException {
        TermDictionary termDictionary = new TermDictionary();
        SimpleTokenizer tokenizer = new SimpleTokenizer();

        List<String> terms = SongUtils.getDatasetTerms();
        // String[] termsArray = terms.toArray(new String[0]);
        // termDictionary.addTerms(termsArray);

        List<String> songsSlice = SongUtils.getSliceOfDatasetSongs(1, 5);
        List<String> songDocs = SongUtils.getSongsInSlice(songsSlice).stream()
                .map(Song::toDocument).toList();

        for(String doc : songDocs) {
            String[] tokens = tokenizer.getTokens(doc);
            termDictionary.addTerms(tokens);
        }

        Vectorizer vectorizer = new Vectorizer(termDictionary, tokenizer, false);
        RealMatrix counts = vectorizer.getCountMatrix(songDocs);
        System.out.println("Song counts matrix:");
        System.out.println(counts.getSubMatrix(0, 4, 0, 20));

        Vectorizer binvectorizer = new Vectorizer(termDictionary, tokenizer, true);
        RealMatrix binCounts = vectorizer.getCountMatrix(songDocs);
        System.out.println("Song bincounts matrix:");
        System.out.println(binCounts.getSubMatrix(0, 4, 0, 20));
//        System.out.println("Vector for first song: ");

        TFIDFVectorizer tfidfVectorizer = new TFIDFVectorizer(termDictionary, tokenizer);
        RealMatrix tfidf = tfidfVectorizer.getTFIDF(songDocs);

        System.out.println("Song tfidf matrix");
        System.out.println(tfidf.getSubMatrix(0, 4, 0, 20));
    }

    public static void main(String[] args) {
        System.out.println("Loading dataset ...");
        try {
            // test reading MXM dataset
            // getFirstThreeLines().forEach(System.out::println);
            // SongUtils.getDatasetTerms().stream().limit(10).forEach(System.out::println);

            // test SongUtils slicing
            // var songsSlice = SongUtils.getSliceOfDatasetSongs(20, 3);
            // var songs = SongUtils.getSongsInSlice(songsSlice);
            // songs.forEach(System.out::println);

            // test vectorizer
            vectorizeMSDatasetSongsExample();
        } catch(IOException ioe) {
            System.err.println("An error occurred: " + ioe.getMessage());
            ioe.printStackTrace();
        }

    }
}