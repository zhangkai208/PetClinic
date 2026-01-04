package com.zk.petclinic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.enums.Petgender;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.service.SysUserService;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/pet")
@RestController
public class PetController {
    @Autowired
    private PetService petService;
    
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    public ResultUtil<Page<Pet>> page(@RequestParam(defaultValue = "1") long pageNo,
                                      @RequestParam(defaultValue = "10") long pageSize) {
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        Long userId = Long.valueOf(userIdStr);
        Page<Pet> page = petService.pagePetsByOwner(pageNo, pageSize, userId);
        return ResultUtil.success(page);
    }

    @GetMapping("/list")
    public ResultUtil<List<Pet>> list() {
        List<Pet> petList = petService.list();
        // 填充每个宠物的主人昵称
        for (Pet pet : petList) {
            if (pet.getOwnerId() != null) {
                SysUser owner = sysUserService.getById(pet.getOwnerId());
                if (owner != null) {
                    pet.setOwnerNickname(owner.getNickname() != null ? owner.getNickname() : owner.getUsername());
                }
            }
        }
        return ResultUtil.success(petList);
    }

    @PutMapping("/{id}")
    public ResultUtil<String> update(@RequestBody Pet pet, @PathVariable long id) {
        pet.setId(id);
        boolean updated = petService.updatePet(pet);
        return updated ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }

    @PostMapping("/create")
    public ResultUtil<String> create(@RequestBody Pet pet) {
        String userIdStr = ThreadLocalUtil.get();
        Long ownerId = userIdStr != null ? Long.valueOf(userIdStr) : null;
        boolean saved = petService.createPet(pet, ownerId);
        return saved ? ResultUtil.success("新增成功") : ResultUtil.fail("新增失败");
    }

    @DeleteMapping("/{id}")
    public ResultUtil<String> delete(@PathVariable long id) {
        boolean removed = petService.removeById(id);
        return removed ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }

    @DeleteMapping
    public ResultUtil<String> deleteBatch(@RequestBody List<Long> ids) {
        Long invalidId = petService.validateAndDeleteBatch(ids);
        if (invalidId != null) {
            return ResultUtil.fail("宠物不存在");
        }
        return ResultUtil.success("批量删除成功");
    }

    @PostMapping("/upload")
    public ResultUtil<String> upload(final MultipartFile file) throws IOException {
        String url = petService.uploadFile(file);
        ResultUtil<String> result = new ResultUtil<>();
        result.setCode(200);
        result.setMessage("上传成功");
        result.setData(url);
        return result;
    }

    /**
     * 根据性别查询当前用户的宠物
     * @param gender 性别枚举：MALE(公)、FEMALE(母)、UNKNOWN(未知)
     */
    @GetMapping("/gender/{gender}")
    public ResultUtil<List<Pet>> findByGender(@PathVariable Petgender gender) {
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr == null || userIdStr.isEmpty()) {
            return ResultUtil.fail("请先登录");
        }
        Long userId = Long.valueOf(userIdStr);
        List<Pet> pets = petService.findByGender(gender, userId);
        return ResultUtil.success(pets);
    }

    /**
     * 上传相册（支持一次上传多张照片）
     * @param files 多个图片文件
     * @return 上传成功的图片URL列表
     */
    @PostMapping("/uploadPhotos")
    public ResultUtil<List<String>> uploadPhotos(@RequestParam("files") MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return ResultUtil.fail("请选择要上传的图片");
        }
        List<String> urls = petService.uploadPhotos(files);
        return ResultUtil.success(urls);
    }
}
