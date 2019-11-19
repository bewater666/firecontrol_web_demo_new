package com.orient.firecontrol_web_demo.controller.sysLog;

import com.github.pagehelper.PageInfo;
import com.orient.firecontrol_web_demo.config.page.PageUtils;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.log.SysLog;
import com.orient.firecontrol_web_demo.service.log.SysLogService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 15:45
 * @func
 */
@RestController
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;


    /**
     * 查看系统操作日志  登录即可访问
     * @return
     */
    @GetMapping("/view")
    @ApiOperation(value = "日志管理",notes = "查看系统操作日志,登录即可访问")
    @RequiresAuthentication
    public ResultBean list(@ModelAttribute @Validated PageUtils pageUtils){
        PageInfo<SysLog> pageInfo = sysLogService.list(pageUtils);
        Map<String,Object> map = new HashMap<>();
        map.put("currentPage", pageUtils.getPage());
        map.put("thisPageNum", pageInfo.getSize());
        map.put("logList", pageInfo.getList());
        map.put("totalNum", pageInfo.getTotal());
        return  new ResultBean(200,"查询成功",map);

    }
}
