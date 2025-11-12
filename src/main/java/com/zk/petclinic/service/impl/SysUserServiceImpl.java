package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
* @author 张恺
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:48
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

}




