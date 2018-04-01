package com.mattgarb;

public class RotationCipher implements Cipher<String> {
    private final Integer rotationKey;

    public static class Builder {
        private Integer rotationKey;

        public Builder setRotationKey(Integer rotationKey) {
            this.rotationKey = rotationKey;
            return this;
        }

        public RotationCipher build() {
            return new RotationCipher(this);
        }
    }

    RotationCipher(Builder builder) {
        this.rotationKey = builder.rotationKey;
    }

    String rotate(final String plainText, Integer rotation) {
        StringBuilder sb = new StringBuilder(plainText.length());
        plainText.chars().parallel().map(e -> {
            if(e >= 65 && e <= 90){
                return (e - 65 + rotation) % 26 + 65;
            } else if(e >= 97 && e <= 122)  {
                return (e - 97 + rotation) % 26 + 97;
            } else {
                return e;
            }
        }).forEachOrdered(e -> sb.append((char) e));
        return sb.toString();
    }

    @Override
    public String encrypt(String plainText) {
        return rotate(plainText, this.rotationKey);
    }

    @Override
    public String decrypt(String cipherText) {
        return rotate(cipherText, 26 - this.rotationKey);
    }
}
