package com.orient.firecontrol_web_demo.model.job;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/27 9:51
 * @func
 */
@Data
@Accessors(chain = true)
public class JobDto {
    @ApiModelProperty(name = "jobClassName",value = "闭合任务调度",example = "connectJob",required = true)
    private String jobClassName;
    @ApiModelProperty(name = "organizationName",value = "单位名称",example = "南京办公室",required = true)
    private String organizationName;
    @ApiModelProperty(name = "buildName",value = "建筑物名称",example = "南京办公室",required = true)
    private String buildName;
    @ApiModelProperty(name = "floorName",value = "楼层名称",example = "5楼",required = true)
    private String floorName;
    @ApiModelProperty(name = "jobGroupName",value = "具体调度名称",example = "测试调度1",required = true)
    private String jobGroupName;
    @ApiModelProperty(name = "cronExpression",value = "cron表达式",example = "0 0 10 * * ?",required = true)
    private String cronExpression;
    @ApiModelProperty(name = "deviceCodeList",value = "设备code",example = "[3201130001010000,3201130001010001]",required = true)
    private List<String> deviceCodeList;
}
