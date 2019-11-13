package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 11:01
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role implements Serializable {

    private static final long serialVersionUID = 1294172508897467264L;

    private Integer id;
    private String roleName;
}
