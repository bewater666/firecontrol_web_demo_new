package com.orient.firecontrol_web_demo.dao.device;

import com.orient.firecontrol_web_demo.model.device.Device03;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 14:37
 * @func 三相子机
 */
@Mapper
@Repository
public interface Device03Dao {
    void insertDevice03Measure(Device03 device03);

    /**
     * 根据设备编号deviceCode查看自己的监测数据  可能有很多很多条
     * @param deviceCode
     * @return
     */
    List<Device03> listByDeviceCode(String deviceCode);


    /**
     * 查看某设备最近7天的检测数据
     * @param deviceCode
     * @return
     */
    List<Device03> findLast7Days(String deviceCode);

    /**
     * 查看设备类型3最近7天的A相支路电流监测数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBranchElecA(String deviceCode);


    /**
     * 查看设备类型3最近7天的B相支路电流监测数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBranchElecB(String deviceCode);


    /**
     * 查看设备类型3最近7天的C相支路电流监测数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBranchElecC(String deviceCode);

    /**
     * 查看设备类型3最近7天的A相支路接头温度监测数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBranchTempA(String deviceCode);

    /**
     * 查看设备类型3最近7天的A相支路接头温度监测数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBranchTempB(String deviceCode);


    /**
     * 查看设备类型3最近7天的A相支路接头温度监测数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysBranchTempC(String deviceCode);


    /**
     * 查看设备类型3最近7天的监测时间数据
     * @param deviceCode
     * @return
     */
    List<Object> findLast7DaysMeasureTime(String deviceCode);
}
