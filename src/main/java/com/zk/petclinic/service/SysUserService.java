package com.zk.petclinic.service;

import com.zk.petclinic.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 张恺
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2025-11-12 14:33:48
*/
public interface SysUserService extends IService<SysUser> {

    SysUser findByUserName(String username);
}
