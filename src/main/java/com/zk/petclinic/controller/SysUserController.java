package com.zk.petclinic.controller;

import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.util.JWTUtil;
import com.zk.petclinic.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/sysUser")
@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/register")
    public ResultUtil<String> register(@Validated @RequestBody SysUser sysUser) {
        SysUser existingUser = sysUserService.findByUserName(sysUser.getUsername());
        if (existingUser != null) {
            return ResultUtil.fail("用户名已存在");
        }
        sysUser.setId(null);
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        if (sysUser.getStatus() == null) {
            sysUser.setStatus(1);
        }
        boolean saved = sysUserService.save(sysUser);
        if (!saved) {
            return ResultUtil.fail("注册失败，请稍后再试");
        }
        return ResultUtil.success("注册成功");
    }

    @PostMapping("/login")
    public ResultUtil<Map<String, Object>> login(@Validated @RequestBody SysUser sysUser) {
        String username = sysUser.getUsername();
        String password = sysUser.getPassword();
        SysUser user = sysUserService.findByUserName(username);
        if(user == null) {
            return ResultUtil.fail("用户名不存在");
        }
        if(!password.equals(user.getPassword())) {
            return ResultUtil.fail("密码错误");
        }
        
        // 生成JWT token
        String token = JWTUtil.generateLoginToken(username, user.getId(), user.getRoleType());
        
        // 返回token和用户基本信息（不包含密码）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("roleType", user.getRoleType());
        result.put("avatar", user.getAvatar());
        
        return ResultUtil.success(result);
    }
}
