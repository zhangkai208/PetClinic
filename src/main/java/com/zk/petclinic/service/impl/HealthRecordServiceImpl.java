package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.HealthRecord;
import com.zk.petclinic.service.HealthRecordService;
import com.zk.petclinic.mapper.HealthRecordMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 张恺
* @description 针对表【health_record(健康记录表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:34
*/
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord>
    implements HealthRecordService{

    @Override
    public Page<HealthRecord> pageRecord(long pageNo, long pageSize,long petId) {
        LambdaQueryWrapper<HealthRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthRecord::getPetId,petId);
        Page<HealthRecord> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean createRecord(HealthRecord healthRecord,long petId) {
        healthRecord.setPetId(petId);
        healthRecord.setCreateTime(new Date());
        return this.save(healthRecord);
    }

    @Override
    public boolean updateRecord(HealthRecord healthRecord, long id) {
        healthRecord.setId(id);
        return this.updateById(healthRecord);
    }

    @Override
    public boolean deleteRecord(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public List<HealthRecord> listRecord() {
        return this.list();
    }

    @Override
    public List<HealthRecord> getRecordsByPetId(Long petId) {
        LambdaQueryWrapper<HealthRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthRecord::getPetId,petId);
        return this.list(queryWrapper);
    }
}




