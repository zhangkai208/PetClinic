package com.zk.petclinic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.HealthRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张恺
* @description 针对表【health_record(健康记录表)】的数据库操作Service
* @createDate 2025-11-12 14:33:34
*/
public interface HealthRecordService extends IService<HealthRecord> {

    Page<HealthRecord> pageRecord(long pageNo, long pageSize,long petId);

    boolean createRecord(HealthRecord healthRecord,long petId);

    boolean updateRecord(HealthRecord healthRecord, long id);

    boolean deleteRecord(List<Long> ids);

    List<HealthRecord> listRecord();
}
