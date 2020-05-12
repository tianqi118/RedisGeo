package com.example.service;

import com.example.entity.MerchantInfo;
import com.example.mapper.MerchantInfoMapper;
import com.example.utils.RedisUtil;
import com.example.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianqi
 */
@Slf4j
@Service
public class MerchantGeoService {
    // 新加坡
    public static final String SINGAPORE = "SIN";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    MerchantInfoMapper merchantInfoMapper;

    /**
     * laod Merchinfo from db to Redis
     */
    public void loadMerchGeo() {

        //获取所有商家信息
        List<MerchantInfo> merchantInfos = merchantInfoMapper.selectMerchantList();
        //存放商户地理信息
        List<RedisGeoCommands.GeoLocation<String>> geoLocations = new ArrayList<>();
        for (MerchantInfo merchantInfo : merchantInfos) {
            //保存具有经纬度信息的商家
            if (StringUtils.isNotEmpty(merchantInfo.getLng()) && StringUtils.isNotEmpty(merchantInfo.getLat())) {
                Point point = new Point(Double.parseDouble(merchantInfo.getLng()), Double.parseDouble(merchantInfo.getLat()));
                RedisGeoCommands.GeoLocation<String> location = new RedisGeoCommands.GeoLocation<>(merchantInfo.getMerchantId(), point);
                geoLocations.add(location);
            }
        }
        //批量导入商户地理位置信息
        log.info("保存商户地理信息到redisGeo");
        boolean saveflag = redisUtil.addGeoBatch(SINGAPORE, geoLocations);
        if (saveflag) {
            log.info("保存商户地理信息完毕");
        } else {
            log.error("保存商户地理信息异常");
        }
    }

}
