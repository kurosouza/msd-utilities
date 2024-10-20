package org.cwinteractive.msdutils;

import org.cwinteractive.msdutils.models.Song;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongUtils {

    private static final String MILLIONSONG_FILE_PATH = "/home/dreamchild/projects/hack/payara/MillionSongSubset";
    private static final String MUSIXMATCH_FILE_PATH = "/home/dreamchild/projects/hack/payara/mxm_dataset_train.txt";
    private static final Integer SONG_LINE_START = 18;
    private static final Integer SONG_LINE_END = 50;
    private static final Integer ALL_WORDS_LINE_IDX = 17;

    public Dictionary getSubsetDictionary() throws IOException {
        FileReader fileReader = new FileReader(MUSIXMATCH_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.lines().skip(19).limit(3);

        throw new UnsupportedEncodingException();
    }

    public static List<String> getDatasetTerms() throws FileNotFoundException {
        FileReader fileReader = new FileReader(MUSIXMATCH_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.lines().skip(ALL_WORDS_LINE_IDX).limit(1)
                .findFirst().orElseThrow(() -> new RuntimeException("Could not retrieve line."));
        String[] terms = line.split(",");
        return Arrays.stream(terms).skip(1).toList();
    }

    public static List<String> getSliceOfDatasetSongs(int start, int count) throws FileNotFoundException {
        FileReader fileReader = new FileReader(MUSIXMATCH_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = bufferedReader.lines().skip(SONG_LINE_START + start).limit(count)
                .toList();
        return lines;
    }

    public static List<Song> getSongsInSlice(List<String> lines) throws FileNotFoundException {
        List<String> terms = getDatasetTerms();
        Map<String, Integer> termCounts = new HashMap<>();
        List<Song> songs = lines.stream().map(line -> {
            String[] chunks = line.split(",");
            assert chunks.length > 2;
            Song song = new Song();
            song.setMsID(chunks[0]);
            song.setMxmID(chunks[1]);

            for(int i = 2; i < chunks.length; i++) {
                String chunk = chunks[i];
                String[] splitTermCounts = chunk.split(":");
                String term = terms.get(Integer.valueOf(splitTermCounts[0]));
                Integer freq = Integer.valueOf(splitTermCounts[1]);
                termCounts.put(term, freq);
            }
            song.setWords(termCounts);
            return song;
        }).toList();

        return songs;
    }

}
