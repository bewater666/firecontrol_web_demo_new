package com.orient.firecontrol_web_demo.model.job;

import lombok.Data;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/31 15:51
 * @func
 */
@Data
public class JobDetailDto {
    private String NEXT_FIRE_TIME;  //下次任务执行时间
    private String PREV_FIRE_TIME;  //上次任务执行时间
    private String START_TIME;  //任务开始时间
    private String TRIGGER_STATE;   //任务状态 WAITING等待下次运行  PAUSED暂停  ACQUIRED正常执行 BLOCKED阻塞 ERROR错误
    private List<String> deviceCodeList;    //参与任务调度的设备列表
    private String jobDesc; //任务描述 开灯还是关灯
}
