package com.example.entity;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商家信息表
 *
 * @author tianqi
 * @date 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MerchantInfo extends MerchantInfoKey {
    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 国家
     */
    private String country;

    /**
     * 经营类目（1-餐饮；2-出行）
     */
    private String category;

    /**
     * 细分品类(餐饮类：1-泰餐；2-西餐；……)
     */
    private String subCategory;

    /**
     * 城市（整套的编码-或者取谷歌）
     */
    private String city;

    /**
     * 行政区（整套的编码-或者取谷歌）
     */
    private String district;

    /**
     * 街道（整套的编码-或者取谷歌）
     */
    private String street;

    /**
     * 所属商圈(1-巴吞旺；2吞武里；)
     */
    private String bizAreaId;

    /**
     * 商圈名称
     */
    private String bizAreaName;

    /**
     * 商家电话
     */
    private String merchantTel;

    /**
     * 营业时间（展示层）
     */
    private String bizHours;

    /**
     * 详细地址（谷歌地图获取位置信息）
     */
    private String addr;

    /**
     * 经度
     */
    private String lng;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 店铺图片（默认头图，关闭图片服务时保证C端可用）
     */
    private String merchantPic;

    /**
     * 商家状态，01-已保存，02-待上线，03-已上线，04-已下线，05-已停用
     */
    private String merchSts;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 修改时间
     */
    private Date modifiedDate;

    /**
     * 备注
     */
    private String remark;
}