package org.cwinteractive.msdutils.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cwinteractive.msdutils.models.Song;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonExporter {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static void export(List<Song> songs) throws IOException {
        assert songs != null;
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File("songs.json"), songs );
    }
}
