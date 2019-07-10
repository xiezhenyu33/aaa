package com.parko.zkcenter.utils;

/**
 * <p>Title: 时间戳转换类</p>
 * <p>Description: 用于时间戳转换</p>
 * <p>File： TimestampConvert.java</p>
 * <p>Copyright 2017 swt</p>
 * @version 1.0
*/

import java.io.PrintStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;


public class TimestampConvert
{

    private static final TimestampConvert instance = new TimestampConvert();
    private static final String copyright = "Copyright 2017 swt. All right reserved.";
    private static Random r = new Random();

    private TimestampConvert()
    {
    }

    public static TimestampConvert getInstance()
    {
        return instance;
    }

	/**
	 * 根据输入的时间字符串得到几天前的，或几天后的日期 i <= +- 28 ; + 正数求为i天前的日期； -为求i 天后的日期;
	 * @param dateString yyyy-mm-dd格式的日期字符串
	 * @return
	 */

    public static String getCurrentDaySeveralDayBefore(String currentDate,int i){
		String retDate, year = currentDate.substring(0,4), month="",day="";
		if (currentDate.length() == 8){
			 month = currentDate.substring(4,6);
			 day = currentDate.substring(6,8);
		 }
		 if (currentDate.length() == 10){
			 year = currentDate.substring(0,4);
			 month = currentDate.substring(5,7);
			 day = currentDate.substring(9,10);
		 }


				 int iYear = Integer.parseInt(year);
				 int iMonth = Integer.parseInt(month);
				 int iDay = Integer.parseInt(day);
				 if(iYear<1900 || iYear>2100) {//年度在1900~2100之间
					 return "";
				 }
				 if(iMonth<1 || iMonth>12) {//月份在1~12
					 return "";
				 }
				 if(iMonth==1 || iMonth==3 || iMonth==5 || iMonth==7 || iMonth==8 || iMonth==10 || iMonth==12) {
					 if(iDay<1 || iDay>31) {
						 return "";
					 }
				 }else if(iMonth==4 || iMonth==6 || iMonth==9 || iMonth==11) {
					 if(iDay<1 || iDay>30) {
						 return "";
					 }
				 }else if(iMonth ==2) {
					 if(iYear%4==0 && iYear%100!=0) {//润年
						 if(iDay<1 || iDay>29) {
							 return "";
						 }
					 }else {//非润年
						 if(iDay<1 || iDay>28) {
							 return "";
						 }
					 }
				 }
				 if( i > 0){ // 天前的日期
					 if(iDay <= i) {
						 iMonth -= 1;
						 if(iMonth == 0) {
							 iYear -= 1;
							 iMonth = 12;
						 }
						 if(iMonth == 1 || iMonth == 3 || iMonth == 5 || iMonth == 7 || iMonth == 8 || iMonth == 10 || iMonth == 12) {
							 iDay += 31;
						 } else if(iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11) {
							 iDay += 30;
						 } else if(iMonth == 2) {
							 if(iYear % 4 == 0 && iYear % 100 != 0) { //润年
								 iDay += 29;
							 } else { //非润年
								 iDay += 28;
							 }
						 }

					 }
					 iDay -= i;
				 }
				 if( i < 0){ //i 天后的日期
					     iDay = iDay - i;
						 //iMonth -= 1;
						 if(iMonth == 1 || iMonth == 3 || iMonth == 5 || iMonth == 7 || iMonth == 8 || iMonth == 10 || iMonth == 12) {
							 if(iDay > 31){
								iDay -= 31;
								if( iMonth !=12){
															iMonth += 1;
														}else{
															iMonth = 1;
															iYear +=1;
														}
					         }
						 } else if(iMonth == 4 || iMonth == 6 || iMonth == 9 || iMonth == 11) {
							 if(iDay > 30){
								iDay -= 30;
								if( iMonth !=12){
															iMonth += 1;
														}else{
															iMonth = 1;
															iYear +=1;
														}
							 }

						 } else if(iMonth == 2) {
							 if(iYear % 4 == 0 && iYear % 100 != 0) { //润年
								if(iDay > 29){
								iDay -= 29;
								if( iMonth !=12){
															iMonth += 1;
														}else{
															iMonth = 1;
															iYear +=1;
														}
							 }

							 } else { //非润年
								 if(iDay > 28){
									iDay -= 28;
									if( iMonth !=12){
																iMonth += 1;
															}else{
																iMonth = 1;
																iYear +=1;
															}
								 }

							 }
						 }

					 }



			     if(iMonth <10){
				    month= "0" + String.valueOf(iMonth);
			     }else{
				    month= String.valueOf(iMonth);
			     }
				 if(iDay<10){
				    day ="0" + String.valueOf(iDay);
			     }else{
					 day = String.valueOf(iDay);
				 }

				 retDate = String.valueOf(iYear) + "-" + month +"-" + day;
			     return retDate;

	}
	/**
	 * 根据输入的时间字符串得到当前星期
	 * @param dateString yyyy-mm-dd格式的日期字符串
	 * @return
	 */
	public static synchronized String getWeekByDate(String dateString)
	{
		String week = "";
		try {
			String year = dateString.substring(0, 4);
			String month = dateString.substring(5, 7);
			String date = dateString.substring(8, 10);
			int iYear = Integer.parseInt(year);
			int iMonth = Integer.parseInt(month);
			int iDate = Integer.parseInt(date);
			Calendar calendar = Calendar.getInstance();
			calendar.set(iYear, iMonth - 1, iDate);
			int iWeek = calendar.get(Calendar.DAY_OF_WEEK);
			switch(iWeek) {
			case Calendar.MONDAY:
				week = "一";
				break;
			case Calendar.TUESDAY:
				week = "二";
				break;
			case Calendar.WEDNESDAY:
				week = "三";
				break;
			case Calendar.THURSDAY:
				week = "四";
				break;
			case Calendar.FRIDAY:
				week = "五";
				break;
			case Calendar.SATURDAY:
				week = "六";
				break;
			case Calendar.SUNDAY:
				week = "日";
			}
		}catch(Exception ex) {
		//ignore
		}
		return week;
	}

