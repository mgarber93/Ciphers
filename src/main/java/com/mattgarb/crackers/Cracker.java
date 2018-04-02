package com.mattgarb.crackers;

import com.mattgarb.ciphers.*;
import com.mattgarb.utilities.WordSets;

import java.util.*;
import java.util.stream.IntStream;

public class Cracker {
    private final WordSets wordSets;
    private final CipherFactory cipherStore;

    public static class Builder {
        private WordSets wordSets;
        private CipherFactory cipherStore;

        public Builder() {
        }

        public Builder setWordSets(WordSets wordSets) {
            this.wordSets = wordSets;
            return this;
        }

        public Builder setCipherFactory(CipherFactory cipherFactory) {
            this.cipherStore = cipherFactory;
            return this;
        }

        public Cracker build() {
            return new Cracker(this);
        }
    }

    private Cracker(Builder builder) {
        this.wordSets = builder.wordSets;
        this.cipherStore = builder.cipherStore;
    }

    /**
     * parallelBruteForce creates a Map of each password to an array of integer scores.
     * Returns the cipher mode/password corresponding to the greatest scoring integer.
     * @param strText this is the string to decrypt
     * @return 'Cipher mode: password or rotation' that scores the best.
     */
    public String parallelBruteForce(String strText) {
        strText = strText.toLowerCase().replaceAll("[^a-z ]"," ");
        String str = strText.length() > 500 ? strText.substring(0,500) : strText;

        Map<String,Integer[]> cipherMap = new HashMap<>();

        int[] rotationScores = IntStream.range(0, 26)
                .mapToObj(cipherStore::createRotation)
                .mapToInt(rotationCipher -> testFast(rotationCipher.decrypt(str)))
                .toArray();

        wordSets.getPasswordSet().forEach(e -> {
            Integer[] scores = new Integer[2];
            Vigenere vigenere = new Vigenere.Builder().setKey(e).build();
            AutoKey autoKey = new AutoKey.Builder().setKey(e).build();
            scores[0] = testFast(vigenere.decrypt(str));
            scores[1] = testFast(autoKey.decrypt(str));
            //scores[2] = testFast(ColumnTransposition.decrypt(str, e));
            cipherMap.put(e, scores);
        });

        Map.Entry<String, Integer[]> result = cipherMap.entrySet()
                .parallelStream()
                .max((entry1, entry2) -> {
                    int entry1Max = 0;
                    int entry2Max = 0;
                    for(int i = 0; i < entry1.getValue().length; i++) {
                        if(i == 0 || entry1Max < entry1.getValue()[i]) entry1Max = entry1.getValue()[i];
                    }
                    for(int i = 0; i < entry2.getValue().length; i++) {
                        if(i == 0 || entry2Max < entry2.getValue()[i]) entry2Max = entry2.getValue()[i];
                    }
                    return entry1Max > entry2Max ? 1 : -1;
                }).orElse(null);

        int index = 0;
        int high = 0;
        for(int i = 0; i < rotationScores.length; i++) {
            if(i == 0 || high < rotationScores[i]) {
                index = i;
                high = rotationScores[i];
            }
        }

        if (result != null) {
            for (int i = 0; i < result.getValue().length; i++) {
                if(result.getValue()[i] > high) {
                    index = 26 + i;
                    high = result.getValue()[i];
                }
            }
        }

        return index == 26 ? "Vigenere: " + result.getKey() : index == 27 ? "AutoKey: " + result.getKey()
                : index == 28 ? "Column: " + result.getKey() : "Rotation: " + index;
    }

    /**
     * Test a resulting plaintext string array against TreeSet wordSet to see if we have a real decrypt.
     * Runs in Theta(n) where n is number of words in body.
     * @param str plaintext string
     * @return out int
     */
    private int testFast(final String str) {
        String[] args = str.split(" ");
        final Integer [] out = {-args.length};
        Arrays.stream(args).parallel().forEach(e -> {
            if(wordSets.getWordSet().contains(e)) out[0] += 1;
        });
        return out[0];
    }
}
