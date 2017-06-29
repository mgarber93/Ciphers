package com.mattgarb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main extends Application {

    private static HashSet<String> wordSet = new HashSet<>();
    static HashSet<String> passSet = new HashSet<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        new Thread(new ImportSets()).start();
        Parent root = FXMLLoader.load(getClass().getResource("../../view/MainView.fxml"));
        primaryStage.setTitle("Ciphers");
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String rotate(final String plainText, final Integer Rotation) {
        StringBuilder sb = new StringBuilder(plainText.length());
        plainText.chars().parallel().map(e -> {
            if(e >= 65 && e <= 90){
                return (e - 65 + Rotation) % 26 + 65;
            } else if(e >= 97 && e <= 122)  {
                return (e - 97 + Rotation) % 26 + 97;
            } else {
                return e;
            }
        }).forEachOrdered(e -> sb.append((char)e));
        return sb.toString();
    }

    public static String base64(String plainText) {
        Base64.Encoder encoder = Base64.getMimeEncoder();
        return encoder.encodeToString(plainText.getBytes());
    }

    public static String base64Decode(String cipherText) {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        try {
            byte[] decoded = decoder.decode(cipherText.getBytes());
            return new String(decoded,"UTF-8");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * parallelBruteForce creates a Map of each password to an array of integer scores.
     * Returns the cipher mode/password corresponding to the greatest scoring integer.
     * @param strText this is the string to decrypt
     * @return 'Cipher mode: password or rotation' that scores the best.
     */
    public static String parallelBruteForce(String strText) {
        strText = strText.toLowerCase().replaceAll("[^a-z ]"," ");
        String str = strText.length() > 500 ? strText.substring(0,500) : strText;

        Map<String,Integer[]> cipherMap = new HashMap<>();
        Integer[] rotationScores = new Integer[26];
        for(int i = 0; i < 26; i++) {
            rotationScores[i] = testFast(rotate(str,26 - i));
        }
        passSet.forEach(e -> {
            Integer[] scores = new Integer[2];
            scores[0] = testFast(Vigenere.decrypt(str, e));
            scores[1] = testFast(AutoKey.decrypt(str, e));
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
        for (int i = 0; i < result.getValue().length; i++) {
            if(result.getValue()[i] > high) {
                index = 26 + i;
                high = result.getValue()[i];
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
    private static int testFast(final String str) {
        String[] args = str.split(" ");
        final int[] out = {-args.length};
        Arrays.stream(args).parallel().forEach(e -> {
            if(wordSet.contains(e)) out[0] += 1;
        });
        return out[0];
    }

    public static String onlyLowerLetters(final String strText) {
        return strText.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    /**
     * ImportSets populates the TreeSets from the txt files in com.mattgarb/resources in a different thread.
     * Creating high quality collections of words, which are tried as a psk and for plain-text detection, is a
     * challenge. The best solution I found has been to throw out all words smaller than 3 chars.
     * Takes about 8s on my machine.
     */
    public static class ImportSets implements Runnable{
        @Override
        public void run() {
            try(BufferedReader reader = new BufferedReader( new InputStreamReader( getClass()
                    .getResourceAsStream("resources/Dictionary.txt")))){
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
            try(BufferedReader reader = new BufferedReader( new InputStreamReader( getClass()
                    .getResourceAsStream("resources/Dictionary.txt")))){
                while (reader.ready()) {
                    String[] words = reader.readLine()
                            .replaceAll("[^a-zA-Z]", " ")
                            .toLowerCase()
                            .split(" ");
                    for(String line : words) {
                        if(line.length()>= 3){
                            passSet.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("bad path! ");
            }
            try(BufferedReader reader = new BufferedReader( new InputStreamReader( getClass()
                    .getResourceAsStream("resources/worstpasswords.txt")))){
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
