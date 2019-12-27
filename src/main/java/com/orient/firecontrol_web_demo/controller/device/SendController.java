package com.orient.firecontrol_web_demo.controller.device;

import com.orient.firecontrol_web_demo.config.rabbit.SendCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/12 11:16
 * @func
 */
@RestController
@RequestMapping("/send")
@Slf4j
public class SendController {
    @Autowired
    private SendCommand sendCommand;


    /**
     * @func  50指令 02合上支路开关
     * @param deviceCode
     * @return
     */
    @GetMapping("/50/connect/{deviceCode}")
    @RequiresAuthentication
    public String send50Connect(@PathVariable("deviceCode") String deviceCode){
        log.info("===开始下发闭合指令===");
        String msg = "eb90eb9002"+deviceCode.substring(0, 10)+"08005001"+deviceCode.substring(10, 16)+"0203";
        System.out.println("需要合上的设备==="+deviceCode);
        System.out.println("发送的指令为==="+msg);
        sendCommand.send50(msg);
        log.info("===闭合指令发送成功===");
        return "SEND OK!!!";
    }

    /**
     * @func  50指令 01断开支路开关
     * @param deviceCode
     * @return
     */
    @GetMapping("/50/break/{deviceCode}")
    @RequiresAuthentication
    public String send50Break(@PathVariable("deviceCode") String deviceCode){
        log.info("===开始下发断开指令===");
        String msg = "eb90eb9002"+deviceCode.substring(0, 10)+"08005001"+deviceCode.substring(10, 16)+"0103";
        System.out.println("需要断开的设备==="+deviceCode);
        System.out.println("发送的指令为==="+msg);
        sendCommand.send50(msg);
        log.info("===断开指令发送成功===");
        return "SEND OK!!!";
    }


    @GetMapping("/60")
    @RequiresAuthentication
    public String send60(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        String msg = "eb90eb9002FFFFFFFFFF0B0060FFFF"+format.substring(2, 4)+format.substring(5, 7)
                +format.substring(8, 10)+format.substring(11, 13)+format.substring(14, 16)+format.substring(17, 19)+"03";
        sendCommand.send60(msg);
        return "SEND OK!!!";
    }


}
