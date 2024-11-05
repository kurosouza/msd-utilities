package org.cwinteractive.msdutils;

import org.cwinteractive.msdutils.models.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SongUtils {

    private static final String MILLIONSONG_FILE_PATH = "/home/dreamchild/projects/hack/payara/MillionSongSubset";
    private static final String MUSIXMATCH_FILE_PATH = "/home/dreamchild/projects/hack/payara/mxm_dataset_train.txt";
    private static final String SONG_INFO_FILE_PATH = "/home/dreamchild/projects/hack/payara/unique_tracks.txt";
    private static final Integer SONG_LINE_START = 19;
    private static final Integer ALL_WORDS_LINE_IDX = 17;

    private static Logger LOG = LoggerFactory.getLogger(SongUtils.class);


    public Dictionary getSubsetDictionary() throws IOException {
        FileReader fileReader = new FileReader(MUSIXMATCH_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.lines().skip(19).limit(3);

        throw new UnsupportedEncodingException();
    }

    public static Map<String, List<String>> loadSongInfo() throws FileNotFoundException {
        FileReader fileReader = new FileReader(SONG_INFO_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        Map<String, List<String>> songMetadata = bufferedReader.lines().map(line -> {
            String[] songInfo = line.split("<SEP>");
            if(songInfo.length < 4) { return null; }
            return List.<String>of(songInfo[0], songInfo[2], songInfo[3]);
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(i -> i.get(0), v -> List.of(v.get(1), v.get(2))));

        return songMetadata;
    }

    public static List<String> getDatasetTerms() throws FileNotFoundException {
        FileReader fileReader = new FileReader(MUSIXMATCH_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.lines().skip(ALL_WORDS_LINE_IDX).limit(1)
                .findFirst().orElseThrow(() -> new RuntimeException("Could not retrieve line."));
        String[] terms = line.substring(1, line.length() - 1).split(",");

        // unskip this NOW!!
        return Arrays.stream(terms).toList();
    }

    public static List<String> getSliceOfDatasetSongs(int start, int count) throws FileNotFoundException {
        FileReader fileReader = new FileReader(MUSIXMATCH_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = bufferedReader.lines().skip(SONG_LINE_START + start).limit(count)
                .toList();
        return lines;
    }

    public static List<Song> getSongsInSlice(List<String> lines, Map<String, List<String>> songsMetadata) throws FileNotFoundException {
        Objects.requireNonNull(songsMetadata);
        List<String> terms = getDatasetTerms();
        Map<String, Integer> termCounts = new HashMap<>();
        List<Song> songs = lines.stream().map(line -> {
            String[] chunks = line.split(",");
            assert chunks.length > 2;
            Song song = new Song();
            song.setMsID(chunks[0]);
            song.setMxmID(chunks[1]);
            song.setArtist(songsMetadata.get(chunks[0]).get(0));
            song.setTitle(songsMetadata.get(chunks[0]).get(1));
            for(int i = 2; i < chunks.length; i++) {
                String chunk = chunks[i];
                String[] splitTermCounts = chunk.split(":");
                String rawTerm = terms.get(Integer.valueOf(splitTermCounts[0]));
                String term = rawTerm.replaceAll("[^A-Za-z\']+" , "");
                Integer freq = Integer.valueOf(splitTermCounts[1]);

                StringBuffer songTextBuilder = new StringBuffer();
                // append previous song text - so that we do not lose it
                songTextBuilder.append(song.getSongText());
                songTextBuilder.append((term + " ").repeat(freq));
                song.setSongText(songTextBuilder.toString());

                termCounts.put(term, freq);
            }
            song.setWords(termCounts);
            return song;
        }).toList();

        return songs;
    }

    public static TFIDFVectorizer getTFIDFVectorizer(List<String> corpus) throws IOException {
        TermDictionary termDictionary = new TermDictionary();
        SimpleTokenizer tokenizer = new SimpleTokenizer();

        List<String> terms = SongUtils.getDatasetTerms();

        for(String doc : corpus) {
            String[] tokens = tokenizer.getTokens(doc);
            termDictionary.addTerms(tokens);
        }

        TFIDFVectorizer tfidfVectorizer = new TFIDFVectorizer(termDictionary, tokenizer);

        return tfidfVectorizer;
    }

    public static Vectorizer getVectorizer(int start, int end, boolean isBinary, Map<String, List<String>> songsMetadata) throws IOException {
        Objects.requireNonNull(songsMetadata);

        TermDictionary termDictionary = new TermDictionary();
        SimpleTokenizer tokenizer = new SimpleTokenizer();

        List<String> terms = SongUtils.getDatasetTerms();

        List<String> songsSlice = SongUtils.getSliceOfDatasetSongs(start, end);
        List<String> songTexts = SongUtils.getSongsInSlice(songsSlice, songsMetadata).stream()
                .map(Song::getSongText).toList();

        for(String doc : songTexts) {
            String[] tokens = tokenizer.getTokens(doc);
            termDictionary.addTerms(tokens);
        }

        Vectorizer vectorizer = new Vectorizer(termDictionary, tokenizer, isBinary);

        return vectorizer;
    }
}
