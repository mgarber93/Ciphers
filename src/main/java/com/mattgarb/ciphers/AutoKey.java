package com.mattgarb.ciphers;

import com.mattgarb.Main;

/**
 * Created by mattg on 5/11/2017.
 * AutoKey polyalphabetic cipher. Arguably more secure than Vigenere.
 */
public class AutoKey implements Cipher<String> {
    private final String key;

    public static class Builder {
        private String key;

        public Builder() {
        }

        public Builder setKey(String key) {
            this.key = Main.onlyLowerLetters(key);
            return this;
        }

        public AutoKey build() {
            return new AutoKey(this);
        }
    }

    AutoKey(Builder builder) {
        this.key = builder.key;
    }

    @Override
    public String encrypt(final String str) {
        if(!"".equals(key)) {
            Vigenere vigenere = new Vigenere.Builder().setKey(key + str).build();
            return vigenere.encrypt(str);
        }
        //special mapping to make autoKey bijective
        StringBuilder sb = new StringBuilder(str.length());
        str.chars().map(e -> {
            int rotation = e >= 97 ? e - 97 : e - 65;
            rotation += ((e >= 110 && e <= 122) || (e >= 78 && e <= 90)) ? 1 : 0;
            if(e >= 65 && e <= 90){
                return (e - 65 + rotation) % 26 + 65;
            } else if(e >= 97 && e <= 122)  {
                return (e - 97 + rotation) % 26 + 97;
            }
            return e;
        }).forEachOrdered(e -> sb.append((char)e));
        return sb.toString();
    }

    /**
     * If no Psk is provided, autoKey is still a bijective mapping
     * A-M maps to A - Y (even letters)
     * N-Z maps to B - Z (odd letters)
     *
     * Inverse functions must clean their psk before passing it in.
     * @param text cipher text to decrypt
     * @return plain text
     */
    @Override
    public String decrypt(final String text) {
        if(text.length() == 0) return "";
        StringBuilder out = new StringBuilder(text.length());

        //special mapping to make autoKey bijective
        if(key.length() == 0) {
            text.chars().map(e -> {
                int letter;
                if(e >= 65 && e <= 90){
                    if( (e - 65) % 2 == 0) letter = (e - 65) / 2 + 65;
                    else letter = (e - 65) / 2 + 65 + 13;
                } else if(e >= 97 && e <= 122)  {
                    if( (e - 97) % 2 == 0) letter = (e - 97) / 2 + 97;
                    else letter = (e - 97) / 2 + 97 + 13;
                } else {
                    letter = e;
                }
                return letter;
            }).forEachOrdered(e -> out.append((char)e));
            return out.toString();
        }

        StringBuilder psk = new StringBuilder(key);
        final int[] index = {0};
        final int[] slip = {0};

        text.chars().map(e -> {
            int letter;
            //if we are past the psk length we grab the next letter from the cipherText as the rotation
            int rotation;
            if (index[0] - slip[0] < psk.length()) rotation = psk.charAt(index[0] - slip[0]) - 97;
            else rotation = (e >= 97 ? e - 97 : e - 65) / 2;
            if(e >= 65 && e <= 90){
                letter = (e - 65 + (26 - rotation )) % 26 + 65;
                psk.append((char)(letter + 32));
            } else if(e >= 97 && e <= 122)  {
                letter = (e - 97 + (26 - rotation)) % 26 + 97;
                psk.append((char)letter);
            } else {
                slip[0]++;
                letter = e;
            }
            index[0]++;
            return letter;
        }).forEachOrdered(e -> out.append((char)e));
        return out.toString();
    }
}
