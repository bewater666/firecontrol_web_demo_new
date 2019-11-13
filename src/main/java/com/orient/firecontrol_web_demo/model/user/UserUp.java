package com.orient.firecontrol_web_demo.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/12 14:07
 * @func
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class UserUp implements Serializable {
    private static final long serialVersionUID = -6331389811745804974L;

    @ApiModelProperty(name = "id",value = "用户id",example = "14",required =true)
    private Integer id;

    @ApiModelProperty(name = "account",value = "账号",example = "zhougong",required =true)
    private String account;

    @ApiModelProperty(name = "password",value = "密码",example = "123456",required =true)
    private String password;

    @ApiModelProperty(name = "username",value = "昵称",example = "周工",required =true)
    private String username;

    @ApiModelProperty(name = "workStatus",value = "在岗状态",example = "离岗",required =true)
    private String workStatus;  //在岗状态 添加的时候默认是在岗的

    @ApiModelProperty(name = "phoneNumber",value = "电话号码",example = "15891262739",required =true)
    private String phoneNumber; //电话号码

    @ApiModelProperty(name = "workId",value = "工号",example = "njbgs009",required =true)
    private String workId; //工号

    @ApiModelProperty(name = "duty",value = "职务",example = "电工",required =true)
    private String duty; //职务


}
