package com.mattgarb.ciphers;

public enum CipherType {
    ROTATION,
    VIGENERE,
    AUTOKEY,
    BASE64,
    COLUMN,
    ROUTE;

    @Override
    public String toString() {
        switch(this) {
            case ROTATION:
                return "Rotation";
            case VIGENERE:
                return "Vigenere";
            case AUTOKEY:
                return "AutoKey";
            case BASE64:
                return "Base64";
            case COLUMN:
                return "ColumnTransposition";
            case ROUTE:
                return "Route";
            default: throw new IllegalArgumentException();
        }
    }
}
