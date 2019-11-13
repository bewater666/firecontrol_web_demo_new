package com.orient.firecontrol_web_demo.dao.organization;

import com.orient.firecontrol_web_demo.model.organization.BuildingInfo;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfoUp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/25 16:52
 * @func
 */
@Mapper
@Repository
public interface BuildingDao {

    /**
     * 建筑管理
     * 查看某单位下的建筑列表
     * @param organId
     * @return
     */
    List<BuildingInfo> findByOrganId(Integer organId);


    /**
     * 根据建筑id 获得建筑信息
     * @param buildingId
     * @return
     */
    BuildingInfo findById(Integer buildingId);

    /**
     * 新增建筑信息  superadmin admin权限
     * @param buildingInfo
     * @return
     */
    int addBuild(BuildingInfo buildingInfo);

    /**
     * 新增建筑物和单位的绑定关系  新增建筑物时用到
     * @param organId
     * @param buildId
     * @return
     */
    int addOrgan_build(@Param("organId")Integer organId,@Param("buildId")Integer buildId);

    /**
     * 根据建筑物编号查询建筑物信息
     * @param buildCode
     * @return
     */
    BuildingInfo findByBuildCode(String buildCode);


    /**
     * 根据建筑物名称查询建筑物信息
     * @param buildName
     * @return
     */
    BuildingInfo findByBuildName(String buildName);


    /**
     * 查询所有的建筑物列表  我这里写是为了后面新增建筑物的时候 不好获得新增的buildingId  这里获得到所有的size  +1即可获得buildingId
     * 没有找到好的方法
     * @return
     */
    List<BuildingInfo> findAll();


    /**
     * 修改建筑物信息 建筑物编号原则上不允许修改 若修改了 会影响代码 编号和设备挂钩
     * @param buildingInfoUp
     * @return
     */
    int updateBuild(BuildingInfoUp buildingInfoUp);

}
