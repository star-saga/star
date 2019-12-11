package org.event.driven.light.omegacommon.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimestampUtils {
    public static String timestamp2String(Timestamp time){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String timeStr="";

        try{
            timeStr=sdf.format(time);
        }catch(Exception e){
            System.out.println("Error in parse Timestemp to String.");
            e.printStackTrace();
        }

        return timeStr;
    }

    public static Timestamp string2Timestemp(String str){
        Timestamp time=new Timestamp(0L);

        try{
            time=Timestamp.valueOf(str);
        }catch(Exception e){
            System.out.println("Error in parse String to Timestemp.");
            e.printStackTrace();
        }

        return time;
    }
}
