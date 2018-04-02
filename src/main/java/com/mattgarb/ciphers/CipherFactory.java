package com.mattgarb.ciphers;

import java.util.HashMap;
import com.mattgarb.Main;

public class CipherFactory {
    private final HashMap<Integer, RotationCipher> rotationCache;
    private final HashMap<String, Vigenere> vigenereCache;
    private final HashMap<String, AutoKey> autoKeyCache;
    private final HashMap<Integer, Route> routeCache;

    public static class Builder {
        public Builder() {
        }

        public CipherFactory build() {
            return new CipherFactory(this);
        }
    }

    private CipherFactory(Builder builder) {
        rotationCache = new HashMap<>();
        vigenereCache = new HashMap<>();
        autoKeyCache = new HashMap<>();
        routeCache = new HashMap<>();
    }

    public RotationCipher createRotation(int rotation) {
        if (!rotationCache.containsKey(rotation)) {
            rotationCache.put(rotation, new RotationCipher.Builder().setRotationKey(rotation).build());
        }
        return rotationCache.get(rotation);
    }
    
    public Vigenere createVigenere(String key) {
        if (!vigenereCache.containsKey(key)) {
            vigenereCache.put(key, new Vigenere.Builder().setKey(key).build());
        }
        return vigenereCache.get(key);
    }

    public AutoKey createAutoKey(String key) {
        if (!autoKeyCache.containsKey(key)) {
            autoKeyCache.put(key, new AutoKey.Builder().setKey(key).build());
        }
        return autoKeyCache.get(key);
    }

    public Route createRoute(Integer columns) {
        if (!routeCache.containsKey(columns)) {
            routeCache.put(columns, new Route.Builder().setColumns(columns).build());
        }
        return routeCache.get(columns);
    }

    public String encrypt(String cipher, String plaintext, int rotation, String key) {
        switch (cipher) {
            case "Rotation":
                RotationCipher rotationCipher = new RotationCipher.Builder().setRotationKey(rotation).build();
                return rotationCipher.encrypt(plaintext);
            case "Vigenere":
                Vigenere vigenere = new Vigenere.Builder().setKey(key).build();
                return vigenere.encrypt(plaintext);
            case "AutoKey":
                AutoKey autoKey = new AutoKey.Builder().setKey(key).build();
                return autoKey.encrypt(plaintext);
            case "Base64":
                return new Base64Cipher().encrypt(plaintext);
            case "Column":
                ColumnTransposition columnTransposition = new ColumnTransposition.Builder()
                        .setColumnOrder(key)
                        .build();
                return columnTransposition.encrypt(plaintext);
            case "Route":
                Route route = new Route.Builder().setColumns(rotation).build();
                return route.encrypt(plaintext);
            default:
                return plaintext;
        }
    }

    public String decrypt(String cipher, String ciphertext, int rotation, String key) {
        switch (cipher) {
            case "Rotation":
                RotationCipher rotationCipher = new RotationCipher.Builder().setRotationKey(rotation).build();
                return rotationCipher.decrypt(ciphertext);
            case "Vigenere":
                Vigenere vigenere = new Vigenere.Builder().setKey(Main.onlyLowerLetters(key)).build();
                return vigenere.decrypt(ciphertext);
            case "AutoKey":
                AutoKey autoKey = new AutoKey.Builder().setKey(Main.onlyLowerLetters(key)).build();
                return autoKey.decrypt(ciphertext);
            case "Base64":
                return new Base64Cipher().decrypt(ciphertext);
            case "Column":
                ColumnTransposition columnTransposition = new ColumnTransposition.Builder()
                        .setColumnOrder(key)
                        .build();
                return columnTransposition.decrypt(ciphertext);
            case "Route":
                Route route = new Route.Builder().setColumns(rotation).build();
                return route.decrypt(ciphertext);
            default:
                return ciphertext;
        }
    }
}
