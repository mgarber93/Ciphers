package com.mattgarb.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Arrays;

public class WordSets {
    private HashSet<String> wordSet = new HashSet<>();
    private HashSet<String> passSet = new HashSet<>();
    private boolean loaded = false;

    public WordSets(Builder builder) {
    }

    public HashSet<String> getPasswordSet() {
        return this.passSet;
    }

    void setPasswordSet(HashSet<String> passSet) {
        this.passSet = passSet;
    }

    public HashSet<String> getWordSet() {
        return this.wordSet;
    }

    void setWordSet(HashSet<String> wordSet) {
        this.wordSet = wordSet;
    }

    synchronized boolean isLoaded() {
        return this.loaded;
    }

    synchronized void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public static class Builder {
        public Builder() {
        }

        public WordSets build() {
            WordSets wordsets = new WordSets(this);
            Loader loader = new Loader(wordsets);
            new Thread(loader).start();
            return wordsets;
        }

        public synchronized WordSets guardedBuild() {
            WordSets wordSets = build();
            while(!wordSets.isLoaded()) {
                try {
                    wait(2000);
                } catch (InterruptedException e) {}
            }
            return wordSets;
        }
    }
}

class Loader implements Runnable {
    private WordSets wordSets;
    private final HashSet<String> wordSet = new HashSet<>();
    private final HashSet<String> passSet = new HashSet<>();
    private boolean done = false;

    Loader(WordSets wordSets) {
        this.wordSets = wordSets;
    }

    private BufferedReader createBufferedReaderFromPath(String path) {
        return new BufferedReader(new InputStreamReader(WordSets.class.getResourceAsStream(path)));
    }

    private synchronized void update() {
        wordSets.setWordSet(wordSet);
        wordSets.setPasswordSet(passSet);
        wordSets.setLoaded(true);
        notifyAll();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        passSet.add("");
        System.out.println("Passwords added: " + passSet.size());
        update();
    }
}

