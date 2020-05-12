package com.example.mapper;

import com.example.dto.TimezoneDto;
import com.example.entity.TimeZoneTable;
import com.example.entity.TimeZoneTableKey;

import java.util.List;

/**
 * Created by Mybatis Generator
 *
 * @author Mybatis Generator
 * @date 2020-04-29
 */
public interface TimeZoneTableMapper {
    int deleteByPrimaryKey(TimeZoneTableKey key);

    int insert(TimeZoneTable record);

    int insertSelective(TimeZoneTable record);

    TimeZoneTable selectByPrimaryKey(TimeZoneTableKey key);

    int updateByPrimaryKeySelective(TimeZoneTable record);

    int updateByPrimaryKey(TimeZoneTable record);

    List<TimeZoneTable> selectAll();

    List<TimeZoneTable> getListByDate(TimezoneDto timezoneDto);

}