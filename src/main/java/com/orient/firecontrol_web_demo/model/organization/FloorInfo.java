package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/5 15:29
 * @func 某建筑物下的楼层bean
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloorInfo {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(name = "buildCode",value = "建筑物编号",required = true,example = "3201130001")
    private String buildCode; //建筑物编号

    @ApiModelProperty(name = "floorCode",value = "楼层编号",required = true,example = "6")
    private Integer floorCode; //楼层编号 例:5

    @ApiModelProperty(name = "floorName",value = "楼层名称",required = true,example = "6楼")
    private String floorName;   //楼层名称 例:5楼
}
