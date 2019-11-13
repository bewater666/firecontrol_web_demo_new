package com.orient.firecontrol_web_demo.config.aop;

import com.alibaba.fastjson.JSON;
import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.dao.log.SysLogDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.log.SysLog;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 11:15
 * @func 定义一个切面
 */
@Aspect
@Component
public class SysLogAspect {
    @Autowired
    private SysLogDao sysLogDao;
    @Autowired
    private UserDao userDao;


    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(com.orient.firecontrol_web_demo.config.aop.MyLog)")
    public void logPointCut(){

    }


    //切面 配置通知
    @AfterReturning("logPointCut()")
    @Transactional
    public void saveSysLog(JoinPoint joinPoint) {
        System.out.println("切面。。。。。");
        //保存日志
        SysLog sysLog =new SysLog();
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);
        if (myLog != null) {
            String value = myLog.description();
            sysLog.setOperate(value);//保存获取的操作
        }

        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        String methodName = method.getName();
        sysLog.setMethod(className + "." + methodName);

        //请求的参数
        Object[] args = joinPoint.getArgs();
        //将参数所在的数组转换成json
        String params = JSON.toJSONString(args);
        sysLog.setParams(params);

        sysLog.setOperateTime(new Date());
        //获取用户username   不拦截登录controller
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        String username = userDao.findOneByAccount(account).getUsername();
        sysLog.setOperator(username);
        //获取用户ip地址
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String ip = IpAdrressUtil.getIpAdrress(request);
        sysLog.setOperateIP(ip);
        sysLogDao.addLog(sysLog);
    }


}
