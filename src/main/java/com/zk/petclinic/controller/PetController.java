package com.zk.petclinic.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.util.RedisUtil;
import com.zk.petclinic.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/pet")
@Controller
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
    public ResultUtil update(@RequestBody Pet pet,@PathVariable long id) {
        boolean updated = petService.updateById(pet);
        return updated ? ResultUtil.success("更新成功") : ResultUtil.fail("更新失败");
    }
    @PostMapping("/create")
    public ResultUtil<Pet> create(@RequestBody Pet pet) {
        boolean saved = petService.save(pet);
        return saved ? ResultUtil.success("新增成功") : ResultUtil.fail("新增失败");
    }
    @DeleteMapping("/{id}")
    public ResultUtil delete(@PathVariable long id) {
        boolean removed = petService.removeById(id);
        return removed ? ResultUtil.success("删除成功") : ResultUtil.fail("删除失败");
    }
    @DeleteMapping
    public ResultUtil deleteBatch(@RequestBody List<Long> ids) {
        for(Long id : ids) {
            Pet pet = petService.getById(id);
            if(pet == null){
                return ResultUtil.fail("宠物不存在");
            }
        }
        boolean removed = petService.removeBatchByIds(ids);
        return removed ? ResultUtil.success("批量删除成功") : ResultUtil.fail("批量删除失败");
    }
}
