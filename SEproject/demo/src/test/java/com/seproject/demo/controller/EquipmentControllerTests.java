package com.seproject.demo.controller;

import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import com.seproject.demo.repository.EquipmentRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EquipmentControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EquipmentRepository equipmentRepository;

    private static int createdEquipId;

    @Test
    @Order(1)
    void testCreateEquipment_Success() {
        Equipment equipment = new Equipment();
        equipment.setEquipname("Test Equipment");
        equipment.setEquipkind("Analyzer");
        equipment.setEquipmodel("Model-X");
        equipment.setEquipnum("EQ123456");
        equipment.setEquipstatus(EquipmentStatus.AVAILABLE);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/equipment", equipment, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("成功添加！", response.getBody());

        createdEquipId = equipmentRepository.findAll()
                .stream()
                .filter(e -> "Test Equipment".equals(e.getEquipname()))
                .findFirst()
                .map(Equipment::getEquipid)
                .orElse(0);
        assertTrue(createdEquipId > 0);
    }

    @Test
    @Order(2)
    void testCreateEquipment_MissingFields() {
        Equipment equipment = new Equipment(); // empty fields
        ResponseEntity<String> response = restTemplate.postForEntity("/api/equipment", equipment, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("不能为空", response.getBody());
    }

    @Test
    @Order(3)
    void testGetAllEquipment() {
        ResponseEntity<Equipment[]> response = restTemplate.getForEntity("/api/equipment", Equipment[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(4)
    void testGetEquipmentById_Found() {
        ResponseEntity<Equipment> response = restTemplate.getForEntity("/api/equipment/" + createdEquipId, Equipment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Equipment", response.getBody().getEquipname());
    }

    @Test
    @Order(5)
    void testGetEquipmentById_NotFound() {
        ResponseEntity<Equipment> response = restTemplate.getForEntity("/api/equipment/999", Equipment.class);
        assertEquals(null, response.getBody()); // 根据你实现的异常机制调整
    }

    @Test
    @Order(6)
    void testUpdateEquipmentStatus() {
        String url = "/api/equipment/" + createdEquipId + "/status?status=IN_USE";
        ResponseEntity<Equipment> response = restTemplate.exchange(url, HttpMethod.PUT, null, Equipment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EquipmentStatus.IN_USE, response.getBody().getEquipstatus());
    }

    @Test
    @Order(7)
    void testSearchEquipment() {
        String url = "/api/equipment/search?name=Test Equipment&type=Analyzer&status=IN_USE";
        ResponseEntity<Equipment[]> response = restTemplate.getForEntity(url, Equipment[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    @Order(8)
    void testGetEquipmentStats() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/equipment/stats", Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("total"));
    }

    @Test
    @Order(9)
    void testGetUserEquipment() {
        String userId = "SomeCompany"; // 假设设备关联字段为 company，且服务端能识别
        ResponseEntity<Equipment[]> response = restTemplate.getForEntity("/api/equipment/user-equipment?userId=" + userId, Equipment[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(10)
    void testUpdateEquipment() {
        Equipment updated = new Equipment();
        updated.setEquipname("Updated Name");
        updated.setEquipkind("Updated Kind");
        updated.setEquipmodel("Updated Model");
        updated.setEquipnum("UpdatedNum");
        updated.setEquipstatus(EquipmentStatus.AVAILABLE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Equipment> request = new HttpEntity<>(updated, headers);

        ResponseEntity<Equipment> response = restTemplate.exchange("/api/equipment/" + createdEquipId, HttpMethod.PUT, request, Equipment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getEquipname());
    }

    @Test
    @Order(11)
    void testGetUsageStats() {
        String url = "/api/equipment/usage-stats?startDate=2024-01-01";
        ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(12)
    void testDeleteEquipment() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/equipment/" + createdEquipId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        equipmentRepository.deleteById(createdEquipId);
    }
}
