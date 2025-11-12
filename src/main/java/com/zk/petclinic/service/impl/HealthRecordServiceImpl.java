package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.HealthRecord;
import com.zk.petclinic.service.HealthRecordService;
import com.zk.petclinic.mapper.HealthRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author 张恺
* @description 针对表【health_record(健康记录表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:34
*/
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord>
    implements HealthRecordService{

}




