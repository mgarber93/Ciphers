package com.mattgarb;

public interface Cipher<T> {
    T encrypt(T plainText);
    T decrypt(T cipherText);
}
