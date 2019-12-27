package com.orient.firecontrol_web_demo.dao.job;

import com.orient.firecontrol_web_demo.model.job.JobAndTrigger;
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

    JobAndTrigger getByJobClassNameAndJobGroupName(@Param("jobClassName")String jobClassName,@Param("jobGroupName")String jobGroupName);


}
