package com.orient.firecontrol_web_demo.model.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 14:17
 * @func 主控设备bean  用于接受测量的数据值
 * 为什么要用device1 device2 device3  三个类来封装呢  因为考虑到三种设备字段不一样 测量数据类型有差别
 * 所以用三个比较合适  查询的时候判断一下设备的类别然后进各自的表查询即可
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class Device01 {
    private Integer id;
    private String deviceCode;  //设备编号
    private String voltageA; //A相电压
    private String voltageB; //B相电压
    private String voltageC;  //C相电压
    private String remainElec;  //剩余电流
    private String boxTemp;  //配电箱环境温度
    private String measureTime; //测量时间 这边接收硬件信息的时候就将收到数据的时间戳当成测量时间 拼接进去插入队列
}
