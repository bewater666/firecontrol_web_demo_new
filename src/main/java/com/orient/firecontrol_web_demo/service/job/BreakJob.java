package com.orient.firecontrol_web_demo.service.job;

import com.orient.firecontrol_web_demo.config.rabbit.SendCommand;
import com.orient.firecontrol_web_demo.dao.job.BaseJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/26 16:32
 * @func
 */
@Component
public class BreakJob implements BaseJob {
    @Autowired
    SendCommand sendCommand;

    public BreakJob() {
    }
    public static BreakJob breakJob;

    @PostConstruct
    public void init(){
        breakJob = this;
        breakJob.sendCommand = this.sendCommand;
        //初使化时将已静态化的sendCommand实例化
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //我这边定义jobDetail中的jobDataMap是存新增任务调度时传过来的deviceCodeList
        //trigger中定义的jobDataMap是存更新后的deviceCodeList
        //所以每次跑的时候判断trigger中的map是否为空 为空就使用jobDetail里的map
        //不为空代表 进行了任务的重新调度(我逻辑是这样处理的) 使用trigger里的map
        //业务代码
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        JobDataMap triggerMap = jobExecutionContext.getTrigger().getJobDataMap();
        List<String> deviceCodes = null;
        if (triggerMap.size()==0){
            deviceCodes = (List<String>) jobDataMap.get("deviceList");
        }else{
            deviceCodes = (List<String>) triggerMap.get("deviceList");
        }
        for (String deviceCode:
                deviceCodes) {
            String msg = "eb90eb9002"+deviceCode.substring(0, 10)+"08005001"+deviceCode.substring(10, 16)+"0103";
            breakJob.sendCommand.send50(msg);
        }
    }
}
