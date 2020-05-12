package com.example.service;

import com.example.dto.TimezoneDto;
import com.example.entity.TimeZoneTable;
import com.example.entity.TimeZoneTableKey;
import com.example.entity.User;
import com.example.mapper.TimeZoneTableMapper;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:0xOO
 * @Date: 2018/9/26 0026
 * @Time: 15:23
 */
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    TimeZoneTableMapper timeZoneTableMapper;


    public User sel(int id) {
        return userMapper.sel(id);
    }

    /**
     * 查询指定数据
     *
     * @param id
     * @return
     */
    public TimeZoneTable getTime(int id) {
        TimeZoneTableKey tableKey = new TimeZoneTableKey();
        tableKey.setId(id);
        return timeZoneTableMapper.selectByPrimaryKey(tableKey);
    }

    /**
     * 获取数据库中所有数据
     *
     * @return
     */
    public List<TimeZoneTable> getAllData() {
        return timeZoneTableMapper.selectAll();
    }

    /**
     * 插入时间戳
     *
     * @param timeZone
     * @return
     */
    public int insertTime(TimeZoneTable timeZone) {
        return timeZoneTableMapper.insert(timeZone);
    }

    /**
     * 根据时间区间查询数据
     *
     * @param timezoneDto
     * @return
     */
    public List<TimeZoneTable> getListByDate(TimezoneDto timezoneDto) {
        return timeZoneTableMapper.getListByDate(timezoneDto);
    }


}
