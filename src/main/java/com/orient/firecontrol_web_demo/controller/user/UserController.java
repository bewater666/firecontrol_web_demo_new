package com.orient.firecontrol_web_demo.controller.user;

import com.orient.firecontrol_web_demo.config.aop.MyLog;
import com.orient.firecontrol_web_demo.config.exception.CustomException;
import com.orient.firecontrol_web_demo.config.exception.CustomUnauthorizedException;
import com.orient.firecontrol_web_demo.config.jwt.JwtUtil;
import com.orient.firecontrol_web_demo.config.page.PageBean;
import com.orient.firecontrol_web_demo.config.page.PageCommons;
import com.orient.firecontrol_web_demo.config.password.AesCipherUtil;
import com.orient.firecontrol_web_demo.config.redis.JedisUtil;
import com.orient.firecontrol_web_demo.dao.user.RoleDao;
import com.orient.firecontrol_web_demo.dao.user.UserDao;
import com.orient.firecontrol_web_demo.dao.user.UserRoleDao;
import com.orient.firecontrol_web_demo.model.common.Constant;
import com.orient.firecontrol_web_demo.model.common.ResultBean;
import com.orient.firecontrol_web_demo.model.user.*;
import com.orient.firecontrol_web_demo.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/21 16:48
 * @func
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * RefreshToken过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;


    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;


//    /**
//     * @func  查询所有用户列表接口
//     * @param pageUtils
//     * @return
//     */
//    @GetMapping("/list")
//    @RequiresAuthentication
//    public ResultBean listAllUser(@Validated PageUtils pageUtils){
//        if (pageUtils.getPage()==null || pageUtils.getRows()==null){
//            pageUtils.setPage(1);
//            pageUtils.setRows(10);
//        }
//        PageHelper.startPage(pageUtils.getPage(),pageUtils.getRows());
//        List<User> userList = userDao.findAll();
//        PageInfo<User> pageInfo = new PageInfo<>(userList);
//        if(userList.size()==0){
//            throw new CustomException("查询失败,当前数据库中无用户信息");
//        }
//        Map<String, Object> result = new HashMap<String, Object>();
//        result.put("count", pageInfo.getTotal());
//        result.put("data", pageInfo.getList());
//        return new ResultBean(HttpStatus.OK.value(), "查询成功", result);
//    }

    /**
     * 用户管理接口
     * superadmin admin权限   superadmin可以看全部人员信息 admin可以看自己部门下的人员讯息
     * @return
     */
    @GetMapping("/view/{currentPage}/{pageSize}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "用户管理接口",notes = "superadmin admin权限   superadmin可以看全部人员信息 admin可以看自己部门下的人员讯息")
//    @MyLog(description = "用户管理")
    public ResultBean listUser(@PathVariable("currentPage")@ApiParam(name = "currentPage",value = "当前页码",required = true) Integer currentPage,
                               @PathVariable("pageSize")@ApiParam(name = "pageSize",value = "每页条数",required = true) Integer pageSize){
        PageBean<User> pageBean = userService.listUser(currentPage, pageSize);
        List<User> items = pageBean.getItems();
        int thisPageNum = PageCommons.getThisPageNum(currentPage, pageSize, pageBean);
        Map map = new HashMap();
        map.put("currentPage",currentPage);
        map.put("thisPageNum",thisPageNum);
        map.put("totalNum", pageBean.getTotalNum());
        map.put("userList",items);
        return  new ResultBean(200,"查询成功",map);

    }


    /**
     * @func 管理员新增用户操作
     * 超级管理员添加的是单位领导(默认) 赋予添加的员工admin角色  需要传organId
     * 单位领导添加的默认是该单位下的员工  不要要穿organId 从登录的账户中获得  员工的角色后期自己赋予
     * @param user
     * @return
     */
    @PostMapping("/add")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "新增用户接口",notes = "超级管理员添加的是单位领导(默认) 赋予添加的员工admin角色  需要传organId  " +
            "单位领导添加的默认是该单位下的员工organId就不重要  员工的角色后期自己赋予")
    @MyLog(description = "新增用户")
    public ResultBean add(@RequestBody @ApiParam(name = "用户bean",value = "传入json格式",required = true) User user
                            ,@RequestParam @ApiParam(name = "roleId",value = "角色id",required = true) Integer roleId) {
        return userService.addUser(user, roleId);
    }


    /**
     * @func  用户登录接口
     * @param user
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口",notes = "用户登录接口 开放权限")
    public ResultBean login(@RequestBody @ApiParam(name = "登录用户bean",value = "传入json格式",required = true) LoginUser user, HttpServletResponse httpServletResponse) {
        // 查询数据库中的帐号信息
        User oneByAccount = userDao.findOneByAccount(user.getAccount());
        if (oneByAccount == null) {
            throw new CustomUnauthorizedException("该帐号不存在(The account does not exist.)");
        }
        // 密码进行AES解密
        String key = AesCipherUtil.deCrypto(oneByAccount.getPassword());
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(user.getAccount() + user.getPassword())) {
            // 清除可能存在的Shiro权限信息缓存
            if (JedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + user.getAccount())) {
                JedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + user.getAccount());
            }
            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getAccount(), currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
            // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
            String token = JwtUtil.sign(user.getAccount(), currentTimeMillis);
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            Map<String,String> map = new HashMap<>();
            Integer roleId = roleDao.findRoleIdByAccount(user.getAccount());
            map.put("TOKEN", token);
            map.put("roleId", String.valueOf(roleId));
            return new ResultBean(HttpStatus.OK.value(), "登录成功(Login Success.)", map);
        } else {
            throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
        }
    }


    /**
     * 获得在线用户接口
     * 超级管理员权限
     * @return
     */
    @GetMapping("/getOnline")
    @RequiresRoles(value = {"superadmin"})
    @ApiOperation(value = "获得在线用户接口",notes = "获得在线用户接口 超级管理员权限")
