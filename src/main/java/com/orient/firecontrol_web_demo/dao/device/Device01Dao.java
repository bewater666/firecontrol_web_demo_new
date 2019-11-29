package com.orient.firecontrol_web_demo.dao.device;

import com.orient.firecontrol_web_demo.model.device.Device01;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 14:35
 * @func 主控
 */
@Mapper
@Repository
public interface Device01Dao {

    void insertDevice01Measure(Device01 device01);


    Device01 findById(Integer id);

    /**
     * 根据设备编号deviceCode查看自己的监测数据  可能有很多很多条
     * @param deviceCode
     * @return
     */
    List<Device01> listByDeviceCode(String deviceCode);


    /**
     * 查看某设备最近7天的检测数据
     * @param deviceCode
     * @return
     */
    List<Device01> findLast7Days(String deviceCode);

    /**
     * 查看设备类型1 最近7天的A相电压
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysVoltageA(String deviceCode);


    /**
     * 查看设备类型1 最近7天的B相电压
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysVoltageB(String deviceCode);


    /**
     * 查看设备类型1 最近7天的C相电压
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysVoltageC(String deviceCode);



    /**
     * 查看设备类型1 最近7天的剩余电流
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysRemainElec(String deviceCode);


    /**
     * 查看设备类型1 最近7天的配电箱温度
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBoxTemp(String deviceCode);


    /**
     * 查看设备类型1 最近7天的监控时间
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysMeasureTime(String deviceCode);


}
