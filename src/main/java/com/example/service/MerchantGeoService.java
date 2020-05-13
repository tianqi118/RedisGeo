package com.example.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.Constants;
import com.example.entity.MerchantInfo;
import com.example.mapper.MerchantInfoMapper;
import com.example.utils.PageUtil;
import com.example.utils.RedisUtil;
import com.example.utils.StringUtils;
import com.example.vo.MerchantFilterParameter;
import com.example.vo.MerchantListVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    /**
     * 只根据距离排序
     *
     * @return
     */
    public MerchantListVO orderByDistance() {
        //距离
        String nearBy = "50000";
        //经度
        String lng = "103.8154017";
        //纬度
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
        List<MerchantInfo> merchantInfos = null;
        if (StringUtils.isNotEmpty(ids)) {
            log.info("当前的ids：{}", ids);
            merchantInfos = merchantInfoMapper.selectByMerchantIds(ids);
            log.info("merchInfos:{}", JSON.toJSONString(merchantInfos));
        }
        merchantVo.setList(merchantInfos);
        log.info("1.1.商家列表默认距离排序查询完毕");
        return merchantVo;
    }

    /**
     * 按照分类查询（此处仅为demo，业务级查询根据场景做sql封装）
     *
     * @return
     */
    public MerchantListVO orderByCatogaroyAndDistance() {
        MerchantListVO merchantVo = new MerchantListVO();
        String nearBy = "50000";
        String lng = "103.8154017";
        String lat = "1.3150217";
        String category = "SFOOD";
        int pageNum = 1;
        int pageSize = 5;
        //二：有分类筛选，还得按距离排序
        log.info("2.商家列表有分类筛选，还得按距离排序");
        List<MerchantInfo> merchantInfos = merchantInfoMapper.selectByCategory(category);
        log.info("2.2.商家分类筛选完毕");

        List<MerchantInfo> merchantInfoVOList = new ArrayList<>();
        if ("all".equals(nearBy)) {
            //不指定距离半径
            GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = null;
            List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> contents;
            List<String> disList = new ArrayList<>();
            disList.add("10000");
            disList.add("100000");
            disList.add("10000000");
            int countNum = 2000;
            for (String s : disList) {
                geoResults = redisUtil.radiusGeo(SINGAPORE, Double.parseDouble(lng), Double.parseDouble(lat), Double.parseDouble(s));
                if (geoResults.getContent().size() >= countNum) {
                    break;
                }
            }

            merchantVo.setPageNum(pageNum);
            merchantVo.setPageSize(pageSize);
            if (StringUtils.isNull(geoResults)) {
                //指定范围内无商家
                merchantVo.setTotal(0);
                merchantVo.setPages(0);
                return merchantVo;
            }
            contents = geoResults.getContent();
            log.info("开始处理距离");

            HashMap<String, MerchantInfo> map = new HashMap<>();

            //过滤用户地理位置
            ArrayList<GeoResult<RedisGeoCommands.GeoLocation<Object>>> results = new ArrayList<>();
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<Object>>> iterator1 = contents.iterator();
            while (iterator1.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<Object>> next = iterator1.next();
                Object name = next.getContent().getName();
                if (StringUtils.isNotNull(name) && StringUtils.isNotNull(next.getDistance())) {
                    if (name.toString().length() == 12) {
                        results.add(next);
                    }
                }
            }

            if (StringUtils.isNotEmpty(merchantInfos)) {
                for (MerchantInfo merchantInfo : merchantInfos) {
                    map.put(merchantInfo.getMerchantId(), merchantInfo);
                }
            }

            Iterator<GeoResult<RedisGeoCommands.GeoLocation<Object>>> iterator = results.iterator();
            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<Object>> next = iterator.next();

                RedisGeoCommands.GeoLocation<Object> content = next.getContent();
                if (map.containsKey(content.getName().toString())) {
                    Distance distance = next.getDistance();
                    MerchantInfo baseVO = map.get(content.getName().toString());
                    baseVO.setDistance(packageDistance(distance.getValue()));
                    baseVO.setDis(distance.getValue());
                    merchantInfoVOList.add(baseVO);
                    map.remove(content.getName().toString());
                }
            }

            Iterator<String> mapIterator = map.keySet().iterator();
            while (mapIterator.hasNext()) {
                MerchantInfo baseVO = map.get(mapIterator.next());
                baseVO.setDistance("100km+");
                merchantInfoVOList.add(baseVO);
            }
        } else {
            //指定距离半径
            GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults;
            List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> contents;
            geoResults = redisUtil.radiusGeo(SINGAPORE, Double.parseDouble(lng), Double.parseDouble(lat), Double.parseDouble(nearBy));
            merchantVo.setPageNum(pageNum);
            merchantVo.setPageSize(pageSize);
            if (StringUtils.isNull(geoResults)) {
                //指定范围内无商家
                merchantVo.setTotal(0);
                merchantVo.setPages(0);
                return merchantVo;
            }
            contents = geoResults.getContent();

            log.info("开始处理距离");

            HashMap<String, MerchantInfo> map = new HashMap<>();

            //过滤用户地理位置
            ArrayList<GeoResult<RedisGeoCommands.GeoLocation<Object>>> results = new ArrayList<>();
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<Object>>> iterator1 = contents.iterator();
            while (iterator1.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<Object>> next = iterator1.next();
                Object name = next.getContent().getName();
                if (StringUtils.isNotNull(name) && StringUtils.isNotNull(next.getDistance())) {
                    if (name.toString().length() == 12) {
                        results.add(next);
                    }
                }
            }

            if (StringUtils.isNotEmpty(merchantInfos)) {
                for (MerchantInfo merchantInfo : merchantInfos) {
                    map.put(merchantInfo.getMerchantId(), merchantInfo);
                }
            }

            Iterator<GeoResult<RedisGeoCommands.GeoLocation<Object>>> iterator = results.iterator();
            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<Object>> next = iterator.next();

                RedisGeoCommands.GeoLocation<Object> content = next.getContent();
                if (map.containsKey(content.getName().toString())) {
                    Distance distance = next.getDistance();
                    MerchantInfo baseVO = map.get(content.getName().toString());
                    baseVO.setDistance(packageDistance(distance.getValue()));
                    baseVO.setDis(distance.getValue());
                    merchantInfoVOList.add(baseVO);
                }
            }
        }
        //处理距离
        log.info("2.3.所有距离计算完毕");
        //分页
        int total = 0;
        if (StringUtils.isNotEmpty(merchantInfoVOList)) {
            total = merchantInfoVOList.size();
        }
        merchantVo.setPageNum(pageNum);
        merchantVo.setPageSize(pageSize);
        merchantVo.setTotal(total);
        PageUtil<MerchantInfo> listPageUtil = new PageUtil<>();
        List<MerchantInfo> merchantList = new ArrayList<>();
        if (StringUtils.isNotEmpty(merchantInfoVOList)) {
            merchantList = listPageUtil.pageList(merchantInfoVOList, pageNum, pageSize);
        }
        int pages = (total + pageSize - 1) / pageSize;
        merchantVo.setPages(pages);
        merchantVo.setList(merchantList);
        return merchantVo;
    }

    /**
     * 距离仅作为标签，不做距离排序
     *
     * @return
     */
    public MerchantListVO orderOnlyTag() {
        MerchantFilterParameter merchantFilterParameter = new MerchantFilterParameter();
        MerchantListVO merchantVo = new MerchantListVO();
        String nearBy = "50000";
        String lng = "103.8154017";
        String lat = "1.3150217";
        String category = "SFOOD";
        int pageNum = 1;
        int pageSize = 5;

        if ("all".equals(nearBy)) {
            log.info("3.商家列表有定位按平均价格排序,距离为all计算距离");
            // 设置分页
            PageHelper.startPage(pageNum, pageSize);
            List<MerchantInfo> merchantInfos = merchantInfoMapper.selectByCategory(category);
            log.info("3.1.按平均价格排序查询完毕");
            //处理距离
            log.info("开始逐个计算距离");
            distanceHandle(merchantFilterParameter, merchantInfos);
            log.info("3.2.所有距离计算完毕");
            //分页
            PageInfo<MerchantInfo> pageInfo = new PageInfo<>();
            BeanUtils.copyProperties(pageInfo, merchantVo);
            return null;//TODO
        }
        if (!"all".equals(nearBy)) {
            log.info("4.商家列表有定位按平均价格排序,距离为！=all计算距离");
            List<MerchantInfo> merchantInfos = merchantInfoMapper.selectByCategory(category);
            log.info("4.1.按平均价格排序查询完毕");

            GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults;
            List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> contents;

            geoResults = redisUtil.radiusGeo(SINGAPORE, Double.parseDouble(lng), Double.parseDouble(lat), Double.parseDouble(nearBy));
            merchantVo.setPageNum(pageNum);
            merchantVo.setPageSize(pageSize);
            if (StringUtils.isNull(geoResults)) {
                //指定范围内无商家
                merchantVo.setTotal(0);
                merchantVo.setPages(0);
                return merchantVo;
            }
            contents = geoResults.getContent();

            log.info("开始处理距离");

            HashMap<String, Distance> map = new HashMap<>();

            //过滤用户地理位置
            ArrayList<GeoResult<RedisGeoCommands.GeoLocation<Object>>> results = new ArrayList<>();
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<Object>>> iterator1 = contents.iterator();
            while (iterator1.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<Object>> next = iterator1.next();
                Object name = next.getContent().getName();
                if (StringUtils.isNotNull(name) && StringUtils.isNotNull(next.getDistance())) {
                    if (name.toString().length() == 12) {
                        results.add(next);
                    }
                }
            }

            if (StringUtils.isNotEmpty(results)) {
                for (GeoResult<RedisGeoCommands.GeoLocation<Object>> content : results) {
                    map.put(content.getContent().getName().toString(), content.getDistance());
                }
            }
            Iterator<MerchantInfo> iterator = merchantInfos.iterator();
            while (iterator.hasNext()) {
                MerchantInfo next = iterator.next();
                if (map.containsKey(next.getMerchantId())) {
                    Distance distance = map.get(next.getMerchantId());
                    next.setDistance(packageDistance(distance.getValue()));
                    next.setDis(distance.getValue());
                } else {
                    iterator.remove();
                }
            }

            log.info("4.2.所有距离计算完毕");
            //分页
            int total = 0;
            if (StringUtils.isNotEmpty(merchantInfos)) {
                total = merchantInfos.size();
            }

            merchantVo.setTotal(total);
            PageUtil<MerchantInfo> listPageUtil = new PageUtil<>();
            List<MerchantInfo> merchantList = new ArrayList<>();
            if (StringUtils.isNotEmpty(merchantInfos)) {
                merchantList = listPageUtil.pageList(merchantInfos, pageNum, pageSize);
            }
            int pages = (total + pageSize - 1) / pageSize;
            merchantVo.setPages(pages);
            merchantVo.setList(merchantList);
            return merchantVo;
        }
        return null;//TODO
    }


    /**
     * 处理商户距离展示方式,四舍五入（舍弃部分>=.5，就进位）
     *
     * @param distance
     * @return
     */
    public static String packageDistance(Double distance) {
        if (distance < 1000) {
            return distance.intValue() + Constants.GEO_METER;
        } else {
            distance = distance / 1000;
            BigDecimal decimal = new BigDecimal(distance);
            return decimal.setScale(1, BigDecimal.ROUND_HALF_UP) + Constants.GEO_KILOMETER;
        }
    }

    /**
     * @MethodName: merchantHandle
     * @Description: 处理商家距离
     * @Author: dengfengming
     * @Date: 2020/4/11
     **/
    private void distanceHandle(MerchantFilterParameter merchantFilterParameter, List<MerchantInfo> merchantInfos) {
        String lng = merchantFilterParameter.getLng();
        String lat = merchantFilterParameter.getLat();
        String userId = merchantFilterParameter.getUserId();
        String nearBy = merchantFilterParameter.getNearBy();
        if (StringUtils.isNotEmpty(merchantInfos)) {
            Iterator<MerchantInfo> iterator = merchantInfos.iterator();
            while (iterator.hasNext()) {
                MerchantInfo merchantInfo = iterator.next();
                Distance distance = redisUtil.geoDist(SINGAPORE, Double.parseDouble(lng), Double.parseDouble(lat), userId, merchantInfo.getMerchantId());
                if (StringUtils.isNotNull(distance)) {
                    Double value = distance.getValue();
                    if (null != value) {
                        String dis = packageDistance(value);
                        if (!"all".equals(nearBy)) {
                            double tans = Double.parseDouble(nearBy);
                            if (tans < value) {
                                iterator.remove();
                            } else {
                                merchantInfo.setDis(value);
                                merchantInfo.setDistance(dis);
                            }
                        } else if ("all".equals(nearBy)) {
                            merchantInfo.setDis(value);
                            merchantInfo.setDistance(dis);
                        }
                    }
                }
            }
        }
    }
}
