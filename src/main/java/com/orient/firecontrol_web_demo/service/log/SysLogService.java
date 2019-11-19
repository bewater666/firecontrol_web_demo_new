package com.orient.firecontrol_web_demo.service.log;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.page.PageUtils;
import com.orient.firecontrol_web_demo.dao.log.SysLogDao;
import com.orient.firecontrol_web_demo.model.log.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 15:42
 * @func
 */
@Service
public class SysLogService {
    @Autowired
    private SysLogDao sysLogDao;


    /**
     * 查询所有系统日志
     * @return
     */
    public PageInfo<SysLog> list(PageUtils pageUtils){
        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
            pageUtils.setPage(1);
            pageUtils.setRows(10);
        }
        PageHelper.startPage(pageUtils.getPage(),pageUtils.getRows());
        List<SysLog> all = sysLogDao.findAll();
        PageInfo<SysLog> pageInfo = new PageInfo<>(all);
        if (all.size()==0){
            throw new CustomException("当前无系统日志信息");
        }
        return pageInfo;
    }

}