    public static synchronized String toGMT(Timestamp timestamp)
    {
        GregorianCalendar gregoriancalendar = (GregorianCalendar)Calendar.getInstance();
        gregoriancalendar.setTime(timestamp);
        long l = gregoriancalendar.get(15) + gregoriancalendar.get(16);
        BigInteger biginteger = BigInteger.valueOf(gregoriancalendar.getTime().getTime()).subtract(BigInteger.valueOf(l));
        Timestamp timestamp1 = new Timestamp(biginteger.longValue());
        timestamp1.setNanos(timestamp.getNanos());
        String s;
        for(s = timestamp1.toString(); s.length() < 26; s = s + "0");
        return s;
    }

    public static synchronized Timestamp toGMT(String s)
    {
        GregorianCalendar gregoriancalendar = (GregorianCalendar)Calendar.getInstance();
        Timestamp timestamp = valueOf(s);
        gregoriancalendar.setTime(timestamp);
        long l = gregoriancalendar.get(15) + gregoriancalendar.get(16);
        BigInteger biginteger = BigInteger.valueOf(gregoriancalendar.getTime().getTime()).subtract(BigInteger.valueOf(l));
        Timestamp timestamp1 = new Timestamp(biginteger.longValue());
        timestamp1.setNanos(timestamp.getNanos());
        return timestamp1;
    }

    public static synchronized String toLocal(Timestamp timestamp)
    {
        GregorianCalendar gregoriancalendar = (GregorianCalendar)Calendar.getInstance();
        gregoriancalendar.setTime(timestamp);
        long l = gregoriancalendar.get(15) + gregoriancalendar.get(16);
        BigInteger biginteger = BigInteger.valueOf(gregoriancalendar.getTime().getTime()).add(BigInteger.valueOf(l));
        Timestamp timestamp1 = new Timestamp(biginteger.longValue());
        timestamp1.setNanos(timestamp.getNanos());
        String s;
        for(s = timestamp1.toString(); s.length() < 26; s = s + "0");
        return s;
    }

    public static synchronized Timestamp toLocal(String s)
    {
        GregorianCalendar gregoriancalendar = (GregorianCalendar)Calendar.getInstance();
        Timestamp timestamp = valueOf(s);
        gregoriancalendar.setTime(timestamp);
        long l = gregoriancalendar.get(15) + gregoriancalendar.get(16);
        BigInteger biginteger = BigInteger.valueOf(gregoriancalendar.getTime().getTime()).add(BigInteger.valueOf(l));
        Timestamp timestamp1 = new Timestamp(biginteger.longValue());
        timestamp1.setNanos(timestamp.getNanos());
        return timestamp1;
    }

    public static Timestamp currentGMTTime()
    {
        GregorianCalendar gregoriancalendar = (GregorianCalendar)Calendar.getInstance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        gregoriancalendar.setTime(timestamp);
        long l = gregoriancalendar.get(15) + gregoriancalendar.get(16);
        BigInteger biginteger = BigInteger.valueOf(gregoriancalendar.getTime().getTime()).subtract(BigInteger.valueOf(l));
        return new Timestamp(biginteger.longValue() + (long)(timestamp.getNanos() / 0xf4240));
    }

