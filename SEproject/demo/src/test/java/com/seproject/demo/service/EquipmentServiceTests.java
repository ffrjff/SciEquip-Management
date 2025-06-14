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
import com.seproject.demo.service.EquipmentService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class EquipmentServiceTests {
    @Autowired
    private  EquipmentService equipmentService;
    

    @Test
    void findAll() {
        // 测试查询所有设备
        List<Equipment> result = equipmentService.findAll();
        Assertions.assertEquals(2, result.size(), "设备数量应该为2");
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
        Assertions.assertEquals("test_machine1", equipmentService.findById(3).getEquipname(), "保存的设备名称应该为test_machine1");

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

        // 恢复原状态
        // equipmentService.updateStatus(1, original_status);
    }

    
}
