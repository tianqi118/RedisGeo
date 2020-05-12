package com.example.utils;

import java.util.List;

/**
 * @Auther: dfm
 * @Date: 2019/12/31 15:07
 * @Description:
 */

public class PageUtil<T> {
    private int beginIndex;//起始索引
    private int endIndex;//终止索引

    public List<T> pageList(List<T> list, int pageNo, int pageSize) {
        int size = list.size();
        beginIndex = (pageNo - 1) * pageSize;
        endIndex = pageNo * pageSize > size ? size : pageNo * pageSize;
        List<T> resultList = list.subList(beginIndex, endIndex);
        return resultList;
    }
}
