package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 11:25
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class UserRole implements Serializable {
    private static final long serialVersionUID = -851363308007467865L;

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "",name = "userId",required = true,example = "9")
    private Integer userId;

    @ApiModelProperty(value = "",name = "roleId",required = true,example = "3")
    private Integer roleId;
}
