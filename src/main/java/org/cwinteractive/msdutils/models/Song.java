package org.cwinteractive.msdutils.models;

import java.util.HashMap;
import java.util.Map;

public class Song {
    private String msID;
    private String mxmID;
    private String title;
    private String artist;
    private Integer year;
    private Map<String, Integer> words = new HashMap<>();

    public Song() {}

    // getters and setters

    public String getMsID() {
        return msID;
    }

    public void setMsID(String msID) {
        this.msID = msID;
    }

    public String getMxmID() {
        return mxmID;
    }

    public void setMxmID(String mxmID) {
        this.mxmID = mxmID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Map<String, Integer> getWords() {
        return words;
    }

    public void setWords(Map<String, Integer> words) {
        this.words = words;
    }

    public String toDocument() {
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String, Integer> entry : this.words.entrySet()) {
            for(int i = 0; i < entry.getValue(); i++) {
                result.append(entry.getKey()).append(" ");
            }
        }

        return result.toString();
    }

    @Override
    public String toString() {
        String result =  "MSID: " + this.msID + "\n"
                + "MXMID: " + this.mxmID + "\n"
                + "words: \n";

        for(Map.Entry<String, Integer> term : this.words.entrySet()) {
            result += "\t " + term.getKey() + ": " + term.getValue() + "\n";
        }

        result += "document: " + this.toDocument();

        return result;
    }
}