//    @MyLog(description = "查看在线用户")
    public ResultBean getOnlineUser(){
        List<UserDto> userDtoList = new ArrayList<UserDto>();
        // 查询所有Redis键
        Set<String> keys = JedisUtil.keysS(Constant.PREFIX_SHIRO_REFRESH_TOKEN + "*");
        for (String key : keys) {
            if (JedisUtil.exists(key)) {
                // 根据:分割key，获取最后一个字符(帐号)
                String[] strArray = key.split(":");
                UserDto userDto = new UserDto();
                String account = strArray[strArray.length - 1];
                User oneByAccount = userDao.findOneByAccount(account);
                userDto.setId(oneByAccount.getId()).setAccount(account).setUsername(oneByAccount.getUsername()).setRegTime(oneByAccount.getRegTime());
                // 设置登录时间
                userDto.setLoginTime(new Date(Long.parseLong(JedisUtil.getObject(key).toString())));
                userDtoList.add(userDto);
            }
        }
        if (userDtoList == null || userDtoList.size() <= 0) {
            throw new CustomException("查询失败(Query Failure)");
        }
        return new ResultBean(HttpStatus.OK.value(), "查询成功(Query was successful)", userDtoList);
    }


    /**
     * 更改用户状态
     * superadmin admin权限
     * 本来只用自己部门的领导才能更改自己员工状态 但是考虑页面中 能进去员工列表的只有superadmin 和自己部门的admin
     * 所以这里就不对admin约束到自己部门了 接口测试的时候注意即可 尽量用自己单位的admin进行操作
     * @param id
     * @return
     */
    @GetMapping("/change/{id}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @ApiOperation(value = "更改用户状态",notes = "原则上自己部门的领导才能更改自己员工状态 接口测试的时候注意即可 尽量用自己单位的admin进行操作")
    @MyLog(description = "更改用户状态")
    public ResultBean changUserStatus(@PathVariable("id") @ApiParam(name = "id",value = "用户id 从上下文获取",required = true) Integer id){
        return userService.changUserStatus(id);
    }

    /**
     * @func 剔除某个在线用户   超级管理员权限
     * @param id
     * @return
     */
    @DeleteMapping("/deleteOnlineUser/{id}")
    @RequiresRoles(value = {"superadmin"})
    @ApiOperation(value = "剔除某个在线用户",notes = "超级管理员权限")
    @MyLog(description = "剔除在线用户")
    public ResultBean deleteOnlineUser(@PathVariable("id") @ApiParam(value = "用户id",name = "id",required = true) Integer id){
        User byUserId = userDao.findByUserId(id);
        if (byUserId==null){
            throw new CustomException("该用户不存在");
        }
        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + byUserId.getAccount())) {
            if (JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + byUserId.getAccount()) > 0) {
                return new ResultBean(HttpStatus.OK.value(), "踢除成功(Delete Success)", null);
            }
        }
        throw new CustomException("踢除失败，该账号并不在线(Delete failed. The account is not online.)");
    }

    /**
     * 查看该用户下的 角色信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "查看用户的角色信息",notes = "查看该用户下的 角色信息")
    @GetMapping("/checkRoles/{id}")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
//    @MyLog(description = "查看用户角色")
    public ResultBean checkRoles(@PathVariable("id") @ApiParam(value = "用户id",name = "id",required = true) Integer userId){
        return userService.checkRoles(userId);
    }

    /**
     * 保存用户角色接口
     * @param userRole
     * @return
     */
    @ApiOperation(value = "保存用户角色接口",notes = "保存用户角色接口")
    @PostMapping("/saveRole")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    @MyLog(description = "修改用户角色")
    public ResultBean saveUserRole(@RequestBody @ApiParam(value = "传入json格式",name = "UserRole实体类",required = true) UserRole userRole){
        return userService.saveUserRole(userRole);
    }

    @ApiOperation(value = "修改用户",notes = "修改用户信息接口")
    @PutMapping("/update")
    @MyLog(description = "修改用户信息")
    @RequiresRoles(value = {"superadmin","admin"},logical = Logical.OR)
    public ResultBean updateUser(@RequestBody @ApiParam(value = "传入json格式",name = "user实体类",required = true) UserUp userUp){
        return userService.updateUser(userUp);
    }

    @ApiOperation(value = "用户登出",notes = "用户登出接口,需登录后访问")
    @GetMapping("/loginOut")
    @RequiresAuthentication
    public ResultBean loginOut(){
        //获得当前登录的账户
        String account = JwtUtil.getClaim(SecurityUtils.getSubject().getPrincipals().toString(), Constant.ACCOUNT);
        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            if (JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account) > 0) {
                return new ResultBean(HttpStatus.OK.value(), account+"登出成功", null);
            }
        }
        throw new CustomException("该用户登录已过期");
    }
}



