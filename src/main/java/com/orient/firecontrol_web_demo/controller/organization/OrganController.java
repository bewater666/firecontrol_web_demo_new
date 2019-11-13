package com.orient.firecontrol_web_demo.controller.organization;

import com.orient.firecontrol_web_demo.config.aop.MyLog;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.organization.Organization;
import com.orient.firecontrol_web_demo.model.organization.OrganizationDto;
import com.orient.firecontrol_web_demo.service.organ.OrganService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/24 14:14
 * @func 单位管理接口
 */
@RestController
@RequestMapping("/organ")
public class OrganController {
    @Autowired
    private OrganService organService;


    /**
     * 超级管理员查看全部部门列表   单位管理只能查看自己的单位信息
     * @return
     */
    @GetMapping("/view")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "查看部门信息",notes = "超级管理员查看全部部门列表   单位管理只能查看自己的单位信息")
//    @MyLog(description = "部门列表")
    public ResultBean listAllOrgan(){
        return organService.organView();
    }


    /**
     * 添加单位信息 超级管理员权限
     * @param organization
     * @return
     */
    @PostMapping("/add")
    @RequiresRoles(value = {"superadmin"})
    @ApiOperation(value = "添加单位信息",notes = "超级管理员权限")
    @MyLog(description = "添加单位信息")
    public ResultBean addOrgan(@RequestBody @ApiParam(value = "传入json格式",name = "单位bean",required = true) Organization organization){
        return organService.addOrgan(organization);
    }


    /**
     * 更新单位信息(名称
     * superadmin admin权限
     * @param organizationDto
     * @return
     */
    @PutMapping("/update")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "更新单位信息",notes = "更新单位信息(名称)")
    @MyLog(description = "更新单位信息")
    public ResultBean updateOrgan(@RequestBody @ApiParam(name = "单位bean",value = "传入json格式",required = true) OrganizationDto organizationDto){
        return organService.updateOrgan(organizationDto);
    }


}
