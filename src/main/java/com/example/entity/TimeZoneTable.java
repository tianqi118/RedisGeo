package com.example.entity;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 
 *
 * @author tianqi
 * @date 2020-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TimeZoneTable extends TimeZoneTableKey {
    /**
     * 
     */
    private Date ts;

    /**
     * 
     */
    private Date dt;
}