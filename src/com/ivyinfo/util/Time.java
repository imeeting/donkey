package com.ivyinfo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    /**
     * 转换取时间格式
     * @param aDate
     * @return String
     */
    public static final String getDateTime(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "0";

        if (aDate != null && aDate.getTime() != 0L) {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }
    
    /**
     * 由String类型的yyyy-MM-dd HH:mm:ss格式输出Date类型的yyyy-MM-dd 格式的时间
     * <p>
     * @param  string
     * @return Date
     * @exception
     *
     */
    public static Date getDateFormatYMDHMSString(String sourcedate) {
        if (sourcedate == null) {
        	//System.out.println("null sourcedate");
            return null;
        }
        Date newDate = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            newDate = sdf.parse(sourcedate);

        } catch (Exception e) {
        	//System.out.println("exception!!!!!");
            return null;
        }
        /*if(newDate!=null)
        	System.out.println("new date: " + newDate.toString());
        else
        	System.out.println("null new date");
        */
        return newDate;
    }
    
    public static String format(long ms) {//将毫秒数换算成x天x时x分x秒x毫秒
    	   int ss = 1000;
    	   int mi = ss * 60;
    	   int hh = mi * 60;
    	   int dd = hh * 24;

    	   long day = ms / dd;
    	   long hour = (ms - day * dd) / hh;
    	   long minute = (ms - day * dd - hour * hh) / mi;
    	   long second = (ms - day * dd - hour * hh - minute * mi) / ss;
    	   long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

    	   String strDay = day < 10 ? "0" + day : "" + day;
    	   String strHour = hour < 10 ? "0" + hour : "" + hour;
    	   String strMinute = minute < 10 ? "0" + minute : "" + minute;
    	   String strSecond = second < 10 ? "0" + second : "" + second;
    	   String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;
    	   strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
    	   String str1="";
    	   String str2="";
    	   String str3="";
    	   String str4="";
    	   if(!"00".equals(strDay))
    		   str1=strDay + " ";
    	   return str1 + strHour + ":" + strMinute + ":" + strSecond ;
    	} 
    
    //@test
//    public static void main(String[] args){
//    	String strData = "2011-6-17 14:55:30";
//    	System.out.println(getDateFormatYMDHMSString(strData).getTime());
//    	
//    	String curTime = getDateTime(new Date(1308293730000L));
//    	System.out.println(curTime);
//    }
    
}
