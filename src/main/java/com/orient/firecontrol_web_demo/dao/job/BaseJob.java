package com.orient.firecontrol_web_demo.dao.job;

import org.apache.ibatis.annotations.Mapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/26 16:27
 * @func
 */
@Mapper
public interface BaseJob extends Job {

    @Override
    void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException;
}
