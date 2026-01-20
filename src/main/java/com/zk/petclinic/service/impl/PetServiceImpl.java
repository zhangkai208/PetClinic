package com.zk.petclinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.petclinic.mapper.PetMapper;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.enums.PetGender;
import com.zk.petclinic.util.QiniuOssUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

/**
* @author 张恺
* @description 针对表【pet(宠物表)】的数据库操作Service实现
* @createDate 2025-11-12 14:33:40
*/
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet>
    implements PetService {

    @Override
    public Page<Pet> pagePetsByOwner(long pageNo, long pageSize, Long ownerId) {
        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Pet::getOwnerId, ownerId);
        return this.page(new Page<>(pageNo, pageSize), queryWrapper);
    }

    @Override
    public List<Pet> list(List<Pet> petList) {
        return this.list(petList);
    }

    @Override
    public boolean createPet(Pet pet, Long ownerId) {
        pet.setOwnerId(ownerId);
        pet.setCreateTime(new Date());
        pet.setUpdateTime(new Date());
        return this.save(pet);
    }

    @Override
    public boolean updatePet(Pet pet,long id) {
        pet.setId(id);
        pet.setUpdateTime(new Date());
        return this.updateById(pet);
    }

    @Override
    public Long validateAndDeleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Pet pet = this.getById(id);
            if (pet == null) {
                return id; // 返回不存在的ID
            }
        }
        this.removeBatchByIds(ids);
        return null; // 全部存在，删除成功
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        final String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        final String fileName = UUID.randomUUID().toString() + originalFilename.substring(0, originalFilename.lastIndexOf("."));
        return QiniuOssUtil.uploadFile(fileName, file.getInputStream());
    }

    @Override
    public List<Pet> findByGender(PetGender gender, Long ownerId) {
        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Pet::getOwnerId, ownerId)
                    .eq(Pet::getGender, gender);  // 直接使用枚举，MyBatis-Plus自动转换
        return this.list(queryWrapper);
    }

    @Override
    public List<String> uploadPhotos(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null) {
                    String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String fileName = UUID.randomUUID().toString() + ext;
                    String url = QiniuOssUtil.uploadFile(fileName, file.getInputStream());
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    @Override
    public List<Map<String, Object>> getPetTypeDistribution() {
        // 获取所有宠物按类型分组统计
        List<Pet> allPets = this.list();
        Map<String, Long> typeCount = new HashMap<>();
        
        for (Pet pet : allPets) {
            String type = pet.getType() != null ? pet.getType() : "未知";
            typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Pet> getPetsByUserId(Long userId) {
        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Pet::getOwnerId, userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Pet> searchPets(String keyword) {
        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Pet::getName, keyword)
                .or().like(Pet::getBreed, keyword)
                .or().like(Pet::getType, keyword);
        return this.list(queryWrapper);
    }

}
