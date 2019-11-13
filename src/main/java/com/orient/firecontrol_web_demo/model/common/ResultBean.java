package com.orient.firecontrol_web_demo.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/18 10:47
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultBean {
    private Integer code;
    private String msg;
    private Object data;


    public ResultBean(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultBean(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
