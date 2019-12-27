package com.orient.firecontrol_web_demo.controller.job;

import com.github.pagehelper.PageInfo;
import com.orient.firecontrol_web_demo.config.page.PageUtils;
import com.orient.firecontrol_web_demo.dao.job.BaseJob;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.job.JobAndTrigger;
import com.orient.firecontrol_web_demo.model.job.JobDto;
import com.orient.firecontrol_web_demo.model.job.ReScheduleDto;
import com.orient.firecontrol_web_demo.service.job.JobAndTriggerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/25 14:18
 * @func
 */
@RestController
@RequestMapping(value="/job")
public class JobController {
    @Autowired
    private JobAndTriggerService jobAndTriggerService;



    @PostMapping(value="/addjob")
    @ApiOperation(value = "新增定时任务",notes = "新增定时任务")
    public ResultBean addjob(@RequestBody@ApiParam(name = "任务调度视图bean",value = "传入json数据",required = true) JobDto jobDto) throws Exception
    {
        return jobAndTriggerService.addJob(jobDto);
    }




    @PostMapping(value="/pausejob")
    public ResultBean pausejob(@ApiParam(name = "jobClassName",value = "想要暂停的任务调度,connectJob还是breakJob",required = true)@RequestParam(value="jobClassName")String jobClassName,
                         @ApiParam(name = "jobGroupName",value = "想要暂停的任务名称",required = true)@RequestParam(value="jobGroupName")String jobGroupName) throws Exception
    {
        return jobAndTriggerService.jobPause(jobClassName, jobGroupName);
    }




    @PostMapping(value="/resumejob")
    public ResultBean resumejob(@ApiParam(name = "jobClassName",value = "想要恢复执行的任务调度,connectJob还是breakJob",required = true)@RequestParam(value="jobClassName")String jobClassName,
                                @ApiParam(name = "jobGroupName",value = "想要恢复执行的任务名称",required = true)@RequestParam(value="jobGroupName")String jobGroupName) throws Exception
    {
        return jobAndTriggerService.jobresume(jobClassName, jobGroupName);
    }




    @PostMapping(value="/reschedulejob")
    public ResultBean rescheduleJob(@ApiParam(name = "更新的任务调度bean",value = "传递json参数",required = true)@RequestBody ReScheduleDto reScheduleDto) throws Exception
    {
        return jobAndTriggerService.jobreschedule(reScheduleDto);
    }




    @PostMapping(value="/deletejob")
    public ResultBean deletejob(@RequestParam(value="jobClassName")String jobClassName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
    {
        return jobAndTriggerService.jobdelete(jobClassName, jobGroupName);
    }




    @GetMapping(value="/queryjob")
    public Map<String, Object> queryjob(@ModelAttribute @Validated PageUtils pageUtils)
    {
        PageInfo<JobAndTrigger> pageInfo = jobAndTriggerService.getJobAndTriggerDetails(pageUtils);
        Map<String,Object> map = new HashMap<>();
        map.put("currentPage", pageUtils.getPage());
        map.put("thisPageNum", pageInfo.getSize());
        map.put("jobList", pageInfo.getList());
        map.put("totalNum", pageInfo.getTotal());
        return map;
    }

    public static BaseJob getClass(String classname) throws Exception
    {
        Class<?> class1 = Class.forName(classname);
        return (BaseJob)class1.newInstance();
    }

}
