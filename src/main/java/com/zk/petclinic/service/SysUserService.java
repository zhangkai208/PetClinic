package com.zk.petclinic.service;

import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.domain.dto.LoginResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* @author 张恺
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2025-11-12 14:33:48
*/
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser findByUserName(String username);

    /**
     * 创建用户（后台管理专用）
     */
    boolean createUser(SysUser sysUser);

    /**
     * 用户注册
     */
    boolean registerUser(SysUser sysUser);

    /**
     * 根据ID获取用户（隐藏密码）
     */
    SysUser getUserById(Long id);

    /**
     * 分页查询用户（隐藏密码）
     */
    Page<SysUser> pageUsers(long pageNo, long pageSize);

    /**
     * 更新用户
     */
    boolean updateUser(Long id, SysUser sysUser);

    /**
     * 用户登录
     */
    LoginResult login(String username, String password);

    /**
     * 用户退出登录
     */
    void logout(Long userId);

    /**
     * 上传头像
     */
    String uploadAvatar(MultipartFile file) throws IOException;
}
