package com.seproject.demo.controller;

import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import com.seproject.demo.entity.EquipmentUsageStat;
import com.seproject.demo.entity.User;
import com.seproject.demo.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    // 获取所有设备
    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentService.findAll();
    }

    // 根据ID查询设备
    @GetMapping("/{equipId}")
    public Equipment getEquipmentById(@PathVariable int equipId) {
        return equipmentService.findById(equipId);
    }

    // 新增设备
//    @PostMapping
//    public Equipment addEquipment(@RequestBody Equipment equipment) {
//        return equipmentService.save(equipment);
//    }

    // 删除设备
    @DeleteMapping("/{equipId}")
    public void deleteEquipment(@PathVariable int equipId) {
        equipmentService.deleteById(equipId);
    }

    // 更新设备状态
    @PutMapping("/{equipId}/status")
    public Equipment updateEquipmentStatus(
            @PathVariable int equipId,
            @RequestParam EquipmentStatus status) {
        return equipmentService.updateStatus(equipId, status);
    }

    @PostMapping
    public ResponseEntity<String> createEquipment(@RequestBody Equipment equipment) {

        if (equipment.getEquipname() == null || equipment.getEquipname().isEmpty() ||
                equipment.getEquipkind() == null || equipment.getEquipkind().isEmpty() ||
                equipment.getEquipmodel() == null || equipment.getEquipmodel().isEmpty() ||
                equipment.getEquipnum() == null || equipment.getEquipnum().isEmpty()
        ) {
            return ResponseEntity.badRequest().body("不能为空");
        }

        equipmentService.save(equipment);
        return ResponseEntity.ok("成功添加！");
    }

    @GetMapping("/search")
    public List<Equipment> searchEquipment(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) EquipmentStatus status,
            @RequestParam(required = false) String company) {
        return equipmentService.findEquipment(name, type, status, company);
    }

    @GetMapping("/stats") // 获得设备状态
    public Map<String, Long> getEquipmentStats() {
        return equipmentService.getEquipmentStats();
    }

    @GetMapping("/user-equipment") // 通过用户id找到设备
    public List<Equipment> getUserEquipment(@RequestParam String userId) {
        return equipmentService.findByCompany(userId);
    }

    @PutMapping("/{equipId}")
    public ResponseEntity<Equipment> updateEquipment(
            @PathVariable int equipId,
            @RequestBody Equipment equipment) {
        Equipment updatedEquipment = equipmentService.updateEquipment(equipId, equipment);
        return ResponseEntity.ok(updatedEquipment);
    }

    @GetMapping("/usage-stats")
    public List<EquipmentUsageStat> getUsageStats(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "deviceType", required = false) String deviceType
    ) {
        
        return equipmentService.getUsageStatistics(startDate, deviceType);
    }



}