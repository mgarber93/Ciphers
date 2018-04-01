package com.mattgarb.ciphers;

/**
 * Created by Matt on 5/14/2017.
 * Simple Route Transposition Cipher.
 */
public class Route implements Cipher<String> {
    private final int columns;

    public static class Builder {
        private int columns;

        public Builder() {
        }

        public Builder setColumns(int columns) {
            this.columns = columns;
            return this;
        }

        public Route build() {
            return new Route(this);
        }
    }

    private Route(Builder builder) {
        this.columns = builder.columns;
    }

    @Override
    public String encrypt(String plain) {
        String cleanText = plain.toUpperCase().replaceAll("[^A-Z]","");
        Pad pad = new Pad(cleanText, columns);
        return cipherFormat(pad.toString());
    }

    @Override
    public String decrypt(String cipher) {
        String cleanText = cipher.toUpperCase().replaceAll("[^A-Z]","");
        ReversePad pad = new ReversePad(cleanText, columns);
        return cipherFormat(pad.toString());
    }

    public static String cipherFormat(String str) {
        if(str.length() == 0) return str;
        StringBuilder sb = new StringBuilder(str.length()+str.length()/5);
        sb.append(str.charAt(0));
        int index = 1;
        while(index < str.length()){
            if(index%5==0) sb.append(" ").append(str.charAt(index++));
            else sb.append(str.charAt(index++));
        }
        return sb.toString();
    }

    static class Pad{
        char[][] pad;

        /**
         * Pad constructor is used for encrypt
         * Pad = [cols][rows]
         * @param text that is encrypted or decrypted
         * @param numberOfColumns is the ordering of the columns to put text into.
         */
        Pad(String text, Integer numberOfColumns) {
            this.pad = new char[numberOfColumns][(int)Math.ceil((double)text.length()/numberOfColumns)];
            for(int i = 0; i < text.length(); i++) {
                this.pad[i%numberOfColumns][(int)Math.floor(i/numberOfColumns)] = text.charAt(i);
            }
            char paddingChar = 'X';
            for(int i = text.length(); i < this.pad.length* this.pad[0].length; i++) {
                this.pad[i % numberOfColumns][(int)Math.floor((double)i / numberOfColumns)] = paddingChar;
            }
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder(this.pad.length * this.pad[0].length);
            for (char[] column : this.pad) {
                for (char character : column) {
                    if (character != '\u0000') out.append(character);
                }
            }
            return out.toString();
        }
    }
    static class ReversePad{
        char[][] reversePad;

        ReversePad(String text, Integer numberOfColumns) {
            this.reversePad = new char[numberOfColumns][(int) Math.ceil((double)text.length()/numberOfColumns)];
            int index = 0;
            for(int i = 0; i < numberOfColumns; i++) {
                for(int j = 0; j < (int) Math.ceil((double)text.length()/numberOfColumns); j++) {
                    if(index < text.length())this.reversePad[i][j] = text.charAt(index++);
                    else break;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder(this.reversePad.length * this.reversePad[0].length);
            for(int i = 0; i < this.reversePad[0].length; i++) {
                for (char[] column : reversePad) {
                    if (column[i] != '\u0000') out.append(column[i]);
                }
            }
            return out.toString();
        }
    }
}
