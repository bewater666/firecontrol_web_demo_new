package com.orient.firecontrol_web_demo.model.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 10:01
 * @func  设备名称不重要后期用户可以自己设置  重要的是设备类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfo {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "建筑物编码",name="buildCode",required = true,example = "1111111111")
    private String buildCode;

    @ApiModelProperty(hidden = true)
    private String buildName;

    @ApiModelProperty(value = "设备编号",name="deviceCode",required = true,example = "1111111111112200")
    private String deviceCode;  //设备编号

    @ApiModelProperty(value = "设备类型",name="deviceType",required = true,example = "00")
    private String deviceType;  //设备类型  01主控  02单相子机 03三相子机

    @ApiModelProperty(hidden = true)
    private String typeName;    //设备类型名称

    @ApiModelProperty(value = "设备名称",name="deviceName",required = true,example = "测试设备3")
    private String deviceName;  //设备名称

    @ApiModelProperty(hidden = true)
    private String status; //设备状态 在线与否  靠心跳包判定

    @ApiModelProperty(value = "所处的楼层id",name="floorCode",required = true,example = "5")
    private Integer floorCode;    //设备所处楼层id 如floorId=5 南京办公室5楼下的某设备信息
}
