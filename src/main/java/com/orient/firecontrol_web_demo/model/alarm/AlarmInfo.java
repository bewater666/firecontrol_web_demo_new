package com.orient.firecontrol_web_demo.model.alarm;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 15:45
 * @func 告警信息bean
 */
@Data
@Repository
@Accessors(chain = true)
public class AlarmInfo {
    private Integer id;
    private String deviceCode;  //设备id
    private String alarmTime;   //告警时间
    private String alarmDetail;    //告警内容
    private String alarmGrade;      //告警级别
    private String isHandler;   //是否处理 已处理 未处理
    private Integer organId;    //单位id  当硬件传来报警信息中的建筑物编号在数据库中不存在 直接设置单位编号为0
}
