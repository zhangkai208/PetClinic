package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.mapper.PetMapper;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.domain.Pet;
import org.springframework.stereotype.Service;

/**
* @author 张恺
* @description 针对表【pet(宠物表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:40
*/
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet>
    implements PetService {

}




