package com.orient.firecontrol_web_demo.model.job;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/31 15:35
 * @func
 */
@Data
public class JobDetail {
    private String JOB_NAME;
    private BigInteger NEXT_FIRE_TIME;  //下次任务执行时间
    private BigInteger PREV_FIRE_TIME;  //上次任务执行时间
    private BigInteger START_TIME;  //任务开始时间
    private String TRIGGER_STATE;   //任务状态 WAITING等待下次运行  PAUSED暂停  ACQUIRED正常执行 BLOCKED阻塞 ERROR错误



}
