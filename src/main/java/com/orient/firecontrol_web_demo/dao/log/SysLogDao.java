package com.orient.firecontrol_web_demo.dao.log;

import com.orient.firecontrol_web_demo.model.log.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 10:37
 * @func
 */
@Mapper
@Repository
public interface SysLogDao {
    /**
     * 添加日志记录
     * @param sysLog
     */
    void addLog(SysLog sysLog);

    /**
     * 查询所有日志列表 前端分页处理
     * @return
     */
    List<SysLog> findAll();
}
