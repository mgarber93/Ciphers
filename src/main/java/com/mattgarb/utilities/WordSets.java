package com.mattgarb.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Arrays;

public class WordSets {
    private final HashSet<String> wordSet = new HashSet<>();
    private final HashSet<String> passSet = new HashSet<>();

    public WordSets() {
        new Thread(new ImportSets()).start();
    }

    public HashSet<String> getPasswordSet() {
        return this.passSet;
    }

    public HashSet<String> getWordSet() {
        return this.wordSet;
    }

    public class ImportSets implements Runnable {
        private BufferedReader createBufferedReaderFromPath(String path) {
            return new BufferedReader(new InputStreamReader(ImportSets.class.getResourceAsStream(path)));
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
                System.out.println("bad path! ");
            }
            passSet.add("");
            System.out.println("Passwords added: " + passSet.size());
        }
    }
}

