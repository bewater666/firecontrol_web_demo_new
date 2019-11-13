package com.orient.firecontrol_web_demo.service.organ;

import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.dao.device.DeviceInfoDao;
import com.orient.firecontrol_web_demo.dao.organization.BuildingDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfo;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfoUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/25 17:23
 * @func
 */
@Service
public class BuildingService {
    @Autowired
    private BuildingDao buildingDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DeviceInfoDao deviceInfoDao;


    /**
     * 设备管理
     * 根据单位id查询  该单位下的设备列表
     * superadmin admin 权限
     * @param organId
     * @return
     */
    public ResultBean listBuild(Integer organId){
        List<BuildingInfo> byOrganId = buildingDao.findByOrganId(organId);
        if (byOrganId.size()==0){
            return new ResultBean(200, "查询成功,该单位下无建筑信息", null);
        }
        return new ResultBean(200, "查询成功", byOrganId);
    }


    /**
     * 新增建筑物  并绑定单元
     * 根据用户信息判定用户所处单位  给新建的建筑物进行和单位绑定  所以要考虑事务
     * 需要前端再传一个获得的单位id organId
     * superadmin admin权限
     * @param buildingInfo
     * @Param organId
     * @return
     */
    @Transactional
    public ResultBean addBuild(BuildingInfo buildingInfo,Integer organId){
        BuildingInfo byBuildCode = buildingDao.findByBuildCode(buildingInfo.getBuildCode());
        if (byBuildCode!=null){
            throw new CustomException("新增建筑物失败,该建筑物已存在(建筑物编号)");
        }
        BuildingInfo byBuildName = buildingDao.findByBuildName(buildingInfo.getBuildName());
        if (byBuildName!=null){
            throw new CustomException("新增建筑物失败,该建筑物已存在(建筑物名称)");
        }
        int i = buildingDao.addBuild(buildingInfo);
        if (i<=0){
            throw new CustomException("新增建筑物失败");
        }
        Integer id = buildingDao.findByBuildName(buildingInfo.getBuildName()).getId();
        int i1 = buildingDao.addOrgan_build(organId, id);
        if (i1<=0){
            throw new CustomException("新增建筑物失败");
        }
        return new ResultBean(200, "新增建筑物成功(New buildings achieved)", null);
    }


    /**
     * 更新建筑物信息
     * superadmin admin权限
     * @param buildingInfoUp
     * @return
     */
    public ResultBean updateBuild(BuildingInfoUp buildingInfoUp){
        BuildingInfo byId = buildingDao.findById(buildingInfoUp.getId());
        if (byId==null){
            throw new CustomException("该建筑不存在");
        }
        //当建筑物下绑定了设备后是不能修改buildCode的  因为改了会出问题 设备的编号和建筑物编号有关
        List<DeviceInfo> byBuildCode = deviceInfoDao.findByBuildCode(byId.getBuildCode());
        if (byBuildCode.size()!=0){//下面有绑定了设备 就不能修改建筑编号了
            if (!(buildingInfoUp.getBuildCode()).equals(byId.getBuildCode())){ //要修改的buildcode和原来的不一样不允许修改
                throw new CustomException("更新建筑信息失败(不可修改建筑物编号,该建筑物下绑定了设备)");
            }
            //buildCode不改只改建筑物名称 或和之前改成一样 那就允许修改
            int i = buildingDao.updateBuild(buildingInfoUp);
            if (i<=0){
                throw new CustomException("更新建筑物信息失败");
            }
            return new ResultBean(200, "更新建筑物信息成功", null);
        }
        //该建筑物下没有绑定设备  那就都允许修改了
        int i = buildingDao.updateBuild(buildingInfoUp);
        return new ResultBean(200, "更新建筑物信息成功", null);
    }



}
