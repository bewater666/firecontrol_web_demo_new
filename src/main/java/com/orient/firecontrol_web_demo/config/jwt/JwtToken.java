package com.orient.firecontrol_web_demo.config.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/18 15:20
 * @func 使用我们自己的jwtToken
 */
public class JwtToken implements AuthenticationToken {
    /**
     * Token
     */
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
