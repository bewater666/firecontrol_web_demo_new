package com.orient.firecontrol_web_demo.model.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/26 16:29
 * @func
 */
@Data
public class JobAndTrigger {
    private String JOB_NAME;
    private String JOB_GROUP;
    private String JOB_CLASS_NAME;
    private String TRIGGER_NAME;
    private String TRIGGER_GROUP;

    @JsonIgnore
    private BigInteger REPEAT_INTERVAL;
    @JsonIgnore
    private BigInteger TIMES_TRIGGERED;

    private String CRON_EXPRESSION;
    private String TIME_ZONE_ID;
    //任务状态
    private String TRIGGER_STATE;
}
