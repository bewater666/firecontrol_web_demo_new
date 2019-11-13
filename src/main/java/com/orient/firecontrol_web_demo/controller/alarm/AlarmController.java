package com.orient.firecontrol_web_demo.controller.alarm;

import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.service.alarm.AlarmService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 17:31
 * @func
 */
@RestController
@RequestMapping("/alarm")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    /**
     * 查看告警信息列表
     * 各单位只能看各单位的下的告警信息
     * 超级管理员查看所有
     * @return
     */
    @ApiOperation(value = "告警信息列表",notes = "告警信息列表,需登录查看,超级管理员查看所有,单位管理员查看自己单位下的告警信息")
    @GetMapping("/view")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean list(){
        return alarmService.list();
    }

    /**
     * 查看已处理的告警列表
     * 超级管理员使用该接口查看全部已处理的告警信息
     * 部门领导使用该接口  查看该部门下已处理的告警信息
     * 我在这里只允许superadmin admin用户使用  所以只需判断organId是否为0即可 为0是superadmin 其他为部门领导
     * @return
     */
    @ApiOperation(value = "已处理告警列表",notes = "查看已处理告警信息列表,需登录查看")
    @GetMapping("/findHasHandler")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean findHasHandler(){
        return alarmService.findHasHandler();
    }


    /**
     * 查询 处理失败的告警列表
     * 逻辑同上 和查询已处理列表一样
     * @return
     */
    @ApiOperation(value = "处理失败的告警列表",notes = "查看处理失败的告警列表,需登录查看")
    @GetMapping("/findHandlerBad")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean findHandlerBad(){
        return alarmService.findHandlerBad();
    }

    /**
     * 查询未处理的告警信息列表
     * 逻辑同上
     * @return
     */
    @ApiOperation(value = "未处理的告警列表",notes = "查询未处理的告警信息列表接口 需登录查看")
    @GetMapping("/findUnHandler")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean findUnHandler(){
        return alarmService.findUnHandler();
    }


    /**
     * 根据设备编号查询该设备下的告警信息
     * @param deviceCode
     * @return
     */
    @ApiOperation(value = "查询设备告警",notes = "根据设备编号查询该设备下的告警信息")
    @GetMapping("/findByDeviceCode")
    @RequiresAuthentication
    public ResultBean findByDeviceCode(@RequestParam @ApiParam(name = "deviceCode",value = "设备编号") String deviceCode){
        return alarmService.findByDeviceCode(deviceCode);
    }

    /**
     * 统计各级告警数目
     * 超级管理员直接根据等级统级个数
     * 单位领导需传入部门id
     * 同样只允许superadmin admin访问
     * @param grade
     * @return
     */
    @ApiOperation(value = "统计各级告警数目",notes = "统计各级告警数目接口,0代表0级告警...3代表3级告警")
    @GetMapping("/count/{grade}")
    @RequiresAuthentication
    public ResultBean countAlarm(@PathVariable("grade") @ApiParam(name = "grade",value = "告警级别") Integer grade){
        return alarmService.countAlarmGrade(grade);
    }
}
