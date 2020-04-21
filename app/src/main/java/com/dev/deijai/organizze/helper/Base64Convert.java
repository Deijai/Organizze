package com.dev.deijai.organizze.helper;

import android.util.Base64;

public class Base64Convert {

    public static String codificarParaBase64(String string){
        return Base64.encodeToString(string.getBytes(), Base64.DEFAULT).replaceAll("\\n|\\r", "");
    }

    public static String decodificarParaBase64(String stringCodificada){
        return new String(  Base64.decode(stringCodificada.getBytes(), Base64.DEFAULT) );
    }
}
