package com.orient.firecontrol_web_demo.dao.organization;

import com.orient.firecontrol_web_demo.model.organization.FloorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/5 16:24
 * @func
 */
@Mapper
@Repository
public interface FloorDao {

    /**
     * 查询某建筑下的楼层列表
     * @param buildCode
     * @return
     */
    List<FloorInfo> listByBuildCode(String buildCode);

    /**
     * 根据建筑物编号 及楼层编号 查询 用于判断该楼层在某一建筑物下是否存在
     * @param buildCode
     * @param floorCode
     * @return
     */
    FloorInfo floorIsRight(@Param("buildCode") String buildCode, @Param("floorCode")Integer floorCode);


    /**
     * 新增楼层
     * @param floorInfo
     * @return
     */
    int addFloor(FloorInfo floorInfo);


}
