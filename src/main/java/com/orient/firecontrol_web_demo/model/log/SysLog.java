package com.orient.firecontrol_web_demo.model.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 10:30
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class SysLog {
    private Integer id;
    private String operate; //操作内容
    private String operator;    //操作人
    private Date operateTime;   //操作时间
    private String operateIP;   //操作人的IP地址
    private String method;  //方法名
    private String params;  //参数
}
