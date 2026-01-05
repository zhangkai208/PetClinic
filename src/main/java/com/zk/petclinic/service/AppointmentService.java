package com.zk.petclinic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.Appointment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张恺
* @description 针对表【appointment(预约表)】的数据库操作Service
* @createDate 2025-11-12 14:33:20
*/
public interface AppointmentService extends IService<Appointment> {

    Page<Appointment> pageAppointment(long pageNo, long pageSize ,long petId,long providerId);

    boolean createAppointment(Appointment appointment, long petId, long providerId);

    boolean updateAppointment(Appointment appointment, long id);

    boolean deleteAppointment(List<Long> ids);
}
