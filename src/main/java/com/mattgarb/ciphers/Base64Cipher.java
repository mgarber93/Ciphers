package com.mattgarb.ciphers;
import java.util.*;

public class Base64Cipher implements Cipher<String> {
    @Override
    public String encrypt(String plainText) {
        Base64.Encoder encoder = Base64.getMimeEncoder();
        return encoder.encodeToString(plainText.getBytes());
    }

    @Override
    public String decrypt(String cipherText) {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        try {
            byte[] decoded = decoder.decode(cipherText.getBytes());
            return new String(decoded,"UTF-8");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
