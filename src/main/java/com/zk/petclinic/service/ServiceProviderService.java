package com.zk.petclinic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.ServiceProvider;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 张恺
* @description 针对表【service_provider(服务商表)】的数据库操作Service
* @createDate 2025-11-12 14:33:44
*/
public interface ServiceProviderService extends IService<ServiceProvider> {

    boolean create(ServiceProvider serviceProvider, Long userId);

    Page<ServiceProvider> pageServiceProvider(long pageNo, long pageSize,long userId);

    boolean updateServiceProvider(ServiceProvider serviceProvider);

    boolean deleteServiceProvider(List<Long> ids);

    List<ServiceProvider> listServiceProvider();
}
