package com.orient.firecontrol_web_demo.service.organ;

import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.dao.device.DeviceInfoDao;
import com.orient.firecontrol_web_demo.dao.organization.BuildingDao;
import com.orient.firecontrol_web_demo.dao.organization.FloorDao;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfo;
import com.orient.firecontrol_web_demo.model.organization.FloorDto;
import com.orient.firecontrol_web_demo.model.organization.FloorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/5 16:53
 * @func
 */
@Service
public class FloorService {
    @Autowired
    private FloorDao floorDao;
    @Autowired
    private BuildingDao buildingDao;
    @Autowired
    private DeviceInfoDao deviceInfoDao;


    /**
     * 查询某建筑物下的楼层列表 并判定各楼层状态
     * @param buildCode
     * @return
     */
    public ResultBean listByBuildCode(String buildCode){
        BuildingInfo byBuildCode = buildingDao.findByBuildCode(buildCode);
        if (byBuildCode==null){
            throw new CustomException("建筑物编号不存在");
        }
        List<FloorInfo> floorInfos = floorDao.listByBuildCode(buildCode);
        if (floorInfos.size()==0){
            return new ResultBean(200, "当前建筑下无楼层信息", null);
        }
        List<FloorDto> floorDtoList = new ArrayList<>();
        // TODO: 2019/11/6  这里代码处理的不是很好 存在两层for循环 可能会导致响应速度变慢 为了查询所有楼层的状态 有待改进
        for (FloorInfo floorInfo:
        floorInfos) {
            //查询该建筑物下 某一楼层的设备列表
            List<DeviceInfo> byBuildCodeAndFloorCode = deviceInfoDao.findByBuildCodeAndFloorCode(buildCode, floorInfo.getFloorCode());
            Integer floorStatus=1;
            FloorDto floorDto = new FloorDto();
            for (DeviceInfo deviceInfo:
            byBuildCodeAndFloorCode) {
                if (deviceInfo.getStatus().equals("离线")){ //若某一楼层下的设备状态存在离线的 则该楼层状态为0
                    floorStatus = 0;
                }
            }
            floorDto.setId(floorInfo.getId());
            floorDto.setBuildCode(buildCode);
            floorDto.setFloorCode(floorInfo.getFloorCode());
            floorDto.setFloorName(floorInfo.getFloorName());
            floorDto.setFloorStatus(floorStatus);
            floorDtoList.add(floorDto);
        }
        return new ResultBean(200, "查询楼层列表成功", floorDtoList);
    }


    /**
     * 给某建筑新增楼层  floorInfo有buildCode字段 根据这个绑定在一起
     * @param floorInfo
     * @return
     */
    public ResultBean addFloor(FloorInfo floorInfo){
        String buildCode = floorInfo.getBuildCode();
        BuildingInfo byBuildCode = buildingDao.findByBuildCode(buildCode);
        if (byBuildCode==null){
            throw new CustomException("该建筑编号不存在");
        }
        Integer floorCode = floorInfo.getFloorCode();
        //这里若floorCode为5  那楼层名称FloorName 只能是五楼 这是规范  所以楼层信息以后用户也省去了修改的麻烦
        if (!(floorCode+"楼").equals(floorInfo.getFloorName())){
            throw new CustomException("新增楼层名称不符合规范,请重新输入");
        }
        FloorInfo floorInfo1 = floorDao.floorIsRight(buildCode, floorCode);
        if (floorInfo1!=null){
            throw new CustomException("该建筑已经存在"+floorCode+"楼");
        }

        int i = floorDao.addFloor(floorInfo);
        if (i<=0){
            throw new CustomException("新增楼层失败");
        }
        return new ResultBean(200, "新增楼层成功", null);
    }
}
