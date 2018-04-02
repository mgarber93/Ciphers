package com.mattgarb.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Arrays;

public class WordSets implements Runnable {
    private final HashSet<String> wordSet = new HashSet<>();
    private final HashSet<String> passSet = new HashSet<>();
    private HandlesPromise<WordSets> callback = null;

    public WordSets(Builder builder) {
        this.callback = builder.callback;
    }

    public HashSet<String> getPasswordSet() {
        return this.passSet;
    }

    public HashSet<String> getWordSet() {
        return this.wordSet;
    }

    @Override
    public void run() {
        try(BufferedReader reader = createBufferedReaderFromPath("../../../wordlist/Dictionary.txt")){
            while (reader.ready()) {
                String[] words = reader.readLine()
                        .replaceAll("[^a-zA-Z]", " ")
                        .toLowerCase()
                        .split(" ");
                for (String word : words) {
                    if (word.length() > 3) {
                        wordSet.add(word);
                    }
                }
            }
        } catch (IOException e) {
            if (callback != null) {
                callback.handleReject(e);
                return;
            } else {
                e.printStackTrace();
            }
        }
        System.out.println("Words added: " + wordSet.size());

        try(BufferedReader reader = createBufferedReaderFromPath("../../../wordlist/worstpasswords.txt")){
            while (reader.ready()) {
                String[] words = reader.readLine()
                        .replaceAll("[^a-zA-Z]", " ")
                        .toLowerCase()
                        .split(" ");
                passSet.addAll(Arrays.asList(words));
            }
        } catch (IOException e) {
            if (callback != null) {
                callback.handleReject(e);
                return;
            } else {
                e.printStackTrace();
            }
        }
        passSet.add("");
        System.out.println("Passwords added: " + passSet.size());

        if (callback != null) {
            callback.handleResolve(this);
        }

    }

    private BufferedReader createBufferedReaderFromPath(String path) {
        return new BufferedReader(new InputStreamReader(WordSets.class.getResourceAsStream(path)));
    }

    public static class Builder {
        HandlesPromise<WordSets> callback = null;

        public Builder() {
        }

        public Builder setCallback(HandlesPromise<WordSets> callback) {
            this.callback = callback;
            return this;
        }

        public WordSets build() {
            WordSets wordsets = new WordSets(this);
            new Thread(wordsets).start();
            return wordsets;
        }
    }
}

