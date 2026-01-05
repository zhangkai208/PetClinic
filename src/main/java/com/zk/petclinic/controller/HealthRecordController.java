package com.zk.petclinic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.HealthRecord;
import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.enums.SysUserRoleType;
import com.zk.petclinic.service.HealthRecordService;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/healthRecord")
@RestController
public class HealthRecordController {
    @Autowired
    private HealthRecordService healthRecordService;
    
    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private PetService petService;

    /**
     * 分页查询健康记录
     * 宠物主人只能查询自己宠物的记录，服务商和管理员可以查询所有
     */
    @GetMapping("/page")
    public ResultUtil<Page<HealthRecord>> page(@RequestParam(defaultValue = "1") long pageNo,
                                               @RequestParam(defaultValue = "10") long pageSize,
                                               @RequestParam long petId) {
        // 权限校验
        ResultUtil<String> checkResult = checkPetPermission(petId);
        if (checkResult != null) {
            return ResultUtil.fail(checkResult.getMessage());
        }
        
        Page<HealthRecord> pageRecord = healthRecordService.pageRecord(pageNo, pageSize, petId);
        return ResultUtil.success(pageRecord);
    }

    /**
     * 创建健康记录
     */
    @PostMapping("/create")
    public ResultUtil<String> create(@RequestBody HealthRecord healthRecord, 
                                     @RequestParam long petId) {
        // 权限校验
        ResultUtil<String> checkResult = checkPetPermission(petId);
        if (checkResult != null) {
            return checkResult;
        }
        
        boolean record = healthRecordService.createRecord(healthRecord, petId);
        return record ? ResultUtil.success("创建成功") : ResultUtil.fail("创建失败");
    }

    /**
     * 更新健康记录
     */
    @PutMapping("/{id}")
    public ResultUtil<String> update(@RequestBody HealthRecord healthRecord, 
                                     @PathVariable long id) {
        // 先获取原记录，校验权限
        HealthRecord existingRecord = healthRecordService.getById(id);
        if (existingRecord == null) {
            return ResultUtil.fail("记录不存在");
        }
        
        ResultUtil<String> checkResult = checkPetPermission(existingRecord.getPetId());
        if (checkResult != null) {
            return checkResult;
        }
        
        boolean record = healthRecordService.updateRecord(healthRecord, id);
        return record ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }

    /**
     * 删除健康记录
     */
    @DeleteMapping("/delete")
    public ResultUtil<String> delete(@RequestBody List<Long> ids) {
        // 校验所有记录的权限
        for (Long id : ids) {
            HealthRecord existingRecord = healthRecordService.getById(id);
            if (existingRecord == null) {
                return ResultUtil.fail("记录ID " + id + " 不存在");
            }
            ResultUtil<String> checkResult = checkPetPermission(existingRecord.getPetId());
            if (checkResult != null) {
                return checkResult;
            }
        }
        
        boolean record = healthRecordService.deleteRecord(ids);
        return record ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }

    /**
     * 检查当前用户是否有权限访问指定宠物的数据
     * @param petId 宠物ID
     * @return 如果有权限返回 null，否则返回错误信息
     */
    private ResultUtil<String> checkPetPermission(Long petId) {
        // 获取当前登录用户
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        Long userId = Long.valueOf(userIdStr);
        
        // 获取用户信息
        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            return ResultUtil.fail("用户不存在");
        }
        
        // 服务商和管理员可以访问所有宠物的健康记录
        if (SysUserRoleType.PROVIDER.getValue().equals(user.getRoleType()) 
                || SysUserRoleType.ADMIN.getValue().equals(user.getRoleType())) {
            return null; // 有权限
        }
        
        // 宠物主人只能访问自己宠物的记录
        Pet pet = petService.getById(petId);
        if (pet == null) {
            return ResultUtil.fail("宠物不存在");
        }
        
        if (!userId.equals(pet.getOwnerId())) {
            return ResultUtil.fail("无权限访问该宠物的健康记录");
        }
        
        return null; // 有权限
    }
}
