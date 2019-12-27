package com.orient.firecontrol_web_demo.model.job;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/27 11:48
 * @func
 */
@Data
public class ReScheduleDto {
    @ApiModelProperty(name = "jobClassName",value = "connectJob还是breakJob",example = "closeJob",required = true)
    private String jobClassName;
    @ApiModelProperty(name = "jobGroupName",value = "任务调度名称",example = "南京办公室-南京办公室-5楼-测试调度1",required = true)
    private String jobGroupName;
    @ApiModelProperty(name = "cronExpression",value = "重新设置cron表达式",example = "0 0 10 * * ?",required = true)
    private String cronExpression;
    @ApiModelProperty(name = "deviceCodeList",value = "重新设置要进行任务调度设备",example = "[3201130001010000,3201130001010002]",required = true)
    private List<String> deviceCodeList;
}
