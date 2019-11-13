package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 13:51
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class UserDto extends User {
    private Date loginTime;  //登陆时间
}
