package com.orient.firecontrol_web_demo.service.alarm;

import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.dao.alarm.AlarmDao;
import com.orient.firecontrol_web_demo.dao.device.DeviceInfoDao;
import com.orient.firecontrol_web_demo.dao.organization.BuildingDao;
import com.orient.firecontrol_web_demo.dao.organization.OrganDao;
import com.orient.firecontrol_web_demo.dao.user.RoleDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.model.alarm.AlarmInfo;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import com.orient.firecontrol_web_demo.model.user.Role;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 17:27
 * @func
 */
@Service
public class AlarmService {
    @Autowired
    private AlarmDao alarmDao;
    @Autowired
    private DeviceInfoDao deviceInfoDao;
    @Autowired
    private OrganDao organDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private BuildingDao buildingDao;

    /**
     * 查看告警信息列表
     * 各单位只能看各单位的下的告警信息
     * 超级管理员查看所有
     * @return
     */
    public ResultBean list(){
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        List<Role> byUser = roleDao.findByUser(account);
        for (Role role:
        byUser) {
            String roleName = role.getRoleName();
            if (roleName.equals("superadmin")){ //若是超级管理 该接口就是查看所有单位下的告警列表
                List<AlarmInfo> all = alarmDao.findAll();
                if (all.size()==0){
                    return new ResultBean(200, "查询成功,所有单位均无告警信息", null);
                }
                return new ResultBean(200, "查询所有单位告警信息成功", all);
            }
            if (roleName.equals("admin")){ //若是单位领导 则是查看自己单位下的告警列表
                Integer organId = organDao.findByAccount(account).getId();
                List<AlarmInfo> byOrganId = alarmDao.findByOrganId(organId);
                if (byOrganId.size()==0){
                    return new ResultBean(200, "查询成功,该单位下无告警信息", null);
                }
                return new ResultBean(200, "查询单位告警信息成功", byOrganId);
            }
            throw new CustomException("查询失败");
        }
        return null;
    }

    /**
     * 查看已处理的告警列表
     * 超级管理员使用该接口查看全部已处理的告警信息
     * 部门领导使用该接口  查看该部门下已处理的告警信息
     * 我在这里只允许superadmin admin用户使用  所以只需判断organId是否为0即可 为0是superadmin 其他为部门领导
     * @return
     */
    public ResultBean findHasHandler(){
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前用户的部门id organId
        Integer organId = userDao.findOneByAccount(account).getOrganId();
        if (organId==0){//superadmin
            List<AlarmInfo> hasHandler = alarmDao.findHasHandler();
            if (hasHandler.size()==0){
                return new ResultBean(200, "当前无已处理的告警信息", null);
            }
            return new ResultBean(200, "查询全部已处理告警列表成功", hasHandler);
        }
        List<AlarmInfo> organHasHandler = alarmDao.findOrganHasHandler(organId);
        if (organHasHandler.size()==0){
            return new ResultBean(200, "该部门下已处理的告警列表为空", null);
        }
        return new ResultBean(200, "查询该部门已处理告警列表成功", organHasHandler);
    }

    /**
     * 查询 处理失败的告警列表
     * 逻辑同上 和查询已处理列表一样
     * @return
     */
    public ResultBean findHandlerBad(){
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前用户的部门id organId
        Integer organId = userDao.findOneByAccount(account).getOrganId();
        if (organId==0){//superadmin
            List<AlarmInfo> handlerBad = alarmDao.findHandlerBad();
            if (handlerBad.size()==0){
                return new ResultBean(200, "当前无处理失败的告警信息", null);
            }
            return new ResultBean(200, "查询全部的处理失败的告警列表成功", handlerBad);
        }
        List<AlarmInfo> organHandlerBad = alarmDao.findOrganHandlerBad(organId);
        if (organHandlerBad.size()==0){
            return new ResultBean(200, "该部门无处理失败的告警信息", null);
        }
        return new ResultBean(200, "查询该部门下处理失败告警列表成功", organHandlerBad);
    }

