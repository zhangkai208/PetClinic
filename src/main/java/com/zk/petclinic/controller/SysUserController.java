package com.zk.petclinic.controller;

import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sysUser")
@Controller
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public ResultUtil<SysUser> login(@Validated SysUser sysUser) {
        String username = sysUser.getUsername();
        String password = sysUser.getPassword();
        SysUser user = sysUserService.findByUserName(username);
        if(user == null) {
            return ResultUtil.fail("用户名不存在");
        }
        if(!password.equals(user.getPassword())) {
            return ResultUtil.fail("密码错误");
        }
        return ResultUtil.success(user);
    }
}
