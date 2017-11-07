package com.dzg.yingyin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dengzhouguang on 2017/11/4.
 */

public class TextUtil {
    public static String parseTime(String str){
        int time=Integer.parseInt(str)/1000;
        int hour=time/3600;
        int minute=(time-hour*3600)/60;
        int second=time-hour*3600-minute*60;
        String hourstr,minutestr,secondstr;
        if (hour==0)
            hourstr="00";
        else if (hour<10)
            hourstr=0+String.valueOf(hour);
        else
            hourstr=String.valueOf(hour);
        if (minute==0)
            minutestr="00";
        else if (minute<10)
            minutestr=0+String.valueOf(minute);
        else
            minutestr=String.valueOf(minute);
        if (second==0)
            secondstr="00";
        else if (second<10)
            secondstr=0+String.valueOf(second);
        else
            secondstr=String.valueOf(second);
        return hourstr+":"+minutestr+":"+secondstr;
    }

    public static String  parseSize(String str){
        long size=Long.parseLong(str)/(1024*1024);
        return  size+"MB";
    }

    public static String getHourAndMinutes(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(currentTime);
    }
}