    public static synchronized Timestamp currentTime()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setNanos(timestamp.getNanos() + r.nextInt(999) * 1000);
        return timestamp;
    }
    /**
     * 
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static synchronized String getCurrentTime()
    {
    	SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	return sdf.format(new Date());
        
    }
    /**
     * 
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static synchronized String getCurrentDate()
    {
    	SimpleDateFormat  sdf = new SimpleDateFormat("yyyyMMdd");
    	
    	return sdf.format(new Date());
        
    }
    
    /**
     * 获取指定格式的日期字符串
     * @author liweinan
     * @date 2018年10月15日 下午5:34:47
     * @param reg
     * @return
     */
    public static synchronized String getFormatDate(String reg)
    {
    	SimpleDateFormat  sdf = new SimpleDateFormat(reg);
    	
    	return sdf.format(new Date());
        
    }
    
    public static synchronized String currentTimeString()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setNanos(timestamp.getNanos() + r.nextInt(999) * 1000);
        String s;
        for(s = timestamp.toString(); s.length() < 26; s = s + "0");
        return s;
    }

    public static Timestamp currentTime(int i)
    {
        return new Timestamp(System.currentTimeMillis() + (long)i * 0x5265c00L);
    }

    public static Date currentDate()
    {
        return new Date(System.currentTimeMillis());
    }

    public static Date currentDate(int i)
    {
        return new Date(System.currentTimeMillis() + (long)i * 0x5265c00L);
    }

    public static Timestamp valueOf(String s)
    {
        char ac[] = s.toCharArray();
        if(ac.length > 10)
            ac[10] = ' ';
        return Timestamp.valueOf(new String(ac));
    }
    /**
     * 日期格式：按照日期格式返回
     * @param longDate
     * @return
     */
    public static String dateFormatStr(long longDate,String strFormat){
		SimpleDateFormat  sdf = new SimpleDateFormat(strFormat);
		return sdf.format(longDate);
    }
    public static void main(String args[])
    {
    	
		TimestampConvert.getWeekByDate("2004-10-25");
		//if(true) {
		//return;
		//}
        Timestamp timestamp = currentTime();
        System.out.println("current time    : " + timestamp.toString());
        System.out.println("convert to GMT  : " + toGMT(timestamp));
        System.out.println("");
        Timestamp timestamp1 = currentGMTTime();
        System.out.println("GMT     time    : " + timestamp1.toString());
        System.out.println("convert to Local: " + toLocal(timestamp1));
        System.out.println("");
        String s = currentGMTTime().toString();
        System.out.println("req sends this date, in GMT time: " + s);
        timestamp = toLocal(s);
        System.out.println("we format to local time         : " + timestamp.toString());
        s = "2002-08-09 13:14:15.123456";
        System.out.println("req sends this date, in GMT time: " + s);
        timestamp = toLocal(s);
        System.out.println("we format to local time         : " + timestamp.toString());
        s = "2002-01-09 13:14:15.123456";
        System.out.println("req sends this date, in GMT time: " + s);
        timestamp = toLocal(s);
        System.out.println("we format to local time         : " + timestamp.toString());
        s = "2002-01-09 13:14:15.123456";
        System.out.println("req sends this date, in local time: " + s);
        timestamp = toGMT(s);
        System.out.println("we format to GMT time         : " + timestamp.toString());
        System.out.println("we send GMT        time         : " + toGMT(timestamp));
        System.out.println("current date + 3 days: " + currentTime(3));
        System.out.println("\n also lenient parsing\n");
        Timestamp timestamp2 = currentTime();
        Timestamp timestamp3 = currentTime();
        System.out.println("curent time is: " + timestamp2.toString());
        System.out.println("curent time is: " + timestamp3.toString());
        String s1 = currentTimeString();
        String s2 = currentTimeString();
        System.out.println("curent time is: " + s1);
        System.out.println("curent time is: " + s2);
        timestamp2 = valueOf("2001-07-04 10:11:59.999999");
        System.out.println(" from this 2001-07-04 10:11:59.999999");
        System.out.println("to " + timestamp2.toString());
        timestamp2 = valueOf("2001-07-04-10:11:59.999999");
        System.out.println(" from this 2001-07-04-10:11:59.999999");
        System.out.println("to " + timestamp2.toString());
        timestamp2 = valueOf("2001-07-04:10:11:59.999999");
        System.out.println(" from this 2001-07-04:10:11:59.999999");
        System.out.println("to " + timestamp2.toString());
        
        System.out.println(TimestampConvert.getCurrentDate());
        
    }

}
