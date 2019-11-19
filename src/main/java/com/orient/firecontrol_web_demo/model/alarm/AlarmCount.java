package com.orient.firecontrol_web_demo.model.alarm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/19 11:21
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmCount {
    private Date click_date;
    private Integer count;
}
