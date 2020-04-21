package com.dev.deijai.organizze.helper;

import java.text.SimpleDateFormat;

public class DateUtil {

    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static String dataFirebase(String data){
        String d [] = data.split("/");
        String mesAno = d[1]+d[2];
        return mesAno;
    }
}
