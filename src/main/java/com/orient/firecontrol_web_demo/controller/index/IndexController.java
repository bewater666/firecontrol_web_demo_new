package com.orient.firecontrol_web_demo.controller.index;

import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.dao.alarm.AlarmDao;
import com.orient.firecontrol_web_demo.dao.device.DeviceInfoDao;
import com.orient.firecontrol_web_demo.dao.organization.BuildingDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/19 14:00
 * @func 用于前端首页  各项数据统计
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private BuildingDao buildingDao;


    /**
     * 用于前端首页图表各项数据统计
     * @return
     */
    @GetMapping("/count")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "首页数据统计接口",notes = "首页数据统计接口")
    public ResultBean count(){
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前用户的部门id organId
        Integer organId = userDao.findOneByAccount(account).getOrganId();
        Map<String,Object> map = new HashMap<>();
        if (organId==0){//超级管理员
            //统计全部单位各级告警数目
            String alarmGrade0 = "0级告警";
            int alarmNum0 = alarmDao.countNum(alarmGrade0);
            String alarmGrade1 = "1级告警";
            int alarmNum1 = alarmDao.countNum(alarmGrade1);
            String alarmGrade2 = "2级告警";
            int alarmNum2 = alarmDao.countNum(alarmGrade2);
            String alarmGrade3 = "3级告警";
            int alarmNum3 = alarmDao.countNum(alarmGrade3);
            //统计全部单位各设备类型的数量
            int deviceNum1 = deviceInfoDao.countByDeviceType("01");
            int deviceNum2 = deviceInfoDao.countByDeviceType("02");
            int deviceNum3 = deviceInfoDao.countByDeviceType("03");
            //统计全部单位离线的各设备类型的数量
            int offLinedeviceNum1 = deviceInfoDao.countByDeviceTypeAndStatus("01");
            int offLinedeviceNum2 = deviceInfoDao.countByDeviceTypeAndStatus("02");
            int offLinedeviceNum3 = deviceInfoDao.countByDeviceTypeAndStatus("03");
            //统计全部单位告警处理情况
            int alarmHasDone = alarmDao.countHasHandler();
            int alarmUnDone = alarmDao.countUnHandler();
            int alarmDoneBad = alarmDao.countHandlerBad();
            map.put("alarmNum0",alarmNum0 );
            map.put("alarmNum1",alarmNum1 );
            map.put("alarmNum2", alarmNum2);
            map.put("alarmNum3", alarmNum3);
            map.put("deviceNum1",deviceNum1 );
            map.put("deviceNum2",deviceNum2 );
            map.put("deviceNum3", deviceNum3);
            map.put("offLinedeviceNum1", offLinedeviceNum1);
            map.put("offLinedeviceNum2", offLinedeviceNum2);
            map.put("offLinedeviceNum3", offLinedeviceNum3);
            map.put("alarmHasDone", alarmHasDone);
            map.put("alarmUnDone", alarmUnDone);
            map.put("alarmDoneBad", alarmDoneBad);
            return new ResultBean(200, "统计成功", map);
        }
        //若是单位领导
        //查看该单位下的建筑物列表
        List<BuildingInfo> byOrganId = buildingDao.findByOrganId(organId);
        //统计该单位下各级告警数目
        String alarmGrade0 = "0级告警";
        int alarmNum0 = alarmDao.countOrganNum(alarmGrade0,organId);
        String alarmGrade1 = "1级告警";
        int alarmNum1 = alarmDao.countOrganNum(alarmGrade1,organId);
        String alarmGrade2 = "2级告警";
        int alarmNum2 = alarmDao.countOrganNum(alarmGrade2,organId);
        String alarmGrade3 = "3级告警";
        int alarmNum3 = alarmDao.countOrganNum(alarmGrade3,organId);
        //统计该单位下各设备类型的数量
        int deviceNum1 = 0;
        int deviceNum2 = 0;
        int deviceNum3 = 0;
        for (BuildingInfo buildingInfo:
             byOrganId) {
            //这是一个建筑物下的 注意多个建筑物要累加
            int i1 = deviceInfoDao.countByDeviceTypeAndOrgan("01", buildingInfo.getBuildCode());
            int i2 = deviceInfoDao.countByDeviceTypeAndOrgan("02", buildingInfo.getBuildCode());
            int i3 = deviceInfoDao.countByDeviceTypeAndOrgan("03", buildingInfo.getBuildCode());
            deviceNum1 = deviceNum1+i1;
            deviceNum2 = deviceNum2+i2;
            deviceNum3 = deviceNum3+i3;
        }
        //统计该单位下离线的各设备类型的数量
        int offLinedeviceNum1 = 0;
        int offLinedeviceNum2 = 0;
        int offLinedeviceNum3 = 0;
        for (BuildingInfo buildingInfo:
             byOrganId) {
            //这是一个建筑物下的 注意多个建筑物要累加
            int i1 = deviceInfoDao.countByDeviceTypeAndStatusAndOrgan("01", buildingInfo.getBuildCode());
            int i2 = deviceInfoDao.countByDeviceTypeAndStatusAndOrgan("02", buildingInfo.getBuildCode());
            int i3 = deviceInfoDao.countByDeviceTypeAndStatusAndOrgan("03", buildingInfo.getBuildCode());
            offLinedeviceNum1 = offLinedeviceNum1 +i1;
            offLinedeviceNum2 = offLinedeviceNum2+i2;
            offLinedeviceNum3 = offLinedeviceNum3+i3;
        }
        //统计全部单位告警处理情况
        int alarmHasDone = alarmDao.countOrganHasHandler(organId);
        int alarmUnDone = alarmDao.countOrganUnHandler(organId);
        int alarmDoneBad = alarmDao.countOrganHandlerBad(organId);
        map.put("alarmNum0",alarmNum0 );
        map.put("alarmNum1",alarmNum1 );
        map.put("alarmNum2", alarmNum2);
        map.put("alarmNum3", alarmNum3);
        map.put("deviceNum1",deviceNum1 );
        map.put("deviceNum2",deviceNum2 );
        map.put("deviceNum3", deviceNum3);
        map.put("offLinedeviceNum1", offLinedeviceNum1);
        map.put("offLinedeviceNum2", offLinedeviceNum2);
        map.put("offLinedeviceNum3", offLinedeviceNum3);
        map.put("alarmHasDone", alarmHasDone);
        map.put("alarmUnDone", alarmUnDone);
        map.put("alarmDoneBad", alarmDoneBad);
        return new ResultBean(200, "统计成功", map);
    }
}
