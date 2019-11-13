package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/29 22:59
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class LoginUser {
    @ApiModelProperty(name = "account",value = "账户",required = true,example = "superadmin")
    private String account;
    @ApiModelProperty(name = "password",value = "密码",required = true,example = "")
    private String password;
}
