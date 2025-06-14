package com.seproject.demo.service;

import com.seproject.demo.entity.*;
import com.seproject.demo.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.seproject.demo.entity.MaintainScrap;
import com.seproject.demo.repository.BorrowReturnRepository;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final BorrowReturnRepository borrowReturnRepository;
    private final MaintainScrapService maintainScrapService;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository,
                            BorrowReturnRepository borrowReturnRepository,
                            MaintainScrapService maintainScrapService) {
        this.equipmentRepository = equipmentRepository;
        this.borrowReturnRepository = borrowReturnRepository;
        this.maintainScrapService = maintainScrapService;
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

    public List<EquipmentUsageStat> getUsageStatistics(LocalDate startDate, String deviceType) {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        List<EquipmentUsageStat> stats = new ArrayList<>();

        for (Equipment equip : allEquipment) {

            // 如果传入了设备类型参数，则进行过滤
            if (deviceType != null && !deviceType.isEmpty() && !deviceType.equals(equip.getEquipkind())) {
                continue;
            }

            List<BorrowReturn> records = borrowReturnRepository.findByEquipid(equip.getEquipid());
            List<MaintainScrap> maintainScraps = maintainScrapService.findByEquipid(equip.getEquipid());

            int usageCount = 0;
            double totalHours = 0;
            int failureCount = 0;

            for (BorrowReturn record : records) {
                // 如果 borrowDate 是 null，则跳过该条记录
                if (record.getBorrowdate() == null) {
                    continue;
                }

                // 如果 borrowDate 在 startDate 之前，则跳过
                if (record.getBorrowdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(startDate)) {
                    continue;
                }

                if (record.getEquipstatus() == BorrowReturnStatus.BORROWED ||
                        record.getEquipstatus() == BorrowReturnStatus.RETURNED) {
                    usageCount++;
                    if (record.getReturndate() != null) {
                        long durationMs = record.getReturndate().getTime() - record.getBorrowdate().getTime();
                        totalHours += durationMs / (1000.0 * 60 * 60);
                    } else {
                        long durationMs = System.currentTimeMillis() - record.getBorrowdate().getTime();
                        totalHours += durationMs / (1000.0 * 60 * 60);
                    }
                }

            }

            failureCount += maintainScraps.size();

            EquipmentUsageStat stat = new EquipmentUsageStat();
            stat.setName(equip.getEquipname());
            stat.setType(equip.getEquipkind());
            stat.setUsageCount(usageCount);
            stat.setTotalDurationHours(Math.round(totalHours * 10.0) / 10.0);
            stat.setAvgDurationHours(usageCount == 0 ? 0 : Math.round((totalHours / usageCount) * 10.0) / 10.0);
            stat.setFailureCount(failureCount);
            stat.setStatus(equip.getEquipstatus().toString());

            stats.add(stat);
        }

        return stats;
    }
}