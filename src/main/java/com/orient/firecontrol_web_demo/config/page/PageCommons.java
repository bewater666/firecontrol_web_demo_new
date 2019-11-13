package com.orient.firecontrol_web_demo.config.page;

import com.orient.firecontrol_web_demo.config.exception.CustomException;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/11/4 16:43
 * @func
 */
public class PageCommons {

    /**
     * 获取当前页数据的个数
     * @param currentPage
     * @param pageSize
     * @param pageBean
     * @return
     */
    public static int getThisPageNum(Integer currentPage,Integer pageSize,PageBean pageBean){
        Integer thisPageNum;
        if (currentPage<=0){
            throw new CustomException("输入的页码不符合规范");
        }
        if (currentPage==1){
            if (pageBean.getTotalNum()<=pageSize){
                thisPageNum =  pageBean.getTotalNum();
            }else {
                thisPageNum = pageSize;
            }
        }else {
            Integer size = pageBean.getTotalNum()-(currentPage-1)*pageSize;
            if (size>=pageSize){
                thisPageNum = pageSize;
            }else {
                thisPageNum = size;
            }
        }
        if ((currentPage-1)*pageSize>pageBean.getTotalNum()){
            thisPageNum = 0;
        }
        return thisPageNum;
    }
}
