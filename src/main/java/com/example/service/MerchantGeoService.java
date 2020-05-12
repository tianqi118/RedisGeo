package com.example.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.MerchantInfo;
import com.example.entity.MerchantInfoKey;
import com.example.mapper.MerchantInfoMapper;
import com.example.utils.PageUtil;
import com.example.utils.RedisUtil;
import com.example.utils.StringUtils;
import com.example.vo.MerchantListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public void getDefaultGeoSorted() {
        //距离
        String nearBy = "50000";
        String lng = "103.8154017";
        String lat = "1.3150217";
        int pageNum = 1;
        int pageSize = 5;
        MerchantListVO merchantVo = new MerchantListVO();
        Map<String, Double> mapDistance = new HashMap<>();

        //一：默认查询只按距离排序
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = null;
        List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> contents = null;
        log.info("1.商家列表默认距离排序查询");

        if ("all".equals(nearBy)) {
            geoResults = redisUtil.radiusGeo(SINGAPORE, Double.parseDouble(lng), Double.parseDouble(lat), 9999999999999999D);
        }
        if (!"all".equals(nearBy)) {
            geoResults = redisUtil.radiusGeo(SINGAPORE, Double.parseDouble(lng), Double.parseDouble(lat), Double.parseDouble(nearBy));
        }
        if (null != geoResults) {
            contents = geoResults.getContent();
        }
        ArrayList<GeoResult<RedisGeoCommands.GeoLocation<Object>>> results = new ArrayList<>();
        if (StringUtils.isNotEmpty(contents)) {
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<Object>>> iterator = contents.iterator();
            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<Object>> next = iterator.next();
                Object name = next.getContent().getName();
                if (StringUtils.isNotNull(name) && StringUtils.isNotNull(next.getDistance())) {
                    if (name.toString().length() == 12) {
                        results.add(next);
                    }
                }
            }
        }
        int total = 0;
        if (StringUtils.isNotEmpty(results)) {
            total = results.size();
        }
        merchantVo.setPageNum(pageNum);
        merchantVo.setPageSize(pageSize);
        merchantVo.setTotal(total);
        PageUtil<GeoResult<RedisGeoCommands.GeoLocation<Object>>> contentsPageUtil = new PageUtil<>();
        //根据商家ids查询商家列表
        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotEmpty(results)) {
            List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> resultsIds = contentsPageUtil.pageList(results, pageNum, pageSize);
            //处理距离
            if (StringUtils.isNotEmpty(resultsIds)) {
                resultsIds.forEach(s -> {
                    mapDistance.put((String) s.getContent().getName(), s.getDistance().getValue());
                    ids.add((String) s.getContent().getName());
                });
            }
        }
        int pages = (total + pageSize - 1) / pageSize;
        merchantVo.setPages(pages);
        //根据商家ids查询商家列表
        if (StringUtils.isNotEmpty(ids)) {
            log.info("当前的ids：{}", ids);
            List<MerchantInfo> merchantInfos = merchantInfoMapper.selectByMerchantIds(ids);
            log.info("merchInfos:{}", JSON.toJSONString(merchantInfos));
        }

        log.info("1.1.商家列表默认距离排序查询完毕");


    }

}
