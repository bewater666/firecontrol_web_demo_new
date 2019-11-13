package com.orient.firecontrol_web_demo.config.common;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/18 16:55
 * @func base64工具类  用于加密解密
 */
public class Base64Utils {

    /**
     * @func  base64加密
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String str) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes("utf-8"));
        return new String(encodeBytes);
    }


    /**
     * @func  base64解密
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String str) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes("utf-8"));
        return new String(decodeBytes);
    }


    public static void main(String[] args) {
        try {
            String zhoudunJwt = Base64Utils.decode("rb5dWtWjCiiRNoGx9sIxmAYOCMtu5ImTZLStGln8gpk");
            System.out.println(zhoudunJwt);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
