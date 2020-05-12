package com.example.controller;

import com.example.service.MerchantGeoService;
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

    @RequestMapping("getId/{id}")
    public String getId(@PathVariable int id) {
        merchantGeoService.loadMerchGeo();
        return "success-" + id + "-" + new Date();
    }

    @RequestMapping("getGeo/{id}")
    public String getGeo(@PathVariable int id) {
        merchantGeoService.getDefaultGeoSorted();
        return "success-" + id + "-" + new Date();
    }



}
