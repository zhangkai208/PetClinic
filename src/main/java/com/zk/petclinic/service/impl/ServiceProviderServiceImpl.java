package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.ServiceProvider;
import com.zk.petclinic.mapper.ServiceProviderMapper;
import com.zk.petclinic.service.ServiceProviderService;
import org.springframework.stereotype.Service;

/**
* @author 张恺
* @description 针对表【service_provider(服务商表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:44
*/
@Service
public class ServiceProviderServiceImpl extends ServiceImpl<ServiceProviderMapper, ServiceProvider>
    implements ServiceProviderService {

}




