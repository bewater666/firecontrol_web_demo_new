package com.orient.firecontrol_web_demo.dao.job;

import com.orient.firecontrol_web_demo.model.job.JobAndTrigger;
import com.orient.firecontrol_web_demo.model.job.JobDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/25 14:25
 * @func
 */
@Mapper
public interface JobAndTriggerMapper {
    List<JobAndTrigger> getJobAndTriggerDetails();

    JobAndTrigger getByJobClassNameAndJobGroupName(@Param("jobClassName") String jobClassName, @Param("jobGroupName") String jobGroupName);


    /**
     * 这里查询是根据不同账号来查的
     * 因为我无法自定义任务调度数据库表 所以我再新建任务的时间 JOB_GROUP字段 我往里面插内容的时候我根据单位-建筑物-楼层-任务名这么插入
     * 所以我这边可以根据需要来进行模糊查询(模糊查询可能不太好) 但是只要我们这个flag限定的好 应该不会有什么问题 先这样处理
     * @param flag
     * @return
     */
    List<JobAndTrigger> getByFlag(String flag);


    /**
     * 查看自定义的任务详情信息
     * @param jobClassName
     * @param jobGroupName
     * @return
     */
    JobDetail getJobDetail(@Param("jobClassName") String jobClassName, @Param("jobGroupName") String jobGroupName);


}