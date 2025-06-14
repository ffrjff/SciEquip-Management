package com.seproject.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.seproject.demo.entity.BorrowReturn;
import com.seproject.demo.entity.BorrowReturnStatus;
import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import com.seproject.demo.entity.EquipmentUsageStat;
import com.seproject.demo.entity.MaintainScrap;
import com.seproject.demo.service.EquipmentService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class EquipmentServiceTests {
    @Autowired
    private  EquipmentService equipmentService;
    @Autowired
    private BorrowReturnService borrowReturnService;
    @Autowired
    private MaintainScrapService maintainScrapService;
    

    @Test
    void findAll() {
        // 测试查询所有设备
        List<Equipment> result = equipmentService.findAll();
        Assertions.assertEquals(3, result.size(), "设备数量应该为2");
    }

    @Test
    void findById() {
        // 测试根据ID查询设备
        Equipment equipment = equipmentService.findById(1);
        Assertions.assertNotNull(equipment, "设备不应该为null");
        Assertions.assertEquals("test_machine", equipment.getEquipname(), "设备名称应该为test_machine");

        Equipment nonExistentEquipment = equipmentService.findById(999);
        Assertions.assertNull(nonExistentEquipment, "不存在的设备应该返回null");
    }

    @Test
    void save() {
        Equipment equipment = new Equipment();
        equipment.setEquipnum("test_num");
        equipment.setEquipname("test_machine1");
        equipment.setEquipkind("test_type");
        equipment.setEquipmodel("test_model");
        equipment.setEquipdate(new java.util.Date());
        equipment.setCompany("test_company");
        equipment.setEquipstatus(EquipmentStatus.AVAILABLE);
        Equipment savedEquipment = equipmentService.save(equipment);
        Assertions.assertNotNull(savedEquipment, "保存的设备不应该为null");
        Assertions.assertEquals("test_machine1", equipmentService.findById(savedEquipment.getEquipid()).getEquipname(), "保存的设备名称应该为test_machine1");

        // equipmentService.deleteById(3); // 清理测试数据
    }

    @Test
    void deleteById() {
        Equipment equipment = new Equipment();
        equipment.setEquipnum("test_num");
        equipment.setEquipname("test_machine1");
        equipment.setEquipkind("test_type");
        equipment.setEquipmodel("test_model");
        equipment.setEquipdate(new java.util.Date());
        equipment.setCompany("test_company");
        equipment.setEquipstatus(EquipmentStatus.AVAILABLE);
        Equipment savedEquipment = equipmentService.save(equipment);
        
        equipmentService.deleteById(savedEquipment.getEquipid());
        Assertions.assertNull(equipmentService.findById(savedEquipment.getEquipid()), "删除后的设备应该为null");
    }

    @Test
    void findEquipment() {

        List<Equipment> result = equipmentService.findEquipment("test_machine", "1", EquipmentStatus.AVAILABLE, "1");
        Assertions.assertNotNull(result, "查询结果不应该为null");
    }

    @Test
    void getEquipmentStats() {
        Map<String, Long> stats = equipmentService.getEquipmentStats();
        Assertions.assertNotNull(stats, "统计结果不应该为null");
    }

    @Test
    void findByCompany() {
        List<Equipment> result = equipmentService.findByCompany("1");
        Assertions.assertNotNull(result, "根据公司查询结果不应该为null");
        Assertions.assertTrue(result.size() > 0, "根据公司查询结果应该有设备");
    }

    @Test
    void updateStatus() {
        Equipment equipment = equipmentService.findById(1);
        Assertions.assertNotNull(equipment, "设备不应该为null");
        EquipmentStatus original_status = equipment.getEquipstatus();
        EquipmentStatus status = EquipmentStatus.SCRAPPED;

        Equipment updatedEquipment = equipmentService.updateStatus(1, status);
        Assertions.assertEquals(status, updatedEquipment.getEquipstatus(), "设备状态应该被更新为" + status);

    }

    @Test
    void updateEquipment() {
        int equipId = 1;

        // 先查设备，确认存在
        Equipment original = equipmentService.findById(equipId);
        Assertions.assertNotNull(original, "设备不应该为null");

        // 1. 测试全部字段都为非null，全部更新
        Equipment detailsAllNotNull = new Equipment();
        detailsAllNotNull.setEquipname("新设备名");
        detailsAllNotNull.setEquipkind("新设备类型");
        detailsAllNotNull.setEquipmodel("新型号");
        detailsAllNotNull.setEquipnum("123456");
        detailsAllNotNull.setEquipdate(java.sql.Date.valueOf("2025-06-14"));

        Equipment updatedAll = equipmentService.updateEquipment(equipId, detailsAllNotNull);
        Assertions.assertEquals("新设备名", updatedAll.getEquipname());
        Assertions.assertEquals("新设备类型", updatedAll.getEquipkind());
        Assertions.assertEquals("新型号", updatedAll.getEquipmodel());
        Assertions.assertEquals("123456", updatedAll.getEquipnum());
        Assertions.assertEquals(java.sql.Date.valueOf("2025-06-14"), updatedAll.getEquipdate());

        // 2. 测试部分字段为null，不更新对应字段，保持原值
        Equipment detailsPartialNull = new Equipment();
        detailsPartialNull.setEquipname(null);
        detailsPartialNull.setEquipkind("部分更新类型");
        detailsPartialNull.setEquipmodel(null);
        detailsPartialNull.setEquipnum("部分更新编号");
        detailsPartialNull.setEquipdate(null);

        Equipment updatedPartial = equipmentService.updateEquipment(equipId, detailsPartialNull);
        // equipname、equipmodel、equipdate 应该保持上一次更新的值
        Assertions.assertEquals("新设备名", updatedPartial.getEquipname());
        Assertions.assertEquals("部分更新类型", updatedPartial.getEquipkind());
        Assertions.assertEquals("新型号", updatedPartial.getEquipmodel());
        Assertions.assertEquals("部分更新编号", updatedPartial.getEquipnum());
        Assertions.assertEquals(java.sql.Date.valueOf("2025-06-14"), updatedPartial.getEquipdate());

        // 3. 测试设备不存在时，抛出异常
        int nonExistId = 999999;
        Equipment details = new Equipment();
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> {
            equipmentService.updateEquipment(nonExistId, details);
        });
        Assertions.assertTrue(ex.getMessage().contains("设备不存在"));
    }

    // @Test
    // void getUsageStatistics_integratedTest() {
    //     LocalDate startDate = LocalDate.of(2025, 6, 1);

    //     // 清理测试环境（如果需要）
    //     // equipmentService.deleteAll();
    //     // borrowReturnService.deleteAll();
    //     // maintainScrapService.deleteAll();

    //     // 1. 新建设备1，类型A
    //     Equipment equip1 = new Equipment();
    //     equip1.setEquipname("设备1");
    //     equip1.setEquipkind("类型A");
    //     equip1.setEquipstatus(EquipmentStatus.AVAILABLE);
    //     equip1 = equipmentService.save(equip1);

    //     // 2. 新建设备2，类型B
    //     Equipment equip2 = new Equipment();
    //     equip2.setEquipname("设备2");
    //     equip2.setEquipkind("类型B");
    //     equip2.setEquipstatus(EquipmentStatus.IN_USE);
    //     equip2 = equipmentService.save(equip2);

    //     // 3. 新建借用归还记录：符合条件的
    //     BorrowReturn br1 = new BorrowReturn();
    //     br1.setEquipid(equip1.getEquipid());
    //     br1.setBorrowdate(java.sql.Date.valueOf("2025-06-05"));
    //     br1.setReturndate(java.sql.Date.valueOf("2025-06-06"));
    //     br1.setEquipstatus(BorrowReturnStatus.BORROWED);
    //     borrowReturnService.save(br1);

    //     // 4. 新建借用归还记录：借用日期早于startDate，不应计入
    //     BorrowReturn br2 = new BorrowReturn();
    //     br2.setEquipid(equip1.getEquipid());
    //     br2.setBorrowdate(java.sql.Date.valueOf("2025-05-20"));
    //     br2.setReturndate(java.sql.Date.valueOf("2025-06-01"));
    //     br2.setEquipstatus(BorrowReturnStatus.RETURNED);
    //     borrowReturnService.save(br2);

    //     // 5. 新建维护报废记录
    //     MaintainScrap ms1 = new MaintainScrap();
    //     ms1.setEquipid(equip1.getEquipid());
    //     maintainScrapService.save(ms1);

    //     // 6. 调用接口，不传设备类型，应该包含所有设备
    //     List<EquipmentUsageStat> statsAll = equipmentService.getUsageStatistics(startDate, null);
    //     Assertions.assertTrue(statsAll.stream().anyMatch(s -> s.getName().equals("设备1")));
    //     Assertions.assertTrue(statsAll.stream().anyMatch(s -> s.getName().equals("设备2")));

    //     // 7. 调用接口，设备类型为 "类型A"，只返回设备1
    //     List<EquipmentUsageStat> statsTypeA = equipmentService.getUsageStatistics(startDate, "类型A");
    //     Assertions.assertEquals(1, statsTypeA.size());
    //     Assertions.assertEquals("设备1", statsTypeA.get(0).getName());

    //     // 8. 校验设备1的统计数据
    //     EquipmentUsageStat stat1 = statsTypeA.get(0);
    //     Assertions.assertEquals("类型A", stat1.getType());
    //     Assertions.assertEquals(1, stat1.getUsageCount(), "应该只统计符合startDate后的记录");
    //     Assertions.assertTrue(stat1.getTotalDurationHours() > 0);
    //     Assertions.assertEquals(1, stat1.getFailureCount(), "维护报废次数应为1");

    //     // 9. 调用接口，设备类型为不存在的类型，应为空
    //     List<EquipmentUsageStat> statsNone = equipmentService.getUsageStatistics(startDate, "不存在类型");
    //     Assertions.assertTrue(statsNone.isEmpty());
    // }


    
}
