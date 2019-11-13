package com.orient.firecontrol_web_demo.service.user;

import com.github.pagehelper.PageHelper;
import com.orient.firecontrol_web_demo.config.common.StringUtil;
import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.config.page.PageBean;
import com.orient.firecontrol_web_demo.config.password.AesCipherUtil;
import com.orient.firecontrol_web_demo.config.shiro.UserRealm;
import com.orient.firecontrol_web_demo.dao.organization.OrganDao;
import com.orient.firecontrol_web_demo.dao.user.RoleDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.dao.user.UserRoleDao;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.organization.Organization;
import com.orient.firecontrol_web_demo.model.user.Role;
import com.orient.firecontrol_web_demo.model.user.User;
import com.orient.firecontrol_web_demo.model.user.UserRole;
import com.orient.firecontrol_web_demo.model.user.UserUp;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/25 13:55
 * @func
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    UserRealm userRealm;
    @Autowired
    private OrganDao organDao;


    /**
     * 人员管理业务代码
     * 超级管理员可以看到所有人员信息
     * 单位领导管理员可以看到该部门下的人员信息
     * @return
     */
    public PageBean<User>listUser(Integer currentPage,Integer pageSize){
        //取出当前登录的用户信息
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        Integer enable = userDao.findOneByAccount(account).getEnable();
        if (enable==0){
            throw new CustomException("查询失败该账户已被禁用,请与管理员联系" +
                    "(Query failed this account has been disabled, please contact the administrator)");
        }
        List<Role> byUser = roleDao.findByUser(account);
        //因为在该项目中  我一个账号只设置了一个角色  故byUser这个list中就一个角色 取第一个就好
        Role role = byUser.get(0);
        Map<String,Object> map = new HashMap<>();
        if (role.getRoleName().equals("superadmin")){
            PageHelper.startPage(currentPage,pageSize);
            List<User> all = userDao.findAll();
            //若是超级管理员  那么查询出的用户列表就是全部的人员信息
            PageBean<User> pageBean = new PageBean<>(currentPage, pageSize, userDao.findAll().size());
            pageBean.setItems(all);
            return pageBean;
        }
        if (role.getRoleName().equals("admin")){
            //该角色是某单位的领导
            //需确定具体是哪个部门的领导  取出该账户下的organId
            Integer organId = userDao.findOneByAccount(account).getOrganId();
            PageHelper.startPage(currentPage,pageSize);
            List<User> byOrganId = userDao.findByOrganId(organId);
            PageBean<User> pageBean = new PageBean<>(currentPage, pageSize, userDao.findByOrganId(organId).size());
            pageBean.setItems(byOrganId);
            return pageBean;
        }
        //因为在该controller接口 已经约定只有superadmin admin  才可以使用该接口   故别的角色暂时不做判断
        return null;
    }

    /**
     * 将某个用户状态设置为不可用或可用
     * superadmin admin权限
     * @return
     */
    public ResultBean changUserStatus(Integer id){
        //注意这里 单位管理员和超级管理员不可以将自己的状态进行操作
        //获取当前账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        //获取当前账户的id
        Integer id1 = userDao.findOneByAccount(account).getId();
        if (id==id1){
            throw new CustomException("更改用户状态失败:不可以对自己状态进行更改" +
                    "(Failed to change user state: you cannot change your state)");
        }
        int i = userDao.changeUserStatus(id);
        if (i<=0){
            throw new CustomException( "更改用户状态失败(Failed to change user status)" );
        }
        Integer enable = userDao.findByUserId(id).getEnable();
        if (enable==1){
            return new ResultBean(200, "用户状态更改为启用状态(User status changed to enabled)");
        }
        if (enable==0){
            return new ResultBean(200, "用户状态更改为禁用状态(User status changed to disabled)");
        }
        return null;
    }

    /**
     * 保存用户角色
     * @param userRole
     * @return
     */
    @Transactional
    public ResultBean saveUserRole(UserRole userRole){
        //默认 我这里不允许 管理员修改自己的角色
        String account1 = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        Integer id = userDao.findOneByAccount(account1).getId();
        if (id==userRole.getUserId()){
            throw new CustomException("该用户角色不建议修改(单位领导)");
        }
        User byUserId1 = userDao.findByUserId(userRole.getUserId());
        if (byUserId1==null){
            throw new CustomException( "该用户不存在");
        }
        Role byRoleId = roleDao.findByRoleId(userRole.getRoleId());
        if (byRoleId==null){
            throw new CustomException("该角色不存在");
        }
        //先看这个userId在tb_user_role是否有对应的值
        List<UserRole> byUserId = userRoleDao.findByUserId(userRole.getUserId());
        if (byUserId.size()==0){ //没有  那就是给账户赋予角色
            int i = userRoleDao.addUser_Role(userRole);
            if (i<=0){
                throw new CustomException("保存账户角色信息失败");
            }
            //这边没有session  当角色权限改变时  动态刷新权限  还没有做到  日后再改
            return new ResultBean(200, "保存账户角色信息成功", null);
        }else{//有  那就是修改角色信息  将其修改掉即可  我这里一个账户对应一个角色
            int i = userRoleDao.updateUser_Role(userRole);
            if (i<=0){
                throw new CustomException( "保存账户角色信息失败");
            }
            return new ResultBean(200, "保存账户角色信息成功", null);
        }
    }

    /**
     * 查看该用户所具有的角色
     * superadmin    admin权限
     * @return
     */
    public ResultBean checkRoles(Integer userId){
        List<Role> byuserId = roleDao.findByuserId(userId);
        return new ResultBean(200, "查询成功", byuserId);
    }


    /**
     * 管理员新增用户操作
     * 超级管理员添加的是单位领导(默认) 赋予添加的员工admin角色  需要传organId
     * 单位领导添加的默认是该单位下的员工  不要要穿organId 从登录的账户中获得  员工的角色后期自己赋予
     * @return
     */
    @Transactional
    public ResultBean addUser(User user,Integer roleId){
        // 判断当前帐号是否存在
        User oneByAccount = userDao.findOneByAccount(user.getAccount());
        if (oneByAccount != null && StringUtil.isNotBlank(oneByAccount.getPassword())) {
            throw new CustomException("新增用户失败,该账号已存在");
        }
        // 密码以帐号+密码的形式进行AES加密
        if (user.getPassword().length() > Constant.PASSWORD_MAX_LEN) {
            throw new CustomException("密码不得超过8位");
        }
        user.setRegTime(new Date());
        String key = AesCipherUtil.enCrypto(user.getAccount() + user.getPassword());
        user.setPassword(key);
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT); //获得当前登录的用户
        //根据账户获得它具有的角色列表
        List<Role> byUser = roleDao.findByUser(account);
        //我这里一个账户只有一个角色  所以取第一个就好
        if (byUser.get(0).getRoleName().equals("superadmin")){ //若是超级管理员 那这里添加某单位的领导
            //添加的用户默认可用
            user.setEnable(1);
            //添加的员工状态默认是在岗的
            user.setWorkStatus("在岗");
            //superadmin默认只会添加部门领导 所以这里直接塞职位为部门领导 关键在于给哪个部门新增领导
            user.setDuty("单位领导");
            //取出传过来的部门id organId 判断这个organId在数据库中是否存在(是否合理)
            Integer organId = user.getOrganId();
            Organization byId = organDao.findById(organId);
            if (byId==null){
                throw new CustomException("部门Id传入有误,该部门不存在");
            }
            //取出该单位所有员工  同一单位员工工号不允许重复
            List<User> byOrganId = userDao.findByOrganId(organId);
            for (User user1:
            byOrganId) {
                if (user1.getWorkId().equals(user.getWorkId())){    //员工工号重复
                    throw new CustomException("该部门员工编号已存在,请重新输入");
                }
            }
            userDao.addUser(user);
            Integer userId = userDao.findOneByAccount(user.getAccount()).getId();
            UserRole userRole =new UserRole();
            userRole.setUserId(userId).setRoleId(2);//因为是超级管理员 所以我默认给他添加单位领导角色了
            userRoleDao.addUser_Role(userRole);
            return new ResultBean(HttpStatus.OK.value(), "新增成功(Insert Success)");
        }
        if (byUser.get(0).getRoleName().equals("admin")){
            //这里进来的是部门领导 只允许添加安全员和电工用户
            if (roleId<=2){
                throw new CustomException("给新增用户设置角色失败(设置权限等级过高)");
            }
            //这里organId直接获取 前端传错了也没关系
            Integer organIDBy = userDao.findOrganIDBy(account);
            //将要插入的用户信息中的单位id和 操作人的单位id一致
            user.setOrganId(organIDBy);
            //添加的用户默认可用
            user.setEnable(1);
            //默认在岗
            user.setWorkStatus("在岗");
            if (roleId==3){//插入的是安全员
                user.setDuty("安全员");
            }
            if (roleId==4){
                user.setDuty("电工");
            }
            //取出该单位所有员工  同一单位员工工号不允许重复
            List<User> byOrganId = userDao.findByOrganId(organIDBy);
            for (User user1:
                    byOrganId) {
                if (user1.getWorkId().equals(user.getWorkId())){    //员工工号重复
                    throw new CustomException("该部门员工编号已存在,请重新输入");
                }
            }
            userDao.addUser(user);
            Integer userId = userDao.findOneByAccount(user.getAccount()).getId();
            UserRole userRole =new UserRole();
            userRole.setUserId(userId).setRoleId(roleId);//这里 单位管理员可以添加 两个角色  角色id前端传入
            userRoleDao.addUser_Role(userRole);
            return new ResultBean(HttpStatus.OK.value(), "新增成功(Insert Success)");
        }
        return null;
    }

    /**
     * 修改用户信息  默认superadmin修改admin用户   admin修改单位员工账户
     * @param userUp
     * @return
     */
    @Transactional
    public ResultBean updateUser(UserUp userUp){
        //修改之前的用户信息
        User byUserId = userDao.findByUserId(userUp.getId());
        if (byUserId.getAccount().equals(userUp.getAccount())){ //修改之后的账户和原来账户名的相同 允许
            userUp.setAccount(byUserId.getAccount());
        }else{
            // 修改之后的账户名不能重复
            User oneByAccount = userDao.findOneByAccount(userUp.getAccount());
            if (oneByAccount != null && StringUtil.isNotBlank(oneByAccount.getPassword())) {
                throw new CustomException("该账户名已存在,修改失败");
            }
        }
        //修改密码 工号 职务的时候需要注意  默认用户的所属的单位id organId不可修改
        // 密码以帐号+密码的形式进行AES加密
        if (userUp.getPassword().length() > Constant.PASSWORD_MAX_LEN) {
            throw new CustomException("密码不得超过8位");
        }
        String key = AesCipherUtil.enCrypto(userUp.getAccount() + userUp.getPassword());
        userUp.setPassword(key);
        //修改工号  工号在单位中不允许重复
        //获取要修改的用户的单位id
        Integer organId = byUserId.getOrganId();
        //该单位下的员工列表
        List<User> byOrganId = userDao.findByOrganId(organId);
        //修改之前的工号  和之前工号一样的话是允许修改保存的
        String workId =byUserId.getWorkId();
        if (userUp.getWorkId().equals(workId)){ //工号跟原来一样 原则可以
            userUp.setWorkId(workId);   //工号不变
        }else { //和原来工号不一样时
            for (User user:
                 byOrganId) {
                if (user.getWorkId().equals(userUp.getWorkId())){
                    throw new CustomException("该工号在该单位中已存在,请重新输入");
                }
            }
        }
        //修改职务
        //拿到当前登录的账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT); //获得当前登录的用户
        //根据账户获得它具有的角色列表
        List<Role> byUser = roleDao.findByUser(account);
        //我这里一个账户只有一个角色  所以取第一个就好
        if (byUser.get(0).getRoleName().equals("superadmin")){ //超级管理员 他有三个选择 将用户职务修改成单位领导\安全员\电工
            //获得传进来的职务
            String duty = userUp.getDuty();
            //用户原来的职务
            String dutyOri = byUserId.getDuty();
            if (duty.equals(dutyOri)){ //若和原来的职务相等  则用户角色不做修改 直接保存即可
                userUp.setDuty(dutyOri);
                int update = userDao.update(userUp);
                if (update<=0){
                    throw new CustomException("修改失败");
                }
                return new ResultBean(200, "修改成功", null);
            }else{  //若和原来职务不一样  那么用户角色就要做修改了
                if (duty.equals("单位领导")){
                    UserRole userRole = new UserRole();
                    userRole.setUserId(byUserId.getId());
                    userRole.setRoleId(2);
                    userRoleDao.updateUser_Role(userRole);
                }else
                if (duty.equals("安全员")){
                    UserRole userRole = new UserRole();
                    userRole.setUserId(byUserId.getId());
                    userRole.setRoleId(3);
                    userRoleDao.updateUser_Role(userRole);
                }else
                if (duty.equals("电工")){
                    UserRole userRole = new UserRole();
                    userRole.setUserId(byUserId.getId());
                    userRole.setRoleId(4);
                    userRoleDao.updateUser_Role(userRole);
                }else{
                    throw new CustomException("该职务不存在");
                }
                int update = userDao.update(userUp);
                if (update<=0){
                    throw new CustomException("修改失败");
                }
                return new ResultBean(200, "修改成功", null);

            }
        }
        if (byUser.get(0).getRoleName().equals("admin")){ //单位领导 他有2个选择 将用户职务修改成安全员\电工
            //获得传进来的职务
            String duty = userUp.getDuty();
            //用户原来的职务
            String dutyOri = byUserId.getDuty();
            if (duty.equals(dutyOri)){ //若和原来的职务相等  则用户角色不做修改 直接保存即可
                userUp.setDuty(dutyOri);
                int update = userDao.update(userUp);
                if (update<=0){
                    throw new CustomException("修改失败");
                }
                return new ResultBean(200, "修改成功", null);
            }else{  //若和原来职务不一样  那么用户角色就要做修改了
                if (duty.equals("安全员")){
                    UserRole userRole = new UserRole();
                    userRole.setUserId(byUserId.getId());
                    userRole.setRoleId(3);
                    userRoleDao.updateUser_Role(userRole);
                }else
                if (duty.equals("电工")){
                    UserRole userRole = new UserRole();
                    userRole.setUserId(byUserId.getId());
                    userRole.setRoleId(4);
                    userRoleDao.updateUser_Role(userRole);
                }else{
                    throw new CustomException("该职务不存在");
                }
                int update = userDao.update(userUp);
                if (update<=0){
                    throw new CustomException("修改失败");
                }
                return new ResultBean(200, "修改成功", null);

            }

        }


        return null;
    }

}
