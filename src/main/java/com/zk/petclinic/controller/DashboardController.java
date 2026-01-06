package com.zk.petclinic.controller;

import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.service.ServiceProviderService;
import com.zk.petclinic.service.AppointmentService;
import com.zk.petclinic.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员看板统计接口
 * @author 张恺
 */
@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private PetService petService;
    
    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private AppointmentService appointmentService;

    /**
     * 获取看板统计数据
     */
    @GetMapping("/stats")
    public ResultUtil<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户总数
        stats.put("userCount", sysUserService.count());
        // 宠物总数
        stats.put("petCount", petService.count());
        // 服务商总数
        stats.put("providerCount", serviceProviderService.count());
        // 预约总数
        stats.put("appointmentCount", appointmentService.count());
        
        return ResultUtil.success(stats);
    }

    /**
     * 获取宠物类型分布
     */
    @GetMapping("/pet-type-distribution")
    public ResultUtil<List<Map<String, Object>>> getPetTypeDistribution() {
        List<Map<String, Object>> distribution = petService.getPetTypeDistribution();
        return ResultUtil.success(distribution);
    }

    /**
     * 获取近7天预约趋势
     */
    @GetMapping("/appointment-trend")
    public ResultUtil<List<Map<String, Object>>> getAppointmentTrend() {
        List<Map<String, Object>> trend = appointmentService.getAppointmentTrend(7);
        return ResultUtil.success(trend);
    }

    /**
     * 获取用户角色分布
     */
    @GetMapping("/user-role-distribution")
    public ResultUtil<List<Map<String, Object>>> getUserRoleDistribution() {
        List<Map<String, Object>> distribution = sysUserService.getUserRoleDistribution();
        return ResultUtil.success(distribution);
    }
}
