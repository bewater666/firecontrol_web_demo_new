package com.orient.firecontrol_web_demo.service.job;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.job.BreakJob;
import com.orient.firecontrol_web_demo.config.job.ConnectJob;
import com.orient.firecontrol_web_demo.config.page.PageUtils;
import com.orient.firecontrol_web_demo.dao.job.JobAndTriggerMapper;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.job.JobAndTrigger;
import com.orient.firecontrol_web_demo.model.job.JobDto;
import com.orient.firecontrol_web_demo.model.job.ReScheduleDto;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/25 14:27
 * @func
 */
@Service
public class JobAndTriggerService {
    @Autowired
    @SuppressWarnings("all")
    private JobAndTriggerMapper jobAndTriggerMapper;

    //该静态map用存deviceCodeList 返回给前端 每次新增任务调度的时候往里放 修改的时候再存一次 根据key来进行覆盖
    private static Map<String,Object> dataMap = new HashMap<>();

    //加入Qulifier注解，通过名称注入bean
    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    public PageInfo<JobAndTrigger> getJobAndTriggerDetails(PageUtils pageUtils) {

        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
            pageUtils.setPage(1);
            pageUtils.setRows(10);
        }
        PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
        List<JobAndTrigger> list = jobAndTriggerMapper.getJobAndTriggerDetails();
        PageInfo<JobAndTrigger> pageInfo = new PageInfo<JobAndTrigger>(list);
        return pageInfo;
    }

    /**
     * 单位领导 超级管理员 给单位下 某建筑物下 某楼层下 新建任务调度
     * jobGroupName将来是我们来进行查询的参数 所以这个jobGroupName我们取名要规范些  前缀要和单位建筑楼层绑定
     * @param jobDto
     * @throws Exception
     */
    public ResultBean addJob(JobDto jobDto)throws Exception{
        //任务className 一般和具体的任务对应 我们这写了两个任务 connectJob breakJob 跟前台约定传这两个值就好
        String jobClassName = jobDto.getJobClassName();
        //cron表达式
        String cronExpression = jobDto.getCronExpression();
        //单位名称
        String organizationName = jobDto.getOrganizationName();
        //建筑物名称
        String buildName = jobDto.getBuildName();
        //楼层名称
        String floorName = jobDto.getFloorName();
        //用户输入的调度名称
        String jobGroupName = jobDto.getJobGroupName();
        //生成我们需要的任务调度名称
        String needJobGroupName = organizationName+"-"+buildName+"-"+floorName+"-"+jobGroupName;
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("deviceList", jobDto.getDeviceCodeList());
        // 启动调度器
        scheduler.start();
        //JobDetail 作业实例
        JobDetail jobDetail = null;
        //这里我写死了 因为就两个任务 所以前台传来的jobClassName值只能是这两个
        if (jobClassName.equals("connectJob")){
            //构建job信息
            jobDetail = JobBuilder.newJob(ConnectJob.class).withIdentity(jobClassName, needJobGroupName).usingJobData(jobDataMap).build();
        }if (jobClassName.equals("breakJob")){
            jobDetail = JobBuilder.newJob(BreakJob.class).withIdentity(jobClassName, needJobGroupName).usingJobData(jobDataMap).build();
        }else{
            throw new CustomException("jobClassName参数传入有误 找不到对应的任务调度处理方法");
        }
        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = null;
        try {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        } catch (Exception e) {
            throw new CustomException("cron表达式有误,请检查");
        }
        scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName)
                .withSchedule(scheduleBuilder).build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            dataMap.put(jobClassName+"-"+jobGroupName, jobDto.getDeviceCodeList());
            return new ResultBean(200, "新建任务调度成功");

        }catch (ObjectAlreadyExistsException e){
            System.out.println("任务调度名称已存在,请重新输入");
        }
        catch (SchedulerException e) {
            System.out.println("创建定时任务失败"+e);
            throw new Exception("创建定时任务失败");
        }
        return null;
    }

    /**
     * 暂停某任务调度
     * @param jobClassName
     * @param jobGroupName
     * @return
     * @throws Exception
     */
    public ResultBean jobPause(String jobClassName, String jobGroupName) throws Exception
    {
        JobAndTrigger byJobClassNameAndJobGroupName = jobAndTriggerMapper.getByJobClassNameAndJobGroupName(jobClassName, jobGroupName);
        if (byJobClassNameAndJobGroupName ==null){
            throw new CustomException("该任务调度不存在");
        }
        scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        return new ResultBean(200, "暂停该任务调度成功", null);
    }

    /**
     * 恢复某个被暂停的任务
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    public ResultBean jobresume(String jobClassName, String jobGroupName) throws Exception
    {
        JobAndTrigger byJobClassNameAndJobGroupName = jobAndTriggerMapper.getByJobClassNameAndJobGroupName(jobClassName, jobGroupName);
        if (byJobClassNameAndJobGroupName ==null){
            throw new CustomException("该任务调度不存在");
        }
        //只有暂停状态才可被恢复
        if (!byJobClassNameAndJobGroupName.getTRIGGER_STATE().equals("PAUSED")){
            throw new CustomException("该任务调度不处于暂停状态");
        }
        scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        return new ResultBean(200, "该任务恢复执行", null);
    }


    /**
     * 更新任务调度 时间 设备列表均可改
     * @param reScheduleDto
     * @return
     * @throws Exception
     */
    public ResultBean jobreschedule(ReScheduleDto reScheduleDto) throws Exception
    {
        try {
            //要更新的jobClassName 由前端通过上下文传入 定义用户不可改 该参数用途是为了找到这个调度
            String jobClassName = reScheduleDto.getJobClassName();
            //要更新的jobGroupName 由前端通过上下文传入 定义用户不可改 该参数用途是为了找到这个调度
            String jobGroupName = reScheduleDto.getJobGroupName();
            //希望修改成的cron表达式
            String cronExpression = reScheduleDto.getCronExpression();
            //希望修改的设备code列表
            List<String> updateDeviceCodeList = reScheduleDto.getDeviceCodeList();

            JobAndTrigger byJobClassNameAndJobGroupName = jobAndTriggerMapper.getByJobClassNameAndJobGroupName(jobClassName, jobGroupName);
            if (byJobClassNameAndJobGroupName ==null){
                throw new CustomException("该任务调度不存在");
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
//            withMisfireHandlingInstructionDoNothing(所有的misfire不管，执行下一个周期的任务)
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //没改之前的任务调度中的设备code列表
            List<String> orignDeviceCodeList = (List<String>)dataMap.get(jobClassName + "-" + jobGroupName);
            //为了要弄得新的list呢 因为进行removeAll操作 原先的list就变了 为了不破坏原来的list 所以这边定义了个新的list 用于操作
            List<String> operateDeviceCodeList = new ArrayList<>(orignDeviceCodeList);
            //去重操作
            operateDeviceCodeList.removeAll(updateDeviceCodeList);
            //若operateDeviceCodeList最终结果变成了空 则表示想修改的和原来一样 则和原来一样处理 dataMap不用变 否则传递新的JobDataMap 并dataMap也进行替换
            if (operateDeviceCodeList.size()==0){ //deviceCodeList不变 修改cron表达式即可
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            }else{//deviceCodeList变了 传递新的JobDataMap(通过trigger传递) 更改dataMap
                JobDataMap updateJobDataMap = new JobDataMap();
                updateJobDataMap.put("deviceList", updateDeviceCodeList);
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).usingJobData(updateJobDataMap).build();
                dataMap.put(jobClassName+"-"+jobGroupName, updateDeviceCodeList);
            }
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            return new ResultBean(200, "修改任务调度成功", null);

        } catch (SchedulerException e) {
            System.out.println("更新定时任务失败"+e);
            throw new Exception("更新定时任务失败");
        }
    }


    /**
     * 删除指定的任务调度
     * @param jobClassName
     * @param jobGroupName
     * @return
     * @throws Exception
     */
    public ResultBean jobdelete(String jobClassName, String jobGroupName) throws Exception
    {
        JobAndTrigger byJobClassNameAndJobGroupName = jobAndTriggerMapper.getByJobClassNameAndJobGroupName(jobClassName, jobGroupName);
        if (byJobClassNameAndJobGroupName ==null){
            throw new CustomException("该任务调度不存在");
        }
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        return new ResultBean(200, "删除任务调度成功", null);
    }
}
