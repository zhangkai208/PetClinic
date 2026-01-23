package com.zk.petclinic.tools;


import com.zk.petclinic.domain.Appointment;
import com.zk.petclinic.domain.HealthRecord;
import com.zk.petclinic.domain.Pet;
import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.service.AppointmentService;
import com.zk.petclinic.service.HealthRecordService;
import com.zk.petclinic.service.PetService;
import com.zk.petclinic.service.SysUserService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetClinicTools {
    @Autowired
    private PetService petService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private HealthRecordService healthRecordService;

    /**
     * 查询用户的所有宠物信息
     */
    @Tool(description = "根据用户ID查询该用户拥有的所有宠物信息，包括宠物名称、类型、品种等")
    public String get_user_pets(
            @ToolParam(description = "用户ID") Long userId
    ) {
        List<Pet> pets = petService.getPetsByUserId(userId);
        if (pets == null || pets.isEmpty()) {
            return "该用户没有登记的宠物";
        }
        return pets.stream()
                .map(
                        pet -> String.format(
                                "宠物ID: %d, 宠物名称: %s, 类型: %s, 品种: %s, 生日: %s, 性别: %s",
                                pet.getId(),
                                pet.getName(),
                                pet.getType(),
                                pet.getBreed(),
                                pet.getBirthDate(),
                                pet.getGender()
                                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 查询宠物的健康记录
     */
    @Tool(description = "根据宠物ID查询该宠物的所有健康记录，包括记录日期、记录类型等")
    public String get_pet_health_records(
            @ToolParam(description = "宠物ID") Long petId
    ) {
        List<HealthRecord> records = healthRecordService.getRecordsByPetId(petId);
        if (records == null || records.isEmpty()) {
            return "该宠物暂无健康记录";
        }
        return records.stream()
                .map(
                        record -> String.format(
                                "记录日期: %s, 记录类型: %s, 标题: %s, 内容: %s, 提醒日期: %s",
                                record.getRecordDate(),
                                record.getRecordType(),
                                record.getTitle(),
                                record.getContent(),
                                record.getNextDate()
                        )).collect(Collectors.joining("\n---\n"));
    }

    /**
     * 查询宠物的预约记录
     */
    @Tool(description = "根据宠物ID查询该宠物的所有预约记录，包括预约时间、服务类型、金额等")
    public String get_pet_appointments(
            @ToolParam(description = "宠物ID") Long petId
    ) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPetId(petId);
        if (appointments == null || appointments.isEmpty()) {
            return "该宠物暂无预约记录";
        }
        return appointments.stream()
                .map(
                        appointment -> String.format(
                                "预约时间: %s, 服务类型: %s, 状态: %s, 备注: %s, 金额: %s, 评价: %s",
                                appointment.getAppointmentTime(),
                                appointment.getServiceType(),
                                appointment.getStatus(),
                                appointment.getRemark(),
                                appointment.getMoney(),
                                appointment.getEvaluation()
                )).collect(Collectors.joining("\n---\n"));
    }

    /**
     * 根据宠物类型统计数量
     */
    @Tool(description = "统计系统中各类型宠物的数量")
    public String count_pets_by_type() {
        return petService.getPetTypeDistribution().toString();
    }

    /**
     * 搜索宠物
     */
    @Tool(description = "根据关键词搜索宠物，可匹配宠物名称、品种等信息")
    public String search_pets(
            @ToolParam(description = "搜索关键词") String keyword
    ) {
        List<Pet> pets = petService.searchPets(keyword);
        if (pets == null || pets.isEmpty()) {
            return "未找到匹配的宠物";
        }

        return pets.stream()
                .map(pet -> String.format("宠物ID: %d, 名称: %s (%s - %s)",
                        pet.getId(), pet.getName(), pet.getType(), pet.getBreed()))
                .collect(Collectors.joining(", "));
    }
    /**
     * 搜索用户
     */
    @Tool(description = "根据关键词搜索宠物诊所系统的用户，可匹配用户名或昵称信息")
    public String search_users(
            @ToolParam(description = "搜索关键词（用户名或昵称）") String keyword
    ) {
        List<SysUser> users = sysUserService.searchUsers(keyword);
        if (users == null || users.isEmpty()) {
            return "未找到匹配的用户";
        }

        return users.stream()
                .map(user -> String.format("用户ID: %d, 用户名: %s, 昵称: %s, 角色: %s",
                        user.getId(), user.getUsername(), user.getNickname(),
                        user.getRoleType() == 1 ? "宠物主人" : user.getRoleType() == 2 ? "服务商" : "管理员"))
                .collect(Collectors.joining("\n"));
    }
}