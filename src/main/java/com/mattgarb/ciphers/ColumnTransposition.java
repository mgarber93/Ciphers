package com.mattgarb.ciphers;

import java.util.ArrayList;

/**
 * Created by Matt Garber on 5/11/2017.
 * Column transposition cipher is a transposition cipher.
 */
public class ColumnTransposition implements Cipher<String> {
    private final Integer[] columnOrders;

    public static class Builder {
        private Integer[] columnOrders;

        public Builder() {
        }

        public Builder setColumnOrder(final String psk) {
            ArrayList<Integer> columnOrder = new ArrayList<>();
            psk.toUpperCase().replaceAll("[^A-Z]","")
                    .chars()
                    .map(e -> (e - 65))
                    .forEachOrdered(columnOrder::add);
            this.columnOrders = columnOrder.toArray(new Integer[columnOrder.size()]);
            return this;
        }

        public Builder setColumnOrder(final Integer[] columnOrders) {
            this.columnOrders = columnOrders;
            return this;
        }

        public ColumnTransposition build() {
            return new ColumnTransposition(this);
        }
    }

    ColumnTransposition(Builder builder) {
        this.columnOrders = toSortedIndices(builder.columnOrders);
    }

    /**
     * TODO more efficient sorted indexs?
     * Return the index location for the sorted array.
     * Array must be [0,26)
     * @param columnOrders Integer[] to sort
     * @return Sorted Integer[].
     */
    private static Integer[] toSortedIndices(Integer[] columnOrders) {
        ArrayList<Integer> indexs = new ArrayList<>();
        for(int i = 0; i < 26; i++ ) {
            for(int j = 0 ; j < columnOrders.length; j++) {
                if(columnOrders[j] == i) {
                    indexs.add(j);
                }
                if(indexs.size() == columnOrders.length) break;
            }
            if(indexs.size() == columnOrders.length) break;
        }
        return indexs.toArray(new Integer[indexs.size()]);
    }

    private static Integer[] reverse(Integer[] integers) {
        Integer[] out = new Integer[integers.length];
        for(int i = 0; i < integers.length; i++) {
            out[i] = integers[integers.length-1-i];
        }
        return out;
    }

    /**
     * A column transposition cipher shuffles the ordering of characters based by splitting the plain text into columns
     * and concat-ing the columns in the order given by columnOrders (which can also be a word)
     *
     * @param plainText to encrypt.
     * @return cipthertext.
     */
    public String encrypt(final String plainText) {
        if(columnOrders.length == 0 || plainText.length() == 0) return plainText;
        String cleanText = plainText.toUpperCase().replaceAll("[^A-Z]","");
        Pad pad = new Pad(cleanText, columnOrders.length);
        pad.encrypt(columnOrders);
        return Route.cipherFormat(pad.toString());
    }

    public String decrypt(String cipherText) {
        String cleanText = cipherText.toUpperCase().replaceAll("[^A-Z]","");
        if(columnOrders.length == 0 || cleanText.length() == 0) return cipherText;
        Integer[] reversedIndices = toSortedIndices(columnOrders);
        ReversePad pad = new ReversePad(cleanText, reversedIndices.length);
        pad.decrypt(reversedIndices);
        return Route.cipherFormat(pad.toString());
    }

    static class Pad {
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
            final char paddingChar = 'X';
            for(int i = text.length(); i < this.pad.length* this.pad[0].length; i++) {
                this.pad[i % numberOfColumns][(int)Math.floor((double)i / numberOfColumns)] = paddingChar;
            }
        }
        /**
         * encrypt mutates the pad by rearranging columns by ordering given.
         * @param columnOrders to mutate pad by.
         */
        void encrypt(Integer[] columnOrders) {
            char[][] pad = new char[this.pad.length][this.pad[0].length];
            for(int i = 0; i < pad.length; i++) pad[i] = this.pad[columnOrders[i]];
            this.pad = pad;
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

    static class ReversePad {
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
        /**
         * Mutates this reverse pad.
         * @param columnOrders order to decrypt by.
         */
        void decrypt(final Integer[] columnOrders) {
            char[][] swapPad = new char[this.reversePad.length][this.reversePad[0].length];
            for(int i = 0; i < reversePad.length; i++) swapPad[i] = this.reversePad[columnOrders[i]];
            this.reversePad = swapPad;
        }
        @Override
        public String toString() {
            StringBuilder out = new StringBuilder(this.reversePad.length * this.reversePad[0].length);
            for(int i = 0; i < this.reversePad[0].length; i++) {
                for (char[] column : reversePad) {
                    if (column[i] != '\u0000') out.append(column[i]); // if not null char
                }
            }
            return out.toString();
        }
    }
}
