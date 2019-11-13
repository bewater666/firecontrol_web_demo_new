package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/24 10:22
 * @func 单位实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "单位名称",name = "organizationName",required = true,example = "测试单位2")
    private String organizationName;    //单位名称

    @ApiModelProperty(value = "所在省份",name = "province",required = true,example = "江苏省")
    private String province;    //所在省份

    @ApiModelProperty(value = "所在城市",name = "city",required = true,example = "南京市")
    private String city;    //所在市

    @ApiModelProperty(value = "备注信息",name = "remark",required = true,example = "备注")
    private String remark;  //备注

    @ApiModelProperty(hidden = true)
    private List<BuildingInfo> buildingInfoList = new ArrayList<>(); //单位下的建筑物列表
}
