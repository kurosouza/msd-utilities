package org.cwinteractive.msdutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import org.apache.commons.math3.linear.RealMatrix;
import org.cwinteractive.msdutils.export.JsonExporter;
import org.cwinteractive.msdutils.llm.LLMVectorizer;
import org.cwinteractive.msdutils.models.Song;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Main {

    private static ObjectMapper mapper;
    private static LLMVectorizer llmVectorizer;

    static {
        llmVectorizer = LLMVectorizer.getInstance();
        mapper = new ObjectMapper();
    }

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    private static List<String> getFirstThreeLines() throws IOException {
        InputStreamReader inReader = new InputStreamReader(new FileInputStream("/home/dreamchild/projects/hack/payara/mxm_dataset_test.txt"));
        BufferedReader bufferedReader = new BufferedReader(inReader);

        List<String> lines = bufferedReader.lines().skip(20).limit(3).toList();
        return lines;
    }

    private static List<Song> analyzeSongsWithLLM(List<Song> songs, Map<String, List<String>> songsMetadata) throws FileNotFoundException, IOException {
        LOG.debug("Embedding values are being computed ..");
        for(Song song : songs) {
            Embedding embedding = llmVectorizer.getEmbedding(song.getSongText());
            double[] embeddings_double_values = IntStream.range(0, embedding.vector().length).mapToDouble(j -> embedding.vector()[j]).toArray();
            song.setTfidfVec(embeddings_double_values);
        }
        LOG.debug("Songs updated with scores.");

        return songs;
    }

    private static List<Song> analyzeSongs(Map<String, List<String>> songsMetadata) throws FileNotFoundException, IOException {

        LOG.debug("Loading songs ..");
        List<String> songsSlice = SongUtils.getSliceOfDatasetSongs(0, 5);
        List<Song> songs = SongUtils.getSongsInSlice(songsSlice, songsMetadata);
        List<String> songTexts = songs.stream()
                .map(Song::getSongText).toList();
        LOG.debug("Songs data loaded.");
        TFIDFVectorizer tfidfVectorizer = SongUtils.getTFIDFVectorizer(songTexts);
        LOG.debug("Computing TFIDF values for songs ..");
        RealMatrix tfidf = tfidfVectorizer.getTFIDF(songTexts);
        LOG.debug("TFIDF values computed.");

        for(int i = 0; i < songs.size(); i++) {
            double[] songTfidf = tfidf.getRow(i);
            songs.get(i).setTfidfVec(songTfidf);
        }
        LOG.debug("Songs updated with scores.");

        //Arrays.stream(tfidf.getRow(0)).forEach(i -> System.out.print(i + ", "));
        return songs;
    }

    public static void main(String[] args) {
        System.out.println("Loading dataset ...");
        try {
            LOG.debug("Loading songs metadata ..");
            Map<String, List<String>> songsMetadata = SongUtils.loadSongInfo();
            LOG.debug("Songs metadata loaded.");
            LOG.debug("Loading songs ..");
            List<String> songsSlice = SongUtils.getSliceOfDatasetSongs(0, 2000);
            List<Song> songs = SongUtils.getSongsInSlice(songsSlice, songsMetadata);
            LOG.debug("Songs data loaded.");
            List<Song> analyzedSongs = analyzeSongsWithLLM(songs, songsMetadata);
            JsonExporter.export(songs);
            LOG.debug("Song processing completed");
            // Arrays.stream(tfidf.getRow(0)).forEach(i -> System.out.print(i + ", "));
        } catch (IOException ioe) {
            System.err.println("An error occurred: " + ioe.getMessage());
            ioe.printStackTrace();
        }

    }
}