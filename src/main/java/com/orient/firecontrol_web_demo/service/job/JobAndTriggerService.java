package com.orient.firecontrol_web_demo.service.job;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.config.page.PageUtils;
import com.orient.firecontrol_web_demo.config.redis.JedisUtil;
import com.orient.firecontrol_web_demo.dao.job.JobAndTriggerMapper;
import com.orient.firecontrol_web_demo.dao.organization.OrganDao;
import com.orient.firecontrol_web_demo.dao.user.RoleDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.job.*;
import com.orient.firecontrol_web_demo.model.job.JobDetail;
import com.orient.firecontrol_web_demo.model.organization.Organization;
import com.orient.firecontrol_web_demo.model.user.Role;
import org.apache.shiro.SecurityUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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



    //加入Qulifier注解，通过名称注入bean
    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private OrganDao organDao;

    /**
     * 查看任务调度列表 只允许superadmin和admin角色访问
     * @param pageUtils
     * @param buildName 建筑物名称
     * @param floorName 楼层名称
     * @return
     */
    public PageInfo<JobAndTrigger> getJobAndTriggerList(PageUtils pageUtils,String buildName,String floorName) {
        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
            pageUtils.setPage(1);
            pageUtils.setRows(10);
        }
        //取出当前登录的账户
        //取出当前登录的用户信息
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        Integer enable = userDao.findOneByAccount(account).getEnable();
        if (enable==0){
            throw new CustomException("查询失败该账户已被禁用,请与管理员联系" +
                    "(Query failed this account has been disabled, please contact the administrator)");
        }
        List<Role> byUser = roleDao.findByUser(account);
        //因为在该项目中  我一个账号只设置了一个角色  故byUser这个list中就一个角色 取第一个就好
        Role role = byUser.get(0);
        if (role.getRoleName().equals("superadmin")){//超级管理员账户
            //超级管理员 默认查询所有单位所有的任务调度列表
            PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows());
            List<JobAndTrigger> list = jobAndTriggerMapper.getJobAndTriggerDetails();
            PageInfo<JobAndTrigger> pageInfo = new PageInfo<JobAndTrigger>(list);
            return pageInfo;
        }
        if (role.getRoleName().equals("admin")){//单位领导
            //根据账号查询当前单位领导账号所属的单位(一个单位领导管辖一个单位 所以这里查出来的是单个 而不是list)
            Organization byAccount = organDao.findByAccount(account);
            String organizationName = byAccount.getOrganizationName();
            String flag = null;
            if (buildName==null&&floorName==null){ //建筑物名称 楼层名称都不传 即查询整个单位的任务调度
                flag = "%"+organizationName+"%";
            }else if (buildName!=null&&floorName==null){//传建筑物名称   则是查询该建筑物下所有楼层的任务调度
                flag = "%"+organizationName+"-"+buildName+"-%";
            }else if (buildName!=null&&floorName!=null){//建筑物名称 楼层名称都传 则是查询某单位某建筑物下的 某楼层下的任务调度
                flag = "%"+organizationName+"-"+buildName+"-"+floorName+"-%";
            }else{
                return null;
            }
            PageHelper.startPage(pageUtils.getPage(), pageUtils.getRows()); //该行代码应放在查询语句前面
            List<JobAndTrigger> byFlag = jobAndTriggerMapper.getByFlag(flag);
            PageInfo<JobAndTrigger> pageInfo = new PageInfo<JobAndTrigger>(byFlag);
            return pageInfo;
        }
        return null;

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
        org.quartz.JobDetail jobDetail = null;
        //这里我写死了 因为就两个任务 所以前台传来的jobClassName值只能是这两个
        if (jobClassName.equals("connectJob")){
            //构建job信息
            jobDetail = JobBuilder.newJob(ConnectJob.class).withIdentity(jobClassName, needJobGroupName).usingJobData(jobDataMap).build();
        }else if (jobClassName.equals("breakJob")){
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
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, needJobGroupName)
                .withSchedule(scheduleBuilder).build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            //该map用存deviceCodeList 因为我当前无法将任务调度传递的dataMap进行持久化
            //所以每次我在新建任务调度的时间 就将传递的设备code列表存在redis中
            //（跟服务端用静态的map存socket连接信息存在内存中不一样 因为设备会一直给服务端发心跳 所以就算服务重启map中存的socket信息清空了也没关系 当有心跳来了 socket信息就又会存在map中了）
            //但是这里就不能用静态map存设备code列表放在内存中了 因为当服务重启了 map就清空了 这时候就不用进行对之前存在的任务进行重新调度了（任务重新调度会用到这个map）
            //所以这里选择存在redis中 map中的key是jobName和jobGroupName组合  value是设备code列表信息
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put(jobClassName+"-"+needJobGroupName, jobDto.getDeviceCodeList());
            //存入redis中
            JedisUtil.setObject("dataMap", dataMap);
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
            //调用clear方法 清空里面存在的JobDataMap  不然只能第一次修改成功 不知道什么原因
            trigger.getJobDataMap().clear();
            //没改之前的任务调度中的设备code列表
            Map dataMap1 = (Map)JedisUtil.getObject("dataMap");
            List<String> orignDeviceCodeList = (List<String>)dataMap1.get(jobClassName + "-" + jobGroupName);
            //为了要弄得新的list呢 因为进行removeAll操作 原先的list就变了 为了不破坏原来的list 所以这边定义了个新的list 用于操作
            List<String> operateDeviceCodeList = new ArrayList<>(orignDeviceCodeList);
            //去重操作
            operateDeviceCodeList.removeAll(updateDeviceCodeList);
            JobDataMap updateJobDataMap = new JobDataMap();
            updateJobDataMap.put("deviceList", updateDeviceCodeList);
            //若operateDeviceCodeList最终结果变成了空 则表示想修改的和原来一样 则和原来一样处理 dataMap不用变 否则传递新的JobDataMap 并dataMap也进行替换
            if (operateDeviceCodeList.size()==0){ //deviceCodeList不变 修改cron表达式即可
                //原先如果deviceCodeList不变不用再向trigger传递JobDataMap 但是好像不行只有第一次修改成功 现在修改成每次都传 传之前清空 该操作前面已经做了

                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).usingJobData(updateJobDataMap).build();
            }else{//deviceCodeList变了 传递新的JobDataMap(通过trigger传递) 更改redis中的dataMap
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).usingJobData(updateJobDataMap).build();
                //JobDetail中的JobDataMap要不要清掉呢 应该不需要 在具体的任务执行类中 我做了判断 当trigger中JoBDataMap就用trigger中的  当然清掉也行 我这里就没清了
                Map<String,Object> updateRedisMap = new HashMap<>();
                updateRedisMap.put(jobClassName+"-"+jobGroupName, updateDeviceCodeList);
                //直接将redis中的dataMap1覆盖即可
                JedisUtil.setObject("dataMap", updateRedisMap);
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

    /**
     * 查看某个任务详情 查看进行任务调度的设备code列表信息
     * 下次执行时间 任务状态 等信息
     * @param jobClassName
     * @param jobGroupName
     * @return
     * @throws Exception
     */
    public ResultBean getJobDetail(String jobClassName, String jobGroupName) throws Exception{
        JobAndTrigger byJobClassNameAndJobGroupName = jobAndTriggerMapper.getByJobClassNameAndJobGroupName(jobClassName, jobGroupName);
        if(byJobClassNameAndJobGroupName==null){
            throw new CustomException("该任务调度不存在");
        }
        JobDetail jobDetail = jobAndTriggerMapper.getJobDetail(jobClassName, jobGroupName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String PREV_FIRE_TIME = sdf.format(jobDetail.getPREV_FIRE_TIME());
        String NEXT_FIRE_TIME = sdf.format(jobDetail.getNEXT_FIRE_TIME());
        String START_TIME = sdf.format(jobDetail.getSTART_TIME());
        JobDetailDto jobDetailDto = new JobDetailDto();
        jobDetailDto.setPREV_FIRE_TIME(PREV_FIRE_TIME);
        jobDetailDto.setNEXT_FIRE_TIME(NEXT_FIRE_TIME);
        jobDetailDto.setSTART_TIME(START_TIME);
        if (jobDetail.getTRIGGER_STATE().equals("WAITING")){
            jobDetailDto.setTRIGGER_STATE("等待下次执行");
        }else if (jobDetail.getTRIGGER_STATE().equals("PAUSED")){
            jobDetailDto.setTRIGGER_STATE("暂停");
        }else if (jobDetail.getTRIGGER_STATE().equals("ACQUIRED")){
            jobDetailDto.setTRIGGER_STATE("正在执行");
        }else if (jobDetail.getTRIGGER_STATE().equals("BLOCKED")){
            jobDetailDto.setTRIGGER_STATE("阻塞");
        }else{
            jobDetailDto.setTRIGGER_STATE("错误");
        }

        if (jobDetail.getJOB_NAME().equals("connectJob")){
            jobDetailDto.setJobDesc("开灯定时任务");
        }
        if (jobDetail.getJOB_NAME().equals("breakJob")){
            jobDetailDto.setJobDesc("关灯定时任务");
        }
        Map dataMap = (Map) JedisUtil.getObject("dataMap");
        List deviceCodeList = (List) dataMap.get(jobClassName + "-" + jobGroupName);
        jobDetailDto.setDeviceCodeList(deviceCodeList);
        return new ResultBean(200, "查询任务详情成功", jobDetailDto);
    }
}
