package com.orient.firecontrol_web_demo.dao.organization;

import com.orient.firecontrol_web_demo.model.organization.Organization;
import com.orient.firecontrol_web_demo.model.organization.OrganizationDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/24 10:48
 * @func 单位相关接口
 */
@Mapper
@Repository
public interface OrganDao {
    /**
     * 查询所有单位列表  超级管理员权限
     * @return
     */
    List<Organization> listAll();


    /**
     * 根据当前账号查询其所属的部门
     * @param account
     * @return
     */
    Organization findByAccount(String account);

    /**
     * 添加单位  超级管理员权限
     * @param organization
     * @return
     */
    int addOrgan(Organization organization);

    /**
     * 根据单位名称查询单位信息
     * @param organName
     * @return
     */
    Organization findByOrganName(String organName);


    /**
     * 修改单位名称
     * @param organization
     * @return
     */
    int updateOrgan(OrganizationDto organization);


    Organization findById(Integer id);


    /**
     * 根据建筑物id查找单位id  因为一个建筑物只可以绑定到一个单位
     * @param buildId
     * @return
     */
    int findByBuildId(Integer buildId);




}
