package com.mattgarb.ciphers;

/**
 * Created by mattg on 5/11/2017.
 * Vigenere ciphers are a simple polyalphabetic substitution cipher.
 */
public class Vigenere implements Cipher<String> {
    final private String key;

    public static class Builder {
        private String key;

        public Builder() {
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Vigenere build() {
            return new Vigenere(this);
        }
    }

    private Vigenere(Builder builder) {
        this.key = builder.key;
    }

    /**
     * @param text is the plain text.
     * @return the cipher text.
     */
    @Override
    public String encrypt(final String text) {
        String psk = key.toLowerCase().replaceAll("[^a-z]","");
        if(text.length()==0||psk.length()==0) return text;
        StringBuilder sb = new StringBuilder(text.length());
        final int[] index = {0};
        final int[] slip = {0};
        text.chars().map(e -> {
            int letter;
            int rotation = (int) psk.charAt((index[0] - slip[0])%psk.length()) - 97;
            if(e >= 65 && e <= 90){
                letter = (e - 65 + rotation) % 26 + 65;
            } else if(e >= 97 && e <= 122)  {
                letter = (e - 97 + rotation) % 26 + 97;
            } else {
                slip[0]++;
                letter = e;
            }
            index[0]++;
            return letter;
        }).forEachOrdered(e -> sb.append((char)e));
        return sb.toString();
    }

    /**
     * VigenereInv maps a vigenere encrypted cipher text back to its plain text.
     * Inverse functions must clean their psk before passing it in.
     * @param text cipher text to lower case and replace All
     * @return the plain text
     */
    @Override
    public String decrypt(final String text) {
        if(text.length()==0 || key.length()==0) return text;
        StringBuilder sb = new StringBuilder(text.length());
        final int[] index = {0};
        final int[] slip = {0};
        text.chars().map(e -> {
            int letter;
            int rotation = (int) key.charAt((index[0] - slip[0]) % key.length()) - 97;
            if(e >= 65 && e <= 90){
                letter = (e - 65 + (26 - rotation )) % 26 + 65;
            } else if(e >= 97 && e <= 122)  {
                letter = (e - 97 + (26 - rotation)) % 26 + 97;
            } else {
                slip[0]++;
                letter = e;
            }
            index[0]++;
            return letter;
        }).forEachOrdered(e -> sb.append((char)e));
        return sb.toString();
    }
}
