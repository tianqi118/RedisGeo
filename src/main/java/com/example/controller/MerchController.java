package com.example.controller;

import com.example.service.MerchantGeoService;
import com.example.vo.MerchantListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author:tianqi
 * @Date: 2020-5-12
 */
@Slf4j
@RestController
@RequestMapping("/merch")
public class MerchController {
    @Autowired
    private MerchantGeoService merchantGeoService;

    //请求样例：http://127.0.0.1:8080/merch/orderOnlyTag/23/5/500000



    @RequestMapping("getId/{id}")
    public String getId(@PathVariable int id) {
        merchantGeoService.loadMerchGeo();
        return "success-" + id + "-" + new Date();
    }

    @RequestMapping("getGeo/{id}")
    public String getGeo(@PathVariable int id) {
        merchantGeoService.loadMerchGeo();
        return "success-" + id + "-" + new Date();
    }

    @RequestMapping("orderByDistance/{pageNum}/{pageSize}/{nearBy}")
    public MerchantListVO orderByDistance(@PathVariable int pageNum, @PathVariable int pageSize, @PathVariable String nearBy) {
        log.info("接收到的参数：pageNum：{}，pageSize:{}，nearBy:{}", pageNum, pageSize, nearBy);
        //距离
//        String nearBy = "500000";
//        int pageNum = 1;
//        int pageSize = 5;
        //经度
        String lng = "103.8154017";
        //纬度
        String lat = "1.3150217";

        MerchantListVO merchantListVO = merchantGeoService.orderByDistance(nearBy, lng, lat, pageNum, pageSize);
        return merchantListVO;
    }


    @RequestMapping("orderByCatogaroyAndDistance/{pageNum}/{pageSize}/{nearBy}")
    public MerchantListVO orderByCatogaroyAndDistance(@PathVariable int pageNum, @PathVariable int pageSize, @PathVariable String nearBy) {
        log.info("接收到的参数：pageNum：{}，pageSize:{}，nearBy:{}", pageNum, pageSize, nearBy);
        //距离
//        String nearBy = "500000";
//        int pageNum = 1;
//        int pageSize = 5;
        //经度
        String lng = "103.8154017";
        //纬度
        String lat = "1.3150217";

        MerchantListVO merchantListVO = merchantGeoService.orderByCatogaroyAndDistance(nearBy, lng, lat, pageNum, pageSize);
        return merchantListVO;
    }

    @RequestMapping("orderOnlyTag/{pageNum}/{pageSize}/{nearBy}")
    public MerchantListVO orderOnlyTag(@PathVariable int pageNum, @PathVariable int pageSize, @PathVariable String nearBy) {
        log.info("接收到的参数：pageNum：{}，pageSize:{}，nearBy:{}", pageNum, pageSize, nearBy);
        //经度
        String lng = "103.8154017";
        //纬度
        String lat = "1.3150217";
        //模拟一个用户ID
        String userId = "U12345678998765543299999";
        MerchantListVO merchantListVO = merchantGeoService.orderOnlyTag(userId, nearBy, lng, lat, pageNum, pageSize);
        return merchantListVO;
    }


}
