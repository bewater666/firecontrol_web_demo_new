package com.orient.firecontrol_web_demo.model.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 14:23
 * @func 三相子机bean  用于接受测量的数据值
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class Device03 {
    private Integer id;
    private String deviceCode;  //设备编号
    private String branchElecA;  //支路A相电流
    private String branchElecB;  //支路B相电流
    private String branchElecC;  //支路C相电流
    private String branchTempA;  //支路 A 相接头温度
    private String branchTempB;  //支路 B 相接头温度
    private String branchTempC;  //支路 C 相接头温度
    private String measureTime; //测量时间 这边接收硬件信息的时候就将收到数据的时间戳当成测量时间 拼接进去插入队列
}
