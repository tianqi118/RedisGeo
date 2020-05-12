package com.example.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date工具类
 *
 * @author tianqi
 * @date 2020-4-30
 */
public class DateUtil {

    public static String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static String TIME_ZONE_0 = "GMT+0:00";
    public static String TIME_ZONE_7 = "GMT+7:00";
    public static String TIME_ZONE_8 = "GMT+8:00";

    /**
     * 根据时区转换时间
     *
     * @param date
     * @param timeZone
     * @return
     */
    public static String changeZoneDate(Date date, String timeZone) {
        SimpleDateFormat sdf0 = new SimpleDateFormat(FORMAT_1);
        sdf0.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf0.format(date);
    }


    /**
     * String 转为指定时区 date
     *
     * @param date
     * @param timeZone
     * @return
     */
    public static Date changeZoneDate(String date, String timeZone) {
        SimpleDateFormat sdf0 = new SimpleDateFormat(FORMAT_1);
        sdf0.setTimeZone(TimeZone.getTimeZone(timeZone));
        return parseDate(date, sdf0);
    }

    /**
     * 转换为固定时间格式
     *
     * @param date
     * @return
     */
    public static String changeDate(Date date) {
        SimpleDateFormat sdf0 = new SimpleDateFormat(FORMAT_1);
        return sdf0.format(date);
    }

    /**
     * String转Date
     *
     * @param date
     * @param format
     * @return
     */
    public static Date convertToDate(String date, String format) {
        SimpleDateFormat sdf0 = new SimpleDateFormat(format);
        return parseDate(date, sdf0);
    }

    /**
     * 私有处理
     *
     * @param date
     * @param sdf
     * @return
     */
    public static Date parseDate(String date, SimpleDateFormat sdf) {
        Date newDate = null;
        try {
            newDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static void main(String[] args) {
        Date date = new Date(1588000843 * 1000L);

        SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf0.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));//设置时区为东八区
        System.out.println("UTC的时间:" + sdf0.format(date));//输出格式化日期

        SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf8.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//设置时区为东八区
        System.out.println("东八区的时间:" + sdf8.format(date));//输出格式化日期

        SimpleDateFormat sdf7 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf7.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));//设置时区为东七区
        System.out.println("东七区的时间:" + sdf7.format(date));//输出格式化日期
    }
}
