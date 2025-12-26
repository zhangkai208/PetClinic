package com.zk.petclinic.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.util.QiniuOssUtil;
import com.zk.petclinic.util.ResultUtil;
import com.zk.petclinic.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/pet")
@RestController
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping("/page")
    public ResultUtil<Page<Pet>> page(@RequestParam(defaultValue = "1") long pageNo,
                                      @RequestParam(defaultValue = "10") long pageSize) {
        Page<Pet> page = petService.page(new Page<>(pageNo, pageSize));
        return ResultUtil.success(page);
    }
    @PutMapping("/{id}")
    public ResultUtil<String> update(@RequestBody Pet pet,@PathVariable long id) {
        pet.setId(id);
        pet.setUpdateTime(new Date());
        boolean updated = petService.updateById(pet);
        return updated ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }
    @PostMapping("/create")
    public ResultUtil<String> create(@RequestBody Pet pet) {
        // 从ThreadLocal获取当前登录用户ID，自动设置为宠物主人
        String userIdStr = ThreadLocalUtil.get();
        if (userIdStr != null) {
            pet.setOwnerId(Long.valueOf(userIdStr));
        }
        pet.setCreateTime(new Date());
        pet.setUpdateTime(new Date());
        boolean saved = petService.save(pet);
        return saved ? ResultUtil.success("新增成功") : ResultUtil.fail("新增失败");
    }
    @DeleteMapping("/{id}")
    public ResultUtil<String> delete(@PathVariable long id) {
        boolean removed = petService.removeById(id);
        return removed ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }
    @DeleteMapping
    public ResultUtil<String> deleteBatch(@RequestBody List<Long> ids) {
        for(Long id : ids) {
            Pet pet = petService.getById(id);
            if(pet == null){
                return ResultUtil.fail("宠物不存在");
            }
        }
        boolean removed = petService.removeBatchByIds(ids);
        return removed ? ResultUtil.success("批量删除成功") : ResultUtil.fail("批量删除失败");
    }
    @PostMapping("/upload")
    public ResultUtil<String> upload(final MultipartFile file) throws IOException {
        //获取文件名
        final String originalFilename = file.getOriginalFilename();
        //判断不能为空
        assert originalFilename != null;
        //获取文件名，例如1.jpg，获取1
        final String fileName = UUID.randomUUID().toString() + originalFilename.substring(0, originalFilename.lastIndexOf("."));
        //上传到七牛云
        final String url = QiniuOssUtil.uploadFile(fileName, file.getInputStream());
        // 注意：不能直接使用 ResultUtil.success(url)，因为会匹配到 success(String message) 方法
        ResultUtil<String> result = new ResultUtil<>();
        result.setCode(200);
        result.setMessage("上传成功");
        result.setData(url);
        return result;
    }
}
