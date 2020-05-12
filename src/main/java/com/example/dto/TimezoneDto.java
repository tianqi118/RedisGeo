package com.example.dto;

import lombok.Data;

import java.util.Date;

/**
 * @program: Mybatis
 * @description: 时间DTO类
 * @author: tianqi
 * @date: 2020-04-30 07:40
 **/
@Data
public class TimezoneDto {

    private Integer id;
    private Date tsStartDate;
    private Date tsEndDate;
    private Date dtStartDate;
    private Date dtEndDate;

}
