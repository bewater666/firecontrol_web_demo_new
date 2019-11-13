package com.orient.firecontrol_web_demo.dao.device;

import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 10:05
 * @func
 */
@Mapper
@Repository
public interface DeviceInfoDao {
    List<DeviceInfo> findAll();

    DeviceInfo findOne(String deviceCode);

    /**
     * 更改设备状态 看心跳包发过来的设备状态与数据库中存的是否一致 一致就不做处理 不一致就需要更新状态
     * @param statusFF
     * @param deviceCode
     */
    void updateDeviceStatus(@Param("statusFF") String statusFF, @Param("deviceCode") String deviceCode);

    /**
     * 根据建筑物编号查询 建筑所具有的设备列表
     * @param buildCode
     * @return
     */
    List<DeviceInfo> findByBuildCode(String buildCode);


    /**
     * 新增设备
     * @param deviceInfo
     * @return
     */
    int addDevice(DeviceInfo deviceInfo);

    /**
     * 查询某建筑下某楼层下的设备列表
     * @param buildCode
     * @param floorCode
     * @return
     */
    List<DeviceInfo> findByBuildCodeAndFloorCode(@Param("buildCode") String buildCode,@Param("floorCode") Integer floorCode);


}
