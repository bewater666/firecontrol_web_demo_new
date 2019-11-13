package com.orient.firecontrol_web_demo.service.log;

import com.github.pagehelper.PageHelper;
import com.orient.firecontrol_web_demo.config.page.PageBean;
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
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageBean<SysLog> list(Integer currentPage, Integer pageSize){
        PageHelper.startPage(currentPage,pageSize);
        List<SysLog> all = sysLogDao.findAll();
        PageBean<SysLog> pageBean = new PageBean<>(currentPage, pageSize, sysLogDao.findAll().size());
        pageBean.setItems(all);
        return pageBean;
    }

}
