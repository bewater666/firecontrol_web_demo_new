package com.orient.firecontrol_web_demo.controller.sysLog;

import com.orient.firecontrol_web_demo.config.page.PageBean;
import com.orient.firecontrol_web_demo.config.page.PageCommons;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.log.SysLog;
import com.orient.firecontrol_web_demo.service.log.SysLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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
    @GetMapping("/view/{currentPage}/{pageSize}")
    @ApiOperation(value = "日志管理",notes = "查看系统操作日志,登录即可访问")
    @RequiresAuthentication
    public ResultBean list(@PathVariable("currentPage")@ApiParam(name = "currentPage",value = "当前页码",required = true) Integer currentPage,
                           @PathVariable("pageSize")@ApiParam(name = "pageSize",value = "每页条数",required = true) Integer pageSize){
        PageBean<SysLog> pageBean = sysLogService.list(currentPage, pageSize);
        List<SysLog> items = pageBean.getItems();
        int thisPageNum = PageCommons.getThisPageNum(currentPage, pageSize, pageBean);
        Map map = new HashMap();
        map.put("currentPage",currentPage);
        map.put("thisPageNum",thisPageNum);
        map.put("totalNum", pageBean.getTotalNum());
        map.put("sysList",items);
        if (items.size()==0){
            return  new ResultBean(200,"当前无操作日志",null);
        }
        return  new ResultBean(200,"查询成功",map);
    }
}
