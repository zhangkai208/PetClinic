package com.zk.petclinic.service;

import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.enums.Petgender;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
* @author 张恺
* @description 针对表【pet(宠物表)】的数据库操作Service
* @createDate 2025-11-12 14:33:40
*/
public interface PetService extends IService<Pet> {

    /**
     * 分页查询当前用户的宠物
     */
    Page<Pet> pagePetsByOwner(long pageNo, long pageSize, Long ownerId);

    /**
     * 创建宠物
     */
    boolean createPet(Pet pet, Long ownerId);

    /**
     * 更新宠物
     */
    boolean updatePet(Pet pet);

    /**
     * 批量删除宠物，返回不存在的宠物ID，如果全部存在则返回null
     */
    Long validateAndDeleteBatch(List<Long> ids);

    /**
     * 上传宠物图片
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 根据性别查询当前用户的宠物
     */
    List<Pet> findByGender(Petgender gender, Long ownerId);
}
