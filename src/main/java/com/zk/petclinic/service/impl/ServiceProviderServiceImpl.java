package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.domain.ServiceProvider;
import com.zk.petclinic.enums.ServiceProviderStatus;
import com.zk.petclinic.mapper.ServiceProviderMapper;
import com.zk.petclinic.service.ServiceProviderService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 张恺
* @description 针对表【service_provider(服务商表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:44
*/
@Service
public class ServiceProviderServiceImpl extends ServiceImpl<ServiceProviderMapper, ServiceProvider>
    implements ServiceProviderService {

    @Override
    public boolean create(ServiceProvider serviceProvider, Long userId) {
        serviceProvider.setUserId(userId);
        serviceProvider.setStatus(ServiceProviderStatus.PENDING_REVIEW);
        serviceProvider.setCreateTime(new Date());
        return this.save(serviceProvider);
    }

    @Override
    public Page<ServiceProvider> pageServiceProvider(long pageNo, long pageSize) {
        Page<ServiceProvider> serviceProviderPage = new Page<>(pageNo, pageSize);
        return this.page(serviceProviderPage);
    }

    @Override
    public boolean updateServiceProvider(ServiceProvider serviceProvider) {
        return this.updateById(serviceProvider);
    }

    @Override
    public boolean deleteServiceProvider(List<Long> ids) {
        return this.removeByIds(ids);
    }
}




