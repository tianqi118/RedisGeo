package com.example.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName MerchantFilterParameter
 * @Description: 商户筛选参数
 * @Author dengfengming
 * @Date 2020/3/11
 * @Version V1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MerchantFilterParameter {

    //用户ID
    private String userId;

    //经度
    private String lat;

    //纬度
    private String lng;

    //距离
    private String nearBy;

    //商圈
    private String businessDistrict;

    //分类条件
    private String category;

    //排序条件
    private String sorting;

    //商家ids
    private List<String> merchantIds;

    //距离不做筛选、
    private Boolean filter;
}
