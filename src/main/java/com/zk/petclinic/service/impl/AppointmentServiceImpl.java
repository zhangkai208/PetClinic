package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.Appointment;
import com.zk.petclinic.domain.ServiceProvider;
import com.zk.petclinic.enums.ServiceProviderStatus;
import com.zk.petclinic.mapper.AppointmentMapper;
import com.zk.petclinic.mapper.ServiceProviderMapper;
import com.zk.petclinic.service.AppointmentService;
import com.zk.petclinic.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
* @author 张恺
* @description 针对表【appointment(预约表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:20
*/
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment>
    implements AppointmentService {
    @Autowired
    private ServiceProviderService serviceProviderService;

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

        ServiceProvider id = serviceProviderService.getById(providerId);
        if (id == null || !id.getStatus().equals(ServiceProviderStatus.APPROVED)) {
            throw  new RuntimeException("该服务商未审核通过，无法预约");
        }
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

    @Override
    public List<Map<String, Object>> getAppointmentTrend(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        
        // 获取所有预约
        List<Appointment> allAppointments = this.list();
        
        // 统计每一天的预约数量
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            Calendar dayStart = Calendar.getInstance();
            dayStart.add(Calendar.DAY_OF_MONTH, -i);
            dayStart.set(Calendar.HOUR_OF_DAY, 0);
            dayStart.set(Calendar.MINUTE, 0);
            dayStart.set(Calendar.SECOND, 0);
            dayStart.set(Calendar.MILLISECOND, 0);
            
            Calendar dayEnd = (Calendar) dayStart.clone();
            dayEnd.add(Calendar.DAY_OF_MONTH, 1);
            
            long count = allAppointments.stream()
                .filter(a -> a.getCreateTime() != null)
                .filter(a -> !a.getCreateTime().before(dayStart.getTime()) 
                          && a.getCreateTime().before(dayEnd.getTime()))
                .count();
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", sdf.format(dayStart.getTime()));
            item.put("count", count);
            result.add(item);
        }
        return result;
    }
}




