package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 11:16
 * @func
 */
@Data@JsonInclude(JsonInclude.Include.NON_NULL)
public class Permission implements Serializable {
    private static final long serialVersionUID = 1843208885066675268L;


    private Integer id;
    private String permName;
    private String permCode;
}
