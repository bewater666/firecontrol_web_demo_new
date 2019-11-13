package com.orient.firecontrol_web_demo.config.user;

import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.user.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 16:53
 * @func 获取当前登录用户工具类
 */
public class UserUtil {
    @Autowired
    private UserDao userDao;

    public User getUser(){
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        //根据token得到当前账户
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        User oneByAccount = userDao.findOneByAccount(account);
        if (oneByAccount!=null){
            return oneByAccount;
        }
        throw new CustomException("该帐号不存在(The account does not exist.)");
    }

}
