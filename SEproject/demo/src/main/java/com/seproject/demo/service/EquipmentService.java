package com.seproject.demo.service;

import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import com.seproject.demo.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    // 查询所有设备
    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    // 根据ID查询设备
    public Equipment findById(int equipId) {
        return equipmentRepository.findById(equipId).orElse(null);
    }

    // 新增设备
    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    // 删除设备
    public void deleteById(int equipId) {
        equipmentRepository.deleteById(equipId);
    }

    public List<Equipment> findEquipment(String name, String type, EquipmentStatus status, String company) {
        return equipmentRepository.findEquipmentByCriteria(name, type, status, company);
    }

    public Map<String, Long> getEquipmentStats() {
        Map<String, Long> stats = new HashMap<>();

        // 总设备数
        stats.put("total", equipmentRepository.count());

        // 在用设备数
        stats.put("inUse", equipmentRepository.countByEquipstatus(EquipmentStatus.IN_USE));

        // 待归还设备数 (假设待归还设备状态也是IN_USE)
        // 如果需要更精确，可以查询借还记录表中状态为"待归还"的记录数
        stats.put("toBeReturned", equipmentRepository.countByEquipstatus(EquipmentStatus.IN_USE));

        // 待审核申请数 (根据你的枚举，PENDING状态)
        stats.put("pendingApproval", equipmentRepository.countByEquipstatus(EquipmentStatus.PENDING));

        return stats;
    }


    public List<Equipment> findByCompany(String company) {
        return equipmentRepository.findByCompany(company);
    }

    public Equipment updateStatus(int equipId, EquipmentStatus status) {
        Equipment equipment = equipmentRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        equipment.setEquipstatus(status);
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(int equipId, Equipment equipmentDetails) {
        Equipment equipment = equipmentRepository.findById(equipId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));

        // 更新可修改的字段
        if (equipmentDetails.getEquipname() != null) {
            equipment.setEquipname(equipmentDetails.getEquipname());
        }
        if (equipmentDetails.getEquipkind() != null) {
            equipment.setEquipkind(equipmentDetails.getEquipkind());
        }
        if (equipmentDetails.getEquipmodel() != null) {
            equipment.setEquipmodel(equipmentDetails.getEquipmodel());
        }
        if (equipmentDetails.getEquipnum() != null) {
            equipment.setEquipnum(equipmentDetails.getEquipnum());
        }
        if (equipmentDetails.getEquipdate() != null) {
            equipment.setEquipdate(equipmentDetails.getEquipdate());
        }

        return equipmentRepository.save(equipment);
    }
}