import com.mattgarb.ciphers.*;
import com.mattgarb.crackers.Cracker;
import com.mattgarb.utilities.WordSets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by m on 6/29/17.
 * JUnit 4.11 test coverage.
 */
public class Tests {
    private CipherFactory cipherStore = new CipherFactory.Builder().build();

    @Test
    public void testRotationCipher() {
        String str13 = "Uryyb Jbeyq guvf vf na rknzcyr bs ebg 13.";
        String str14 = "Vszzc Kcfzr hvwg wg ob sloadzs ct fch 14.";
        String str20 = "[]uxezdb a kyLKLauixzed !#$!# XZUAUBL UEMXZA lin 20";

        Rotation rotation = cipherStore.createRotation(13);
        Rotation rotation2 = cipherStore.createRotation(0);
        Rotation rotation3 = cipherStore.createRotation(26);
        Rotation rotation14 = cipherStore.createRotation(14);
        Rotation rotation20 = cipherStore.createRotation(20);

        assertEquals("", rotation.encrypt(""));
        assertEquals(str13, rotation2.encrypt(str13));
        assertEquals(str13, rotation3.encrypt(str13));
        assertEquals(str13, rotation.encrypt("Hello World this is an example of rot 13."));
        assertEquals(str14, rotation14.encrypt("Hello World this is an example of rot 14."));
        assertEquals(str20, rotation20.encrypt("[]adkfjh g qeRQRgaodfkj !#$!# DFAGAHR AKSDFG rot 20"));
    }

    //https://en.wikipedia.org/wiki/Base64
    @Test
    public void testBase64() {
        String enc1 = "any carnal pleasure.";
        String dec1 = "YW55IGNhcm5hbCBwbGVhc3VyZS4=";
        String enc2 = "any carnal pleasure";
        String dec2 = "YW55IGNhcm5hbCBwbGVhc3VyZQ==";
        String enc3 = "any carnal pleasur";
        String dec3 = "YW55IGNhcm5hbCBwbGVhc3Vy";
        String enc4 = "any carnal pleasu";
        String dec4 = "YW55IGNhcm5hbCBwbGVhc3U=";
        String enc5 = "any carnal pleas";
        String dec5 = "YW55IGNhcm5hbCBwbGVhcw==";

        Base64Cipher base64Cipher = new Base64Cipher();

        assertEquals(dec1, base64Cipher.encrypt(enc1));
        assertEquals(enc1, base64Cipher.decrypt(dec1));
        assertEquals(dec2, base64Cipher.encrypt(enc2));
        assertEquals(dec3, base64Cipher.encrypt(enc3));
        assertEquals(dec4, base64Cipher.encrypt(enc4));
        assertEquals(dec5, base64Cipher.encrypt(enc5));
    }

    @Test
    public void testRoute() {
        String plain = "abort the mission, you have been spotted";
        String invCipher = "ABORT THEMI SSION YOUHA VEBEE NSPOT TEDXX";
        String cipher = "ATSYV NTBHS OESEO EIUBP DRMOH EOXTI NAETX";
        Route route5 = cipherStore.createRoute(5);
        assertEquals(cipher, route5.encrypt(plain));
        assertEquals(invCipher, route5.decrypt(cipher));
    }

    @Test
    public void testColumnTranspotions() {
        String plain = "WHICH WRIST WATCH ESARE SWISS WRIST WATCH ESXXX";
        String cipher ="HTHES THXHR ASWRA SCSCR SSCXW WWESW WEIIT AIITX";
        ColumnTransposition columnTranspositionText = new ColumnTransposition.Builder()
                .setColumnOrder("dbECA")
                .build();
        ColumnTransposition columnTranspositionArray = new ColumnTransposition.Builder()
                .setColumnOrder(new Integer[]{4, 2, 5, 3, 1})
                .build();
        assertEquals("preshared keyword", cipher, columnTranspositionText.encrypt(plain));
        assertEquals("encrypt by array", cipher, columnTranspositionArray.encrypt(plain));
        assertEquals("decrypt by array", plain, columnTranspositionArray.decrypt(cipher));
    }

