package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.Appointment;
import com.zk.petclinic.mapper.AppointmentMapper;
import com.zk.petclinic.service.AppointmentService;
import org.springframework.stereotype.Service;

/**
* @author 张恺
* @description 针对表【appointment(预约表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:20
*/
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment>
    implements AppointmentService {

}




