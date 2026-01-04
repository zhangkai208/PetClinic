package com.zk.petclinic.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.ServiceProvider;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.service.ServiceProviderService;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.enums.SysUserRoleType;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/serviceProviders")
@RestController
public class ServiceProviderController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ServiceProviderService serviceProviderService;
    @PostMapping("/create")
    public ResultUtil create(@RequestBody ServiceProvider serviceProvider) {
        String userIdStr = ThreadLocalUtil.get();
        Long userId = userIdStr != null ? Long.valueOf(userIdStr) : null;
        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            return ResultUtil.fail("用户未登录或不存在");
        }
        if (SysUserRoleType.PROVIDER.getValue().equals(user.getRoleType()) 
                || SysUserRoleType.ADMIN.getValue().equals(user.getRoleType())){
          boolean saved = serviceProviderService.create(serviceProvider,userId);
          return saved ? ResultUtil.success("创建成功") : ResultUtil.fail("创建失败");
        }else {
            return ResultUtil.fail("账号权限非服务商或管理员");
        }
    }
    @GetMapping("/page")
    public ResultUtil<Page<ServiceProvider>> page (@RequestParam(defaultValue = "1") long pageNo,
                                                   @RequestParam(defaultValue = "10") long pageSize
    ){
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        Long userId = Long.valueOf(userIdStr);
        Page<ServiceProvider> page = serviceProviderService.pageServiceProvider(pageNo,pageSize,userId);
        return ResultUtil.success(page);
    }

    @GetMapping("/list")
    public ResultUtil<List<ServiceProvider>> list(){
        List<ServiceProvider> serviceProviders = serviceProviderService.listServiceProvider();
        return ResultUtil.success(serviceProviders);
    }

    @PutMapping("/update")
    public ResultUtil update(@RequestBody ServiceProvider serviceProvider){
        boolean updated = serviceProviderService.updateServiceProvider(serviceProvider);
        return updated ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }
    @DeleteMapping
    public ResultUtil delete(@RequestBody List<Long> ids){
        boolean deleted = serviceProviderService.deleteServiceProvider(ids);
        return deleted ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }
}
