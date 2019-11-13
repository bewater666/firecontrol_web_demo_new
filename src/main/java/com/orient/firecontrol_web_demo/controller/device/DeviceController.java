package com.orient.firecontrol_web_demo.controller.device;

import com.orient.firecontrol_web_demo.config.aop.MyLog;
import com.orient.firecontrol_web_demo.config.page.PageBean;
import com.orient.firecontrol_web_demo.config.page.PageCommons;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.device.DeviceInfo;
import com.orient.firecontrol_web_demo.service.device.DeviceService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/28 14:10
 * @func
 */
@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;


    /**
     * 根据建筑物id  查询该建筑物下的设备列表
     * superadmin admin权限
     * 还是一样  只能查看自己单位下的建筑物下的设备列表
     * 因为前面进来这个接口  已经约束好了 是自己单位下的建筑物了  所以这个建筑物id不会是别的单位的   这里不再再约束了
     * @param buildId
     * @return
     */
    @GetMapping("view/{id}/{currentPage}/{pageSize}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "查询建筑物下的设备列表",notes = "根据建筑物id 查询该建筑物下的设备列表")
    @ApiImplicitParam(name = "id",value = "建筑物id",required = true,dataType = "int",paramType = "path")
//    @MyLog(description = "查询建筑物下设备")
    public ResultBean findByBuildId(@PathVariable("id") Integer buildId,
                                    @PathVariable("currentPage")@ApiParam(name = "currentPage",value = "当前页码",required = true) Integer currentPage,
                                    @PathVariable("pageSize")@ApiParam(name = "pageSize",value = "每页条数",required = true) Integer pageSize){
        PageBean<DeviceInfo> pageBean = deviceService.findByBuildId(currentPage, pageSize, buildId);
        List<DeviceInfo> items = pageBean.getItems();
        int thisPageNum = PageCommons.getThisPageNum(currentPage, pageSize, pageBean);
        Map map = new HashMap();
        map.put("currentPage",currentPage);
        map.put("thisPageNum",thisPageNum);
        map.put("totalNum", pageBean.getTotalNum());
        map.put("deviceList",items);
        if (items.size()==0){
            return  new ResultBean(200,"该建筑物下无设备信息",null);
        }
        return  new ResultBean(200,"查询成功",map);
    }


    /**
     * 新增设备  并绑定建筑物及楼层编号
     * 这里不需要建筑物id是因为  deviceInfo中有buildCode这个字段 且前端在进行添加时将这个值上下文获取 设置为固定灰白不可改
     * superadmin admin 权限
     * @param deviceInfo
     * @return
     */
    @ApiOperation(value = "新增设备",notes = "这里不需要建筑物id是因为  deviceInfo中有buildCode这个字段 且前端在进行添加时将这个值上下文获取 设置为固定灰白不可改")
    @PostMapping("/add")
    @MyLog(description = "新增设备")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean addDevice(
            @RequestBody @ApiParam(name = "设备bean",value = "传入json格式",required = true) DeviceInfo deviceInfo){
        return deviceService.addDevice(deviceInfo);
    }


    /**
     * 查询某建筑物下的某单位下的设备列表
     * 建筑物编号 有前端上下文获得
     * @param buildCode
     * @param floorCode
     * @return
     */
    @GetMapping("/listByFloorCode/{currentPage}/{pageSize}")
    @ApiOperation(value = "查询楼层下设备",notes = "查询某建筑物下的某单位下的设备列表")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean listByFloorCode(@RequestParam("buildCode")@ApiParam(name = "buildCode",value = "建筑物编号",required = true) String buildCode,
                                      @RequestParam("floorCode") @ApiParam(name = "floorCode",value = "楼层编号",required = true) Integer floorCode,
                                      @PathVariable("currentPage")@ApiParam(name = "currentPage",value = "当前页码",required = true) Integer currentPage,
                                      @PathVariable("pageSize")@ApiParam(name = "pageSize",value = "每页条数",required = true) Integer pageSize){
        PageBean<DeviceInfo> pageBean = deviceService.listByBuildCodeAndFloorCode(currentPage, pageSize, buildCode, floorCode);
        List<DeviceInfo> items = pageBean.getItems();
        int thisPageNum = PageCommons.getThisPageNum(currentPage, pageSize, pageBean);
        Map map = new HashMap();
        map.put("currentPage",currentPage);
        map.put("thisPageNum",thisPageNum);
        map.put("totalNum", pageBean.getTotalNum());
        map.put("deviceList",items);
        return  new ResultBean(200,"查询成功",map);
    }


    /**
     * 查询某个设备最新的一条监测数据
     * @param deviceCode    设备编号
     * @param deviceType    设备类型
     * 设备类型得传 因为每种设备检测的数据不一样  我数据库做了分类
     * 设备类型由前端  通过上下文获取
     * @return
     */
//    @ApiOperation(value = "查询某个设备最新的一条监测数据",notes = "查询某个设备最新的一条监测数据")
//    @GetMapping("/findNewMeasure")
//    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
//    public ResultBean findNewMeasure(@RequestParam @ApiParam(name = "deviceCode",value = "设备编号",required = true) String deviceCode,
//                                     @RequestParam@ApiParam(name = "deviceType",value = "设备类型",required = true) String deviceType){
//        return deviceService.findNewMeasure(deviceCode, deviceType);
//    }


    /**
     * 根据设备编号查询所有(历史)数据
     * 注意 这里根据id进行了倒序  所以第一条就是最新的监测数据
     * @param currentPage 页码
     * @param pageSize  每页数量
     * @param deviceCode    设备编号
     * @param deviceType    设备类型
     * @return
     */
    @ApiOperation(value = "查看某设备下的历史(全部)数据",notes = "查看某设备下的历史(全部)数据")
    @GetMapping("/findAllMeasure/{currentPage}/{pageSize}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean listAllMeasure(@RequestParam @ApiParam(name = "deviceCode",value = "设备编号",required = true) String deviceCode,
                                     @RequestParam@ApiParam(name = "deviceType",value = "设备类型",required = true) String deviceType,
                                     @PathVariable("currentPage")@ApiParam(name = "currentPage",value = "当前页码",required = true) Integer currentPage,
                                     @PathVariable("pageSize")@ApiParam(name = "pageSize",value = "每页条数",required = true) Integer pageSize){
        PageBean pageBean = deviceService.listAllMeasure(currentPage, pageSize, deviceCode, deviceType);
        List items = pageBean.getItems();
        int thisPageNum = PageCommons.getThisPageNum(currentPage, pageSize, pageBean);
        Map map = new HashMap();
        map.put("currentPage",currentPage);
        map.put("thisPageNum",thisPageNum);
        map.put("totalNum", pageBean.getTotalNum());
        map.put("measureList",items);
        return  new ResultBean(200,"查询成功",map);

    }

}
