package com.appublisher.quizbank.common.utils;

/**
 * Created by Admin on 2017/1/5.
 */

public class TimeUtils {

    public static String formatDateTime(int mss) {

        String DateTimes = null;
        int minutes = ( mss % ( 60 * 60) ) / 60;
        int seconds = mss % 60;

        if(minutes>0){
            if(seconds < 10){
                DateTimes = minutes + "\'" + "0" + seconds + "\"";
                return DateTimes;
            }
            DateTimes = minutes + "\'" + seconds + "\"";
        }else{
            DateTimes = seconds + "\"";
        }
        return DateTimes;
    }
}
