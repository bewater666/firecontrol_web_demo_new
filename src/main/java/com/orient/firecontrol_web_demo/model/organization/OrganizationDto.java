package com.orient.firecontrol_web_demo.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/25 10:05
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationDto {
    @ApiModelProperty(name = "id",value = "id",required = true,example = "5")
    private Integer id;

    @ApiModelProperty(name = "organizationName",value = "单位名称",required = true,example = "测试更新单位")
    private String organizationName;    //单位名称

    @ApiModelProperty(value = "所在省份",name = "province",required = true,example = "江苏省")
    private String province;    //所在省份

    @ApiModelProperty(value = "所在城市",name = "city",required = true,example = "南京市")
    private String city;    //所在市

    @ApiModelProperty(value = "备注信息",name = "remark",required = true,example = "备注")
    private String remark;  //备注
}
