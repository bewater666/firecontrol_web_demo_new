package com.orient.firecontrol_web_demo.dao.user;

import com.orient.firecontrol_web_demo.model.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/29 11:04
 * @func
 */
@Mapper
@Repository
public interface UserRoleDao {
    /**
     * 绑定用户角色信息
     * @param userRole
     * @return
     */
    int addUser_Role(UserRole userRole);

    /**
     * 根据userId查看tb_user_role对应的信息
     * @param userId
     * @return
     */
    List<UserRole> findByUserId(Integer userId);

    /**
     * 修改用户角色
     * @param userRole
     * @return
     */
    int updateUser_Role(UserRole userRole);

}

