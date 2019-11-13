package com.orient.firecontrol_web_demo.dao.device;

import com.orient.firecontrol_web_demo.model.device.Device02;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 14:37
 * @func 单相子机
 */
@Mapper
@Repository
public interface Device02Dao {
    void insertDevice02Measure(Device02 device02);

    /**
     * 根据设备编号deviceCode查看自己的监测数据  可能有很多很多条
     * @param deviceCode
     * @return
     */
    List<Device02> listByDeviceCode(String deviceCode);
}
