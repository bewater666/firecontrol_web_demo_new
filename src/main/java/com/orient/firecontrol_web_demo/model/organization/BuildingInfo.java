package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/15 15:56
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingInfo {
    @ApiModelProperty(hidden = true)
    private Integer id;
    @ApiModelProperty(value = "建筑编号",name="buildCode",required = true,example = "3201134444")
    private String buildCode;
    @ApiModelProperty(value = "建筑物名称",name="buildName",required = true,example = "南京办公室测试建筑4")
    private String buildName;

    @ApiModelProperty(value = "建筑物所在经度",name="longitude",required = true,example = "118.77704")
    private String longitude;   //建筑物所在经度;

    @ApiModelProperty(value = "建筑物所在纬度",name="latitude",required = true,example = "32.087041")
    private String latitude;    //建筑物所在纬度;

    @ApiModelProperty(value = "备注信息",name="remark",required = true,example = "备注")
    private String remark;  //备注
}
