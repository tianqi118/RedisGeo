package com.example.vo;

import com.example.entity.MerchantInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianqi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MerchantListVO {
    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页条目数
     */
    private int pageSize;

    /**
     * 总条数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    List<MerchantInfo> list = new ArrayList<>();

}
