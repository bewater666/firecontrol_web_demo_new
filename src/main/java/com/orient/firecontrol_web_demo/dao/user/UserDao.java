package com.orient.firecontrol_web_demo.dao.user;

import com.orient.firecontrol_web_demo.model.user.User;
import com.orient.firecontrol_web_demo.model.user.UserUp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 11:40
 * @func
 */
@Mapper
@Repository
public interface UserDao {

    User findOneByAccount(String account);

    User findByUserId(Integer id);

    List<User> findAll();

    void addUser(User user);

    int deleteUser(Integer id);

    Integer findOrganIDBy(String account);

    /**
     * 查询某个单位下的用户列表
     * @param organId
     * @return
     */
    List<User> findByOrganId(Integer organId);

    /**
     * 启用与禁用账户
     * @param id 用户id
     * @return
     */
    int changeUserStatus(Integer id);


    /**
     * 更新用户信息
     * @param userUp
     * @return
     */
    int update(UserUp userUp);
}
