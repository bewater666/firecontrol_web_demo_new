package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/30 10:32
 * @func  建筑修改的bean  为什么要加这个bean  因为为了配合swagger2 使用实体类 前面新增已经用了buildingInfo这个bean
 * 所以要是这个bean某些字段不需要的话 就需要建一个新的bean  来配合swagger2使用@ApiModelProperty这个注解
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingInfoUp {
    @ApiModelProperty(value = "id",name="id",required = true,example = "9")
    private Integer id;
    @ApiModelProperty(value = "建筑编号",name="buildCode",required = true,example = "1111111112")
    private String buildCode;
    @ApiModelProperty(value = "建筑物名称",name="buildName",required = true,example = "测试单位测试更新建筑1")
    private String buildName;
    @ApiModelProperty(value = "建筑物所在经度",name="longitude",required = true,example = "118.77704")
    private String longitude;   //建筑物所在经度;

    @ApiModelProperty(value = "建筑物所在纬度",name="latitude",required = true,example = "32.087041")
    private String latitude;    //建筑物所在纬度;

    @ApiModelProperty(value = "备注信息",name="remark",required = true,example = "备注")
    private String remark;  //备注
}
