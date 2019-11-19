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


    /**
     * 统计全部单位各设备类型的数量
     * 超级管理员使用  统计全部单位下的
     * @param deviceType
     * @return
     */
    int countByDeviceType(String deviceType);

    /**
     * 统计全部单位离线的各设备类型的数量
     * 超级管理员使用
     * @param deviceType
     * @return
     */
    int countByDeviceTypeAndStatus(String deviceType);

    /**
     * 单位领导统计该单位下各设备类型的数量
     * @param deviceType
     * @param buildCode
     * @return
     */
    int countByDeviceTypeAndOrgan(@Param("deviceType") String deviceType,@Param("buildCode") String buildCode);

    /**
     * 单位领导统计该单位下离线的各设备类型的数量
     * @param deviceType
     * @param buildCode
     * @return
     */
    int countByDeviceTypeAndStatusAndOrgan(@Param("deviceType") String deviceType,@Param("buildCode") String buildCode);
}
