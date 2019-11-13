package com.orient.firecontrol_web_demo.controller.organization;

import com.orient.firecontrol_web_demo.config.aop.MyLog;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfo;
import com.orient.firecontrol_web_demo.model.organization.BuildingInfoUp;
import com.orient.firecontrol_web_demo.service.organ.BuildingService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/25 17:28
 * @func
 */
@RestController
@RequestMapping("/building")
public class BuildingController {
    @Autowired
    private BuildingService buildingService;


    /**
     * 建筑管理接口  单位admin 和superadmin都是查看单位下的建筑列表(因为这次操作是点击某单位下的建筑列表按钮 所以都是查这个单位下的)
     *
     * @param organId
     * @return
     */
    @GetMapping("/view/{organId}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "建筑管理接口",notes = "单位admin 和superadmin都是查看单位下的建筑列表(因为这次操作是点击某单位下的建筑列表按钮 所以都是查这个单位下的)")
    @ApiImplicitParam(name="organId",value = "单位id",dataType = "int",required = true,paramType = "path")
//    @MyLog(description = "建筑管理")
    public ResultBean listBulid(@PathVariable("organId") Integer organId){
        return buildingService.listBuild(organId);
    }


    /**
     * 新增建筑物信息
     * superadmin admin权限
     * @param buildingInfo
     * @param organId
     * @return
     */
    @PostMapping("/add/{id}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "新增建筑物信息",notes = "需要从上下文中获取单位id 传入路径中 ")
    @MyLog(description = "新增建筑物")
    public ResultBean addBuild(@RequestBody @ApiParam(name = "建筑物bean",value = "传入json格式",required = true) BuildingInfo buildingInfo,
                               @PathVariable("id") @ApiParam(name = "id",value = "单位id",required = true) Integer organId){
        return buildingService.addBuild(buildingInfo, organId);
    }

    /**
     * 更新建筑物信息
     * @param buildingInfoUp
     * @return
     */
    @PostMapping("/update")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "更新建筑物信息",notes = "建筑物下绑定了设备后是不能修改buildCode的  因为改了会出问题 设备的编号和建筑物编号有关")
    @MyLog(description = "更新建筑物信息")
    public ResultBean updateBuild(@RequestBody @ApiParam(name = "建筑物bean",value = "传入json格式",required = true) BuildingInfoUp buildingInfoUp){
        return buildingService.updateBuild(buildingInfoUp);
    }

}
