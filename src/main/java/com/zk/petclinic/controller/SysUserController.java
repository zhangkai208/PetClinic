package com.zk.petclinic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.domain.dto.LoginResult;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.util.JWTUtil;
import com.zk.petclinic.util.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/sysUser")
@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 新增用户（后台管理专用）
     */
    @PostMapping("/create")
    public ResultUtil<String> create(@Validated @RequestBody SysUser sysUser) {
        SysUser existingUser = sysUserService.findByUserName(sysUser.getUsername());
        if (existingUser != null) {
            return ResultUtil.fail("用户名已存在");
        }
        boolean saved = sysUserService.createUser(sysUser);
        return saved ? ResultUtil.success("新增成功") : ResultUtil.fail("新增失败");
    }

    @PostMapping("/register")
    public ResultUtil<String> register(@Validated @RequestBody SysUser sysUser) {
        SysUser existingUser = sysUserService.findByUserName(sysUser.getUsername());
        if (existingUser != null) {
            return ResultUtil.fail("用户名已存在");
        }
        boolean saved = sysUserService.registerUser(sysUser);
        return saved ? ResultUtil.success("注册成功") : ResultUtil.fail("注册失败，请稍后再试");
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
            SysUser user = sysUserService.getUserById(id);
            if (user == null) {
                return ResultUtil.fail("用户不存在");
            }
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
        Page<SysUser> page = sysUserService.pageUsers(pageNo, pageSize);
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
        boolean updated = sysUserService.updateUser(id, sysUser);
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
    public ResultUtil<LoginResult> login(@Validated @RequestBody SysUser sysUser) {
        LoginResult result = sysUserService.login(sysUser.getUsername(), sysUser.getPassword());
        if (!result.isSuccess()) {
            return ResultUtil.fail(result.getMessage());
        }
        return ResultUtil.success(result);
    }

    @PostMapping("/logout")
    public ResultUtil<String> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Long id = JWTUtil.getUserIdFromToken(token);
            sysUserService.logout(id);
        }
        return ResultUtil.success("退出登录成功");
    }

    @PostMapping("/upload")
    public ResultUtil<String> upload(final MultipartFile file) throws IOException {
        String url = sysUserService.uploadAvatar(file);
        ResultUtil<String> result = new ResultUtil<>();
        result.setCode(200);
        result.setMessage("上传成功");
        result.setData(url);
        return result;
    }
}
