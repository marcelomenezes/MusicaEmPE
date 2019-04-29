package xie.araca.musicaempe.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codeBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decodeBase64(String textDecoded){
        return new String(Base64.decode(textDecoded, Base64.DEFAULT));
    }
}
