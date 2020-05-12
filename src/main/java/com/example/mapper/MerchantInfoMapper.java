package com.example.mapper;

import com.example.entity.MerchantInfo;
import com.example.entity.MerchantInfoKey;

/**
 * Created by Mybatis Generator
 *
 * @author Mybatis Generator
 * @date 2020-05-12
 */
public interface MerchantInfoMapper {
    int deleteByPrimaryKey(MerchantInfoKey key);

    int insert(MerchantInfo record);

    int insertSelective(MerchantInfo record);

    MerchantInfo selectByPrimaryKey(MerchantInfoKey key);

    int updateByPrimaryKeySelective(MerchantInfo record);

    int updateByPrimaryKey(MerchantInfo record);
}