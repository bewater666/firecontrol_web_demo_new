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
}
