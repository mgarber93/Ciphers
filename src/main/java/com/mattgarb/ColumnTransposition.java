package com.mattgarb;


import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by mattg on 5/11/2017.
 * Column transposition cipher is a transposition cipher.
 */
public abstract class ColumnTransposition {

    public static String encrypt(final String plainText, final String psk) {
        if(plainText.length() == 0 || psk.length() == 0) return plainText;
        ArrayList<Integer> columnOrder = new ArrayList<>();
        psk.toUpperCase().replaceAll("[^A-Z]","")
                .chars()
                .map(e -> (e - 65))
                .forEachOrdered(columnOrder::add);
        Integer[] cols = columnOrder.toArray(new Integer[columnOrder.size()]);
        return encrypt(plainText, cols );
    }

    static String decrypt(final String plainText, final String psk) {
        if(plainText.length() == 0 || psk.length() == 0) return plainText;
        ArrayList<Integer> columnOrder = new ArrayList<>();
        psk.toUpperCase().replaceAll("[^A-Z]","")
                .chars()
                .map(e -> (e - 65))
                .forEachOrdered(columnOrder::add);
        Integer[] cols = columnOrder.toArray(new Integer[columnOrder.size()]);
        return decrypt(plainText, cols );
    }

    /**
     * TODO more efficient sorted indexs?
     * @param columnOrders Integer[] to sort
     * @return Sorted Integer[].
     */
    private static Integer[] sortedIndexs(Integer[] columnOrders) {
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
     * @param columnOrders the Pre Shared Key must be less than 26, left associative.
     * @return cipthertext.
     */
    public static String encrypt(final String plainText, final Integer[] columnOrders) {
        if(columnOrders.length == 0 || plainText.length() == 0) return plainText;
        String cleanText = plainText.toUpperCase().replaceAll("[^A-Z]","");
        Integer[] indexs = sortedIndexs(columnOrders);
        Pad pad = new Pad(cleanText,indexs.length);
        pad.encrypt(indexs);
        return Route.cipherFormat(pad.toString());
    }

    public static String decrypt(String cipherText, Integer[] columnOrders) {
        if(columnOrders.length == 0 || cipherText.length() == 0) return cipherText;
        String cleanText = cipherText.toUpperCase().replaceAll("[^A-Z]","");
        Integer[] indexs = reverse(sortedIndexs(columnOrders));
        ReversePad pad = new ReversePad(cleanText, indexs.length);
        pad.decrypt(indexs);
        return Route.cipherFormat(pad.toString());
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
        /**
         * TODO Fix Column out of order bug.
         * @param columnOrders order to decrypt by.
         */
        void decrypt(Integer[] columnOrders) {
            System.out.println(this.toString());
            System.out.println(Arrays.toString(columnOrders));
            char[][] swapPad = new char[this.reversePad.length][this.reversePad[0].length];
            for(int i = 0; i < reversePad.length; i++) swapPad[i] = this.reversePad[columnOrders[i]];
            this.reversePad = swapPad;
            System.out.println(this.toString());
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
