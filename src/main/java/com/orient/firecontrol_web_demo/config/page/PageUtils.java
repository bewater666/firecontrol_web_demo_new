package com.orient.firecontrol_web_demo.config.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 17:03
 * @func 分页排序通用工具类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageUtils implements Serializable {
    private static final long serialVersionUID = 3039816540468659394L;

    @Min(value = 1,message = "当前页数不能小于1")
    private Integer page;

    @Min(value = 1,message = "每页条数不能小于1")
    @Max(value = 50,message = "每页条数不能大于50")
    private Integer rows;

    /** 排序的列名 */
    private String sidx;

    /** 排序规则(DESC或者ESC) */
    private String sord;




}
