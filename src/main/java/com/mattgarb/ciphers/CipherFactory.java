package com.mattgarb.ciphers;

import java.util.HashMap;

public class CipherFactory {
    private final HashMap<Integer, RotationCipher> rotationCache;
    private final HashMap<String, Vigenere> vigenereCache;
    private final HashMap<String, AutoKey> autoKeyCache;

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
}