    @Test
    public void testVigenere() {
        String out1 = "Ihw kpoi qpmwv Owiljs, sdoc bqdwf so hyh Soy Kpoi, lh bjacvk dcd jaosj kxgz aj hyh hkq aj " +
                "zrwt Amyqgk. Wwik oasb ru tzw dckwtsl oaokktr af pvv vjmewn wj qpmwv pvv Gdg Vsug riiej lds jwpr.";
        String in1 = "The star named Sirius, also known as the Dog Star, is bright and rises high in the sky in" +
                " late August. This week of the hottest weather in the summer is named the Dog Days after the star.";
        String out2 = "Alp dhhv ylalh Dtfpyd, lzzs vycdr ld hoi Ozu Zxlc, wz fctuox lyr ymdpg omrs wu xsp grc ty " +
                "zhxp Linyde. Homd hslo zq hoi szhaide kleessy my evl wfxalv td bhqpo hoi Ozu Kejd omxpc hoi deoy.";

        Vigenere vigenere1 = cipherStore.createVigenere("PASSword");
        Vigenere vigenere2 = cipherStore.createVigenere("heLLo ");

        //psk case insensitive
        assertEquals(out1, vigenere1.encrypt(in1));
        assertEquals(out2, vigenere2.encrypt(in1));
    }

    @Test
    public void testAutoKey() {
        String example1 = "Ihw kpoi qttiv Liivue, eokw bvion lk hrr Rkt Slty, mv pxazhk ife iqyll hvjy " +
                "qf xzl aqf qa ehxw Ksohdt. Mlim cywd hm bza lsdhjla alomaij bj xhx zydurk pw fuyqh kpw Qos Hdrz " +
                "eihku tfw sytv.";
        String plainText = "The star named Sirius, also known as the Dog Star, is bright and rises high in the sky in" +
                " late August. This week of the hottest weather in the summer is named the Dog Days after the star.";

        String blankMapIn =  "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String blankMapOut = "A C E G I K M O Q S U W Y B D F H J L N P R T V X Z";

        AutoKey autoKey1 = cipherStore.createAutoKey(" paSSword ");
        assertEquals(autoKey1.encrypt(plainText), example1);
        assertEquals(plainText, autoKey1.decrypt(example1));

        AutoKey autoKeyBlank = cipherStore.createAutoKey("");
        assertEquals(blankMapOut, autoKeyBlank.encrypt(blankMapIn));
        assertEquals(blankMapOut.toLowerCase(), autoKeyBlank.encrypt(blankMapIn.toLowerCase()));
        assertEquals(blankMapOut, blankMapIn, autoKeyBlank.decrypt(blankMapOut));
        assertEquals(
                blankMapOut.toLowerCase(),
                blankMapIn.toLowerCase(),
                autoKeyBlank.decrypt(blankMapOut.toLowerCase())
        );
    }

    Cracker setUpCracker() {
        //set up passSet and wordSet
        WordSets wordSets = new WordSets.Builder().guardedBuild();
        return new Cracker.Builder().setWordSets(wordSets).setCipherFactory(cipherStore).build();
    }

    @Test
    public void testBruteForce() {
        Cracker cracker = setUpCracker();

        String cipherText = "Alp dhhv ylalh Dtfpyd, lzzs vycdr ld hoi Ozu Zxlc, wz fctuox lyr ymdpg omrs wu xsp grc ty" +
                " zhxp Linyde. Homd hslo zq hoi szhaide kleessy my evl wfxalv td bhqpo hoi Ozu Kejd omxpc hoi deoy.";
        String cipherMode = "Vigenere: hello";

        String autoKeyCipherText = "Ihw kpoi qttiv Liivue, eokw bvion lk hrr Rkt Slty, mv pxazhk ife iqyll hvjy " +
                "qf xzl aqf qa ehxw Ksohdt. Mlim cywd hm bza lsdhjla alomaij bj xhx zydurk pw fuyqh kpw Qos Hdrz " +
                "eihku tfw sytv.";
        String cipherMode2 = "AutoKey: password";

        assertEquals(cipherMode, cracker.parallelBruteForce(cipherText));
        assertEquals(cipherMode2, cracker.parallelBruteForce(autoKeyCipherText));
    }
}
