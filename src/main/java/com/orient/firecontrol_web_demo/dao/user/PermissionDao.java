package com.orient.firecontrol_web_demo.dao.user;

import com.orient.firecontrol_web_demo.model.user.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 11:41
 * @func
 */
@Mapper
@Repository
public interface PermissionDao {
    /**
     * @func  查询某个角色下的权限列表
     * @param roleName
     * @return
     */
    List<Permission> findByRole(String roleName);

    List<Permission> loadPermListByAccount(String account);
}
