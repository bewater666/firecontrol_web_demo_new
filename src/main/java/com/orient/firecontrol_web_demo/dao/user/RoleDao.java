package com.orient.firecontrol_web_demo.dao.user;

import com.orient.firecontrol_web_demo.model.user.Role;
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
public interface RoleDao {
    /**
     * @func  查看账户下的角色列表
     * @param account
     * @return
     */
    List<Role> findByUser(String account);

    /**
     * 根据userId查看角色列表  我这里每个账户一个角色  所以list,size()==1
     * @param userId
     * @return
     */
    List<Role> findByuserId(Integer userId);

    Role findByRoleId(Integer roleId);

    /**
     * 根据账户查询角色id  前端需要
     * @param account
     * @return
     */
    Integer findRoleIdByAccount(String account);
}
