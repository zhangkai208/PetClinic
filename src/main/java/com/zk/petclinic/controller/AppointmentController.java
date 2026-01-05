package com.zk.petclinic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.Appointment;
import com.zk.petclinic.service.AppointmentService;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.service.ServiceProviderService;
import com.zk.petclinic.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/appointment")
@RestController
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ServiceProviderService serviceProviderService;
    @Autowired
    private PetService petService;
    @GetMapping("/page")
    public ResultUtil<Page<Appointment>> page(@RequestParam long pageNo
            ,@RequestParam long pageSize
            ,@RequestParam long petId
            ,@RequestParam long providerId
    ){
        Page<Appointment> appointment = appointmentService.pageAppointment(pageNo, pageSize,petId,providerId);
        return ResultUtil.success(appointment);
    }
    @PostMapping("/create")
    public ResultUtil<String> create(@RequestBody Appointment appointment
            ,@RequestParam long petId
            ,@RequestParam long providerId
    ){
        boolean create = appointmentService.createAppointment(appointment, petId, providerId);
        return create ? ResultUtil.success("创建成功") : ResultUtil.fail("创建失败");
    }
    @PutMapping("/{id}")
    public ResultUtil<String> update(@RequestBody Appointment appointment, @PathVariable long id){
        boolean create = appointmentService.updateAppointment(appointment,id);
        return create ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }
    @DeleteMapping("/delete")
    public ResultUtil<String> delete(@RequestBody List<Long> ids){
        boolean create = appointmentService.deleteAppointment(ids);
        return create ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }
}
