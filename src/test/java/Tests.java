import com.mattgarb.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by m on 6/29/17.
 * JUnit 4.11 test coverage.
 */
public class Tests {

    @Test
    public void testRot() {

        String str13 = "Uryyb Jbeyq guvf vf na rknzcyr bs ebg 13.";
        String str14 = "Vszzc Kcfzr hvwg wg ob sloadzs ct fch 14.";
        String str20 = "[]uxezdb a kyLKLauixzed !#$!# XZUAUBL UEMXZA lin 20";
        assertEquals("", Main.rotate("",13));
        assertEquals(str13,Main.rotate(str13,0));
        assertEquals(str13,Main.rotate(str13,26));
        assertEquals(str13,Main.rotate("Hello World this is an example of rot 13.",13));
        assertEquals(str14,Main.rotate("Hello World this is an example of rot 14.",14));
        assertEquals(str20,Main.rotate("[]adkfjh g qeRQRgaodfkj !#$!# DFAGAHR AKSDFG rot 20",20));
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

        assertEquals(dec1,Main.base64(enc1));
        assertEquals(enc1,Main.base64Decode(dec1));
        assertEquals(dec2,Main.base64(enc2));
        assertEquals(dec3,Main.base64(enc3));
        assertEquals(dec4,Main.base64(enc4));
        assertEquals(dec5,Main.base64(enc5));
    }
    @Test
    public void testRoute() {
        String plain = "abort the mission, you have been spotted";
        String invCipher = "ABORT THEMI SSION YOUHA VEBEE NSPOT TEDXX";
        String cipher = "ATSYV NTBHS OESEO EIUBP DRMOH EOXTI NAETX";
        assertEquals(cipher,Route.encrypt(plain, 5));
        assertEquals(invCipher,Route.decrypt(cipher,5));
    }
    @Test
    public void testColumnTranspotions() {
        String plain = "WHICH WRIST WATCH ESARE SWISS WRIST WATCH ESXXX";
        String cipher ="HTHES THXHR ASWRA SCSCR SSCXW WWESW WEIIT AIITX";
        assertEquals("encrypt by array",cipher, ColumnTransposition.encrypt(plain,
                new Integer[]{4, 2, 5, 3, 1}));
        assertEquals("preshared keyword",cipher,ColumnTransposition.encrypt(plain, "dbECA"));
        //failing test!
        assertEquals("decrypt by array", plain, ColumnTransposition.decrypt(cipher, new Integer[]{4, 2, 5, 3, 1}));
    }

    @Test
    public void testVigenere() {
        String out1 = "Ihw kpoi qpmwv Owiljs, sdoc bqdwf so hyh Soy Kpoi, lh bjacvk dcd jaosj kxgz aj hyh hkq aj " +
                "zrwt Amyqgk. Wwik oasb ru tzw dckwtsl oaokktr af pvv vjmewn wj qpmwv pvv Gdg Vsug riiej lds jwpr.";
        String in1 = "The star named Sirius, also known as the Dog Star, is bright and rises high in the sky in" +
                " late August. This week of the hottest weather in the summer is named the Dog Days after the star.";
        String out2 = "Alp dhhv ylalh Dtfpyd, lzzs vycdr ld hoi Ozu Zxlc, wz fctuox lyr ymdpg omrs wu xsp grc ty " +
                "zhxp Linyde. Homd hslo zq hoi szhaide kleessy my evl wfxalv td bhqpo hoi Ozu Kejd omxpc hoi deoy.";

        //psk case insensitive
        assertEquals(out1,Vigenere.encrypt(in1, "PASSword"));
        assertEquals(out2,Vigenere.encrypt(in1," heLLo "));
    }

    @Test
    public void testAutoKey() {
        String example1 = "Ihw kpoi qttiv Liivue, eokw bvion lk hrr Rkt Slty, mv pxazhk ife iqyll hvjy " +
                "qf xzl aqf qa ehxw Ksohdt. Mlim cywd hm bza lsdhjla alomaij bj xhx zydurk pw fuyqh kpw Qos Hdrz " +
                "eihku tfw sytv.";
        String plainText = "The star named Sirius, also known as the Dog Star, is bright and rises high in the sky in" +
                " late August. This week of the hottest weather in the summer is named the Dog Days after the star.";

        String blankMapIn =  "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String blankMapOut = "A C E G I K M O Q S U W Y " +
                "B D F H J L N P R T V X Z";

        assertEquals(AutoKey.encrypt(plainText, " paSSword "), example1);
        assertEquals(AutoKey.decrypt(example1,Main.onlyLowerLetters(" paSSword ")),plainText);

        assertEquals(blankMapOut, AutoKey.encrypt(blankMapIn,""));
        assertEquals(blankMapOut.toLowerCase(), AutoKey.encrypt(blankMapIn.toLowerCase(),""));
        assertEquals(blankMapOut,blankMapIn, AutoKey.decrypt(blankMapOut,""));
        assertEquals(blankMapOut.toLowerCase(),blankMapIn.toLowerCase(),AutoKey.decrypt(
                blankMapOut.toLowerCase(),""));
    }

    @Test
    public void testBruteForce() {
        //set up passSet and wordSet
        new Main.ImportSets().run();

        String cipherText = "Alp dhhv ylalh Dtfpyd, lzzs vycdr ld hoi Ozu Zxlc, wz fctuox lyr ymdpg omrs wu xsp grc ty" +
                " zhxp Linyde. Homd hslo zq hoi szhaide kleessy my evl wfxalv td bhqpo hoi Ozu Kejd omxpc hoi deoy.";
        String cipherMode = "Vigenere: hello";

        String autoKeyCipherText = "Ihw kpoi qttiv Liivue, eokw bvion lk hrr Rkt Slty, mv pxazhk ife iqyll hvjy " +
                "qf xzl aqf qa ehxw Ksohdt. Mlim cywd hm bza lsdhjla alomaij bj xhx zydurk pw fuyqh kpw Qos Hdrz " +
                "eihku tfw sytv.";
        String cipherMode2 = "AutoKey: password";


        assertEquals(cipherMode,Main.parallelBruteForce(cipherText));
        assertEquals(cipherMode2,Main.parallelBruteForce(autoKeyCipherText));
    }
}
