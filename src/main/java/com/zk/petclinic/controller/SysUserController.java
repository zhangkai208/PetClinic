package com.zk.petclinic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.util.JWTUtil;
import com.zk.petclinic.util.RedisUtil;
import com.zk.petclinic.util.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/sysUser")
@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 新增用户（后台管理专用）
     */
    @PostMapping("/create")
    public ResultUtil<String> create(@Validated @RequestBody SysUser sysUser) {
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
        return saved ? ResultUtil.success("新增成功") : ResultUtil.fail("新增失败");
    }

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

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public ResultUtil<SysUser> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResultUtil.fail("用户ID无效");
        }
        try {
            SysUser user = sysUserService.getById(id);
            if (user == null) {
                return ResultUtil.fail("用户不存在");
            }
            user.setPassword(null);
            return ResultUtil.success(user);
        } catch (Exception e) {
            return ResultUtil.fail("查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public ResultUtil<Page<SysUser>> page(@RequestParam(defaultValue = "1") long pageNo,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        Page<SysUser> page = sysUserService.page(new Page<>(pageNo, pageSize));
        page.getRecords().forEach(u -> u.setPassword(null));
        return ResultUtil.success(page);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResultUtil<String> update(@PathVariable Long id, @RequestBody SysUser sysUser) {
        SysUser existingUser = sysUserService.getById(id);
        if (existingUser == null) {
            return ResultUtil.fail("用户不存在");
        }
        sysUser.setId(id);
        sysUser.setUpdateTime(new Date());
        boolean updated = sysUserService.updateById(sysUser);
        return updated ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }

    /**
     * 删除单个用户
     */
    @DeleteMapping("/{id}")
    public ResultUtil<String> delete(@PathVariable Long id) {
        boolean removed = sysUserService.removeById(id);
        return removed ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public ResultUtil<String> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResultUtil.fail("请选择要删除的用户");
        }
        boolean removed = sysUserService.removeBatchByIds(ids);
        return removed ? ResultUtil.success("批量删除成功") : ResultUtil.fail("批量删除失败");
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

        String tokenkey = "token:user:"+user.getId();
        boolean tokenSaved = redisUtil.set(tokenkey,token,7 * 24 * 3600);
        String userInfokey = "userInfo:"+user.getId();
        boolean userSaved = redisUtil.set(userInfokey,user,7 * 24 * 3600);

        if (!tokenSaved || !userSaved) {
            return ResultUtil.fail("登录失败：系统缓存服务不可用，请联系管理员");
        }
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
    @PostMapping("/logout")
    public ResultUtil<String> logout(HttpServletRequest request) {
        // 从 Authorization header 读取 Bearer Token
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // 去掉 "Bearer " 前缀
            String token = bearerToken.substring(7);
            Long id = JWTUtil.getUserIdFromToken(token);
            
            if (id != null) {
                // 删除 Redis 中的 Token 和用户信息
                redisUtil.delete("token:user:" + id);
                redisUtil.delete("userInfo:" + id);
            }
        }
        
        return ResultUtil.success("退出登录成功");
    }
}
