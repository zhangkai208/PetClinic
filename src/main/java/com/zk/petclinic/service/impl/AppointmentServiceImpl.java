package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.Appointment;
import com.zk.petclinic.mapper.AppointmentMapper;
import com.zk.petclinic.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 张恺
* @description 针对表【appointment(预约表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:20
*/
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment>
    implements AppointmentService {

    @Override
    public Page<Appointment> pageAppointment(long pageNo, long pageSize,long petId,long providerId) {
        Page<Appointment> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getPetId, petId)
                .eq(Appointment::getProviderId, providerId);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean createAppointment(Appointment appointment, long petId, long providerId) {
        appointment.setPetId(petId);
        appointment.setProviderId(providerId);
        appointment.setCreateTime(new Date());
        return this.save(appointment);
    }

    @Override
    public boolean updateAppointment(Appointment appointment, long id) {
        appointment.setId(id);
        return this.updateById(appointment);
    }

    @Override
    public boolean deleteAppointment(List<Long> ids) {
        return this.removeByIds(ids);
    }
}




