package com.example;

/**
 * @author huochunjie
 * @Description: 常量类
 * @date 2019年9月19日
 */
public class Constants {

    /***************** Redis GEO***************************/
    // 商户位置缓存key前缀
    public static final String GEO_PRREFIX = "aquiver:geo:";
    // 记录上次缓存商户地理信息时间key
    public static final String GEO_RECORD_TIME = "aquiver_saveGeoTime";
    // 距离单位 ：m
    public static final String GEO_METER = "m";
    // 距离单位 ：km
    public static final String GEO_KILOMETER = "km";

}