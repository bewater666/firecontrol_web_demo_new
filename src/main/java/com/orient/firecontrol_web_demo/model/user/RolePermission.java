package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 11:27
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolePermission implements Serializable {
    private Integer id;
    private Integer roleId;
    private Integer permId;
}
