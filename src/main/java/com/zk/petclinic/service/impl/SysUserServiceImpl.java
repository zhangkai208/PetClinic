package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.domain.dto.LoginResult;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.mapper.SysUserMapper;
import com.zk.petclinic.util.JWTUtil;
import com.zk.petclinic.util.QiniuOssUtil;
import com.zk.petclinic.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author 张恺
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:48
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public SysUser findByUserName(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean createUser(SysUser sysUser) {
        sysUser.setId(null);
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        if (sysUser.getStatus() == null) {
            sysUser.setStatus(1);
        }
        return this.save(sysUser);
    }

    @Override
    public boolean registerUser(SysUser sysUser) {
        sysUser.setId(null);
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        if (sysUser.getStatus() == null) {
            sysUser.setStatus(1);
        }
        return this.save(sysUser);
    }

    @Override
    public SysUser getUserById(Long id) {
        SysUser user = this.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public Page<SysUser> pageUsers(long pageNo, long pageSize) {
        Page<SysUser> page = this.page(new Page<>(pageNo, pageSize));
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    @Override
    public boolean updateUser(Long id, SysUser sysUser) {
        SysUser existingUser = this.getById(id);
        if (existingUser == null) {
            return false;
        }
        sysUser.setId(id);
        sysUser.setUpdateTime(new Date());
        return this.updateById(sysUser);
    }

    @Override
    public LoginResult login(String username, String password) {
        SysUser user = findByUserName(username);
        if (user == null) {
            return LoginResult.fail("用户名不存在");
        }
        if (!password.equals(user.getPassword())) {
            return LoginResult.fail("密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            return LoginResult.fail("账号已被禁用，请联系管理员");
        }

        // 生成JWT token
        String token = JWTUtil.generateLoginToken(username, user.getId(), user.getRoleType());

        String tokenKey = "token:user:" + user.getId();
        boolean tokenSaved = redisUtil.set(tokenKey, token, 7 * 24 * 3600);
        String userInfoKey = "userInfo:" + user.getId();
        boolean userSaved = redisUtil.set(userInfoKey, user, 7 * 24 * 3600);

        if (!tokenSaved || !userSaved) {
            return LoginResult.fail("登录失败：系统缓存服务不可用，请联系管理员");
        }

        // 返回登录成功信息
        return LoginResult.success(token, user.getId(), user.getUsername(),
                user.getNickname(), user.getRoleType(), user.getAvatar());
    }

    @Override
    public void logout(Long userId) {
        if (userId != null) {
            redisUtil.delete("token:user:" + userId);
            redisUtil.delete("userInfo:" + userId);
        }
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        final String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        final String fileName = UUID.randomUUID().toString() + originalFilename.substring(0, originalFilename.lastIndexOf("."));
        return QiniuOssUtil.uploadFile(fileName, file.getInputStream());
    }

    @Override
    public List<Map<String, Object>> getUserRoleDistribution() {
        List<SysUser> allUsers = this.list();
        Map<Integer, Long> roleCount = new HashMap<>();
        
        for (SysUser user : allUsers) {
            Integer roleType = user.getRoleType() != null ? user.getRoleType() : 0;
            roleCount.put(roleType, roleCount.getOrDefault(roleType, 0L) + 1);
        }
        
        // 角色名称映射
        Map<Integer, String> roleNames = new HashMap<>();
        roleNames.put(1, "宠物主人");
        roleNames.put(2, "服务商");
        roleNames.put(3, "管理员");
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : roleCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", roleNames.getOrDefault(entry.getKey(), "未知"));
            item.put("value", entry.getValue());
            result.add(item);
        }
        return result;
    }
}
