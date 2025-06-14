package com.seproject.demo.controller;

import com.seproject.demo.entity.MaintainScrap;
import com.seproject.demo.repository.MaintainScrapRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MaintainScrapControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MaintainScrapRepository maintainScrapRepository;

    private static int createdRecordId;

    @Test
    @Order(1)
    void testCreateRecord_Success() {
        MaintainScrap record = new MaintainScrap();
        record.setEquipid(101);
        record.setDate(new Date());
        record.setKind(0); // 维修

        ResponseEntity<MaintainScrap> response = restTemplate.postForEntity("/api/maintain-scrap", record, MaintainScrap.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getId() > 0);

        createdRecordId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testGetAllRecords() {
        String url = "/api/maintain-scrap?page=0&size=5";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content")); // 检查分页响应结构
    }

    @Test
    @Order(3)
    void testGetRecordsByEquipment() {
        ResponseEntity<MaintainScrap[]> response = restTemplate.getForEntity("/api/maintain-scrap/by-equipment/101", MaintainScrap[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);
        assertEquals(101, response.getBody()[0].getEquipid());
    }

    @Test
    @Order(4)
    void testGetRecordsByEquipment_NotFound() {
        ResponseEntity<MaintainScrap[]> response = restTemplate.getForEntity("/api/maintain-scrap/by-equipment/999999", MaintainScrap[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);

        maintainScrapRepository.deleteById(101);
    }
}
