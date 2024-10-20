package org.cwinteractive.msdutils;

public class HashingDictionary implements Dictionary {

    private int numTerms;

    public HashingDictionary() {
        this(new Double(Math.pow(2, 20)).intValue());
    }

    public HashingDictionary(int numTerms) {
        this.numTerms = numTerms;
    }

    @Override
    public Integer getTermIndex(String term) {
        return Math.floorMod(term.hashCode(), numTerms);
    }

    @Override
    public int getNumTerms() {
        return numTerms;
    }
}
