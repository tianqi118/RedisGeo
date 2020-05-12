package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.dto.TimezoneDto;
import com.example.entity.TimeZoneTable;
import com.example.service.UserService;
import com.example.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Author:0xOO
 * @Date: 2018/9/26 0026
 * @Time: 14:42
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("getUser/{id}")
    public String getUser(@PathVariable int id) {
        return userService.sel(id).toString();
    }

    @RequestMapping("getId/{id}")
    public String getId(@PathVariable int id) {
        TimeZoneTable timezone = userService.getTime(id);
        System.out.println(JSON.toJSON(timezone));

        return "success" + new Date();
    }

    @RequestMapping("insert")
    public String insertVal() {
        TimeZoneTable timeZone = new TimeZoneTable();
        timeZone.setDt(new Date());
        timeZone.setTs(new Date());
        log.info("插入数据：{},{}", DateUtil.changeDate(timeZone.getDt()), DateUtil.changeDate(timeZone.getTs()));
        int isrt = userService.insertTime(timeZone);
        log.info("插入条数：{}", isrt);
        return "success" + new Date();
    }

    @RequestMapping("getAll")
    public String getAllData() {
        List<TimeZoneTable> timeZoneTableList = userService.getAllData();
        for (TimeZoneTable timeZonefor : timeZoneTableList) {
            getTime(timeZonefor);
        }
        return "success-getAll" + new Date();
    }

    @RequestMapping("getAllExchange")
    public String getAllExchange() {
        List<TimeZoneTable> timeZoneTableList = userService.getAllData();
        for (TimeZoneTable timeZonefor : timeZoneTableList) {
            getExchangeTime(timeZonefor, DateUtil.TIME_ZONE_8);
            getExchangeTime(timeZonefor, DateUtil.TIME_ZONE_7);
        }
        return "success-getAll-" + new Date();
    }

    @RequestMapping("getByDate")
    public String getListByDate() {
        String dtSartDate = "2020-04-30 13:27:00";
        String dtEndDate = "2020-04-30 13:50:00";

        Date start = DateUtil.changeZoneDate(dtSartDate, DateUtil.TIME_ZONE_8);
        Date end = DateUtil.changeZoneDate(dtEndDate, DateUtil.TIME_ZONE_8);

        TimezoneDto dto = new TimezoneDto();
//        dto.setDtStartDate(DateUtil.convertToDate(dtSartDate, DateUtil.FORMAT_1));
//        dto.setDtEndDate(DateUtil.convertToDate(dtEndDate, DateUtil.FORMAT_1));
//        dto.setDtStartDate(start);
//        dto.setDtEndDate(end);
        log.info("查询条件：{}", JSON.toJSONString(dto));
        log.info("test version:{}", "v1.1.1");
//        dto.setTsStartDate(DateUtil.convertDateFromString(dtSartDate, DateUtil.FORMAT_1));
//        dto.setTsEndDate(DateUtil.convertDateFromString(dtEndDate, DateUtil.FORMAT_1));
        dto.setTsStartDate(start);
        dto.setTsEndDate(end);
        List<TimeZoneTable> timeZoneTableList = userService.getListByDate(dto);
        log.info("查询结果：{}", JSON.toJSON(timeZoneTableList));

        return "success-getByDate";
    }

    /**
     * 数据库表时间
     *
     * @param timezone
     */
    private void getTime(TimeZoneTable timezone) {
        Date dateTime = timezone.getDt();
        Date timeStamp = timezone.getTs();
        log.info("DateTime：{}", dateTime);
        log.info("TimeStamp：{}", timeStamp);
        log.info("DateTime：{}", DateUtil.changeDate(dateTime));
        log.info("TimeStamp：{}", DateUtil.changeDate(timeStamp));
    }

    /**
     * 按照时区转换表的时间createFromTimestamp
     *
     * @param timezone
     * @param zone
     */
    private void getExchangeTime(TimeZoneTable timezone, String zone) {
        Date dateTime = timezone.getDt();
        String dt = DateUtil.changeZoneDate(dateTime, zone);
        log.info("时区:{}，DT:{}", zone, dt);
        Date timeStamp = timezone.getTs();
        String ts = DateUtil.changeZoneDate(timeStamp, zone);
        log.info("时区:{}，TS:{}", zone, ts);

    }


}