    /**
     * 查询未处理的告警信息列表
     * 逻辑同上
     * @return
     */
    public ResultBean findUnHandler(){
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前用户的部门id organId
        Integer organId = userDao.findOneByAccount(account).getOrganId();
        if (organId==0){ //superadmin
            List<AlarmInfo> unHandler = alarmDao.findUnHandler();
            if (unHandler.size()==0){
                return new ResultBean(200, "当前无未处理的告警信息", null);
            }
            return new ResultBean(200, "查询全部未处理的告警列表成功", unHandler);
        }
        List<AlarmInfo> organUnHandler = alarmDao.findOrganUnHandler(organId);
        if (organUnHandler.size()==0){
            return new ResultBean(200, "该部门下当前无未处理的告警信息", null);
        }
        return new ResultBean(200, "查询该部门未处理的告警列表成功", organUnHandler);
    }

    /**
     * 根据设备编号 查询该设备下的告警信息
     * superadmin admin权限
     * 超级管理员可以任意查看
     * 单位领导只可以看自己部门下的设备告警信息
     * 将当前用户的organId和设备所属的部门id比对  一致才可访问
     * @param deviceCode
     * @return
     */
    public ResultBean findByDeviceCode(String deviceCode){
        DeviceInfo one = deviceInfoDao.findOne(deviceCode);
        if (one==null){
            throw new CustomException("该设备编号不存在,请确认后再输入");
        }
        //根据设备信息获得建筑物编号
        String buildCode = one.getBuildCode();
        //获得建筑物id
        Integer buildId = buildingDao.findByBuildCode(buildCode).getId();
        //根据建筑物获得部门id
        int byBuildId = organDao.findByBuildId(buildId);
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前用户的部门id organId
        Integer organId = userDao.findOneByAccount(account).getOrganId();
        if (organId==0){ //superadmin
            List<AlarmInfo> byDeviceCode = alarmDao.findByDeviceCode(deviceCode);
            if (byDeviceCode.size()==0){
                return new ResultBean(200, "该设备暂无告警信息", null);
            }
            return new ResultBean(200, "查询成功", byDeviceCode);
        }
        if (organId==byBuildId){ //部门领导身份 且部门id匹配
            List<AlarmInfo> byDeviceCode = alarmDao.findByDeviceCode(deviceCode);
            if (byDeviceCode.size()==0){
                return new ResultBean(200, "该设备暂无告警信息", null);
            }
            return new ResultBean(200, "查询成功", byDeviceCode);
        }
        //部门领导身份 但organId不匹配
        throw new CustomException("抱歉,该设备下的告警信息不属于您管辖的范围,无权访问");
    }


    /**
     * 统计各级告警数目
     * 超级管理员直接根据等级统级个数
     * 单位领导需传入部门id
     * 同样只允许superadmin admin访问
     * @param grade
     * @return
     */
    public ResultBean countAlarmGrade(Integer grade){
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前用户的部门id organId
        Integer organId = userDao.findOneByAccount(account).getOrganId();
        String alarmGrade = "";
        if (organId==0){//superadmin 不需传organId
            if (grade==0){
                alarmGrade = "0级告警";
                int i = alarmDao.countNum(alarmGrade);
                return new ResultBean(200, "全部的0级告警统计成功", i);
            }
            if (grade==1){
                alarmGrade = "1级告警";
                int i = alarmDao.countNum(alarmGrade);
                return new ResultBean(200, "全部的1级告警统计成功", i);
            }
            if (grade==2){
                alarmGrade = "2级告警";
                int i = alarmDao.countNum(alarmGrade);
                return new ResultBean(200, "全部的2级告警统计成功", i);
            }
            if (grade==3){
                alarmGrade = "3级告警";
                int i = alarmDao.countNum(alarmGrade);
                return new ResultBean(200, "全部的3级告警统计成功", i);
            }
            throw new CustomException("输入告警级别有误,请重新输入");
        }
        if (grade==0){
            alarmGrade = "0级告警";
            int i = alarmDao.countOrganNum(alarmGrade,organId);
            return new ResultBean(200, "该部门下的0级告警统计成功", i);
        }
        if (grade==1){
            alarmGrade = "1级告警";
            int i = alarmDao.countOrganNum(alarmGrade,organId);
            return new ResultBean(200, "该部门下的1级告警统计成功", i);
        }
        if (grade==2){
            alarmGrade = "2级告警";
            int i = alarmDao.countOrganNum(alarmGrade,organId);
            return new ResultBean(200, "该部门下的2级告警统计成功", i);
        }
        if (grade==3){
            alarmGrade = "3级告警";
            int i = alarmDao.countOrganNum(alarmGrade,organId);
            return new ResultBean(200, "该部门下的3级告警统计成功", i);
        }
        throw new CustomException("输入告警级别有误,请重新输入");
    }
}
