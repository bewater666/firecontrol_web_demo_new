package com.orient.firecontrol_web_demo.dao.alarm;

import com.orient.firecontrol_web_demo.model.alarm.AlarmCount;
import com.orient.firecontrol_web_demo.model.alarm.AlarmInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/16 15:56
 * @func
 */
@Mapper
@Repository
public interface AlarmDao {
    void insert(AlarmInfo alarmInfo);

    AlarmInfo findOne(String deviceCode);

    void updateStatus(@Param("isHandler") String isHandler, @Param("deviceCode") String deviceCode);


    /**
     * 查询所有的告警  用于告警管理
     * 超级管理员使用该接口查看全部的告警信息
     * @return
     */
    List<AlarmInfo> findAll();

    /**
     * 根据单位id  查询该单位下的报警信息
     * 部门领导使用该接口  查看该部门下的告警信息
     * @param organId
     * @return
     */
    List<AlarmInfo> findByOrganId(Integer organId);

    /**
     * 查看已处理(处理成功)的列表
     * 超级管理员使用该接口查看全部已处理的告警信息
     * @return
     */
    List<AlarmInfo> findHasHandler();

    /**
     * 查看已处理(处理成功)的列表
     * 部门领导使用该接口  查看该部门下已处理的告警信息
     * @param organId
     * @return
     */
    List<AlarmInfo> findOrganHasHandler(Integer organId);


    /**
     * 查看 处理失败的告警信息
     * 超级管理员使用该接口查看全部处理失败的告警信息
     * @return
     */
    List<AlarmInfo> findHandlerBad();

    /**
     * 部门领导使用该接口  查看该部门下处理失败的告警信息
     * @param organId
     * @return
     */
    List<AlarmInfo> findOrganHandlerBad(Integer organId);


    /**
     * 查看未处理的告警信息列表
     * 超级管理员使用该接口查看全部未处理的告警信息
     * @return
     */
    List<AlarmInfo> findUnHandler();

    /**
     * 部门领导使用该接口  查看该部门下未处理的告警信息
     * @param organId
     * @return
     */
    List<AlarmInfo> findOrganUnHandler(Integer organId);

    /**
     * 根据设备id  查询他的告警信息列表
     * @param deviceCode
     * @return
     */
    List<AlarmInfo> findByDeviceCode(String deviceCode);

    /**
     * 计数某告警级别的数量
     * 超级管理员使用该接口直接计数 ,不需要organId这个参数
     * @param alarmGrade
     * @return
     */
    int countNum(String alarmGrade);


    /**
     * 部门领导使用该接口  传入告警等级 和部门id来计数
     * @param alarmGrade
     * @param organId
     * @return
     */
    int countOrganNum(@Param("alarmGrade") String alarmGrade,@Param("organId") Integer organId);


    /**
     * 计数 所有部门最近7天的告警数量
     * 超级管理员使用
     * @return
     */
    List<AlarmCount> countLast7Days();

    /**
     * 单位领导 计数各自单位 最近7天的告警数量
     * 单位领导使用  超级管理员也可使用
     * @param organId
     * @return
     */
    List<AlarmCount> countOrganLast7Days(int organId);

    /**
     * 超级管理员统计全部单位已处理的告警数量
     * @return
     */
    int countHasHandler();

    /**
     * 超级管理员统计全部单位未处理的告警数量
     * @return
     */
    int countUnHandler();

    /**
     * 超级管理员统计全部单位处理失败的告警数量
     * @return
     */
    int countHandlerBad();


    /**
     * 单位领导统计该单位下已处理告警数量
     * @param organId
     * @return
     */
    int countOrganHasHandler(Integer organId);


    /**
     * 单位领导统计该单位下未处理的告警数量
     * @param organId
     * @return
     */
    int countOrganUnHandler(Integer organId);

    /**
     * 单位领导统计该单位下处理失败的告警数量
     * @param organId
     * @return
     */
    int countOrganHandlerBad(Integer organId);

}
