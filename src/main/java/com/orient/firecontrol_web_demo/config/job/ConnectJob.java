package com.orient.firecontrol_web_demo.config.job;

import com.orient.firecontrol_web_demo.config.rabbit.SendCommand;
import com.orient.firecontrol_web_demo.dao.job.BaseJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/12/26 16:32
 * @func
 */
public class ConnectJob implements BaseJob {
    @Autowired
    SendCommand sendCommand;

    public ConnectJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //业务代码
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        List<String> deviceCodes = (List<String>) jobDataMap.get("deviceList");
        for (String deviceCode:
                deviceCodes) {
            String msg = "eb90eb9002"+deviceCode.substring(0, 10)+"08005001"+deviceCode.substring(10, 16)+"0203";
            sendCommand.send50(msg);
        }
    }
}
