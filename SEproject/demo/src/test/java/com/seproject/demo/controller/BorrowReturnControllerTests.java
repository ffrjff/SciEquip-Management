package com.seproject.demo.controller;

import com.seproject.demo.entity.BorrowReturn;
import com.seproject.demo.entity.BorrowReturnStatus;
import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import com.seproject.demo.repository.BorrowReturnRepository;
import com.seproject.demo.repository.EquipmentRepository;
import com.seproject.demo.service.EquipmentService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BorrowReturnControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private BorrowReturnRepository borrowReturnRepository;

    // private static int equipmentId;
    // private static int borrowRecordId;

    @Test
    @Order(1)
    @Rollback(true)
    void testBorrowEquipment_Success() {
        Equipment equipment = new Equipment();
        equipment.setEquipname("BorrowTestEquip");
        equipment.setEquipstatus(EquipmentStatus.AVAILABLE);
        equipment = equipmentRepository.save(equipment);
        int equipmentId = equipment.getEquipid();

        String url = "/api/borrow-return/borrow?userId=100&equipId=" + equipmentId;
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BorrowReturn br = response.getBody();
        assertNotNull(br);
        assertEquals(100, br.getUserid());
        assertEquals(equipmentId, br.getEquipid());
        assertEquals(BorrowReturnStatus.PENDING, br.getEquipstatus());

        // 验证设备状态变为 PENDING
        Equipment eq = equipmentRepository.findById(equipmentId).orElse(null);
        assertNotNull(eq);
        assertEquals(EquipmentStatus.PENDING, eq.getEquipstatus());

        equipmentService.deleteById(equipmentId); // 清理测试数据
        borrowReturnRepository.deleteById(br.getId());
    }


    @Test
    @Order(2)
    @Rollback(true)
    void testBorrowEquipment_EquipmentNotFound() {
        String url = "/api/borrow-return/borrow?userId=100&equipId=999999";
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(3)
    @Rollback(true)
    void testBorrowEquipment_EquipmentNotAvailable() {
        // 设备状态为 IN_USE，不可借用
        Equipment equipment = new Equipment();
        equipment.setEquipname("BusyEquip");
        equipment.setEquipstatus(EquipmentStatus.IN_USE);
        equipment = equipmentRepository.save(equipment);
        int busyEquipId = equipment.getEquipid();

        String url = "/api/borrow-return/borrow?userId=101&equipId=" + busyEquipId;
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        equipmentService.deleteById(busyEquipId); // 清理测试数据
        // borrowReturnRepository.deleteById();
    }

    @Test
    @Order(4)
    @Rollback(true)
    void testReturnEquipment_Success() {
        // 先准备设备和借用记录
        Equipment equipment = new Equipment();
        equipment.setEquipname("ReturnTestEquip");
        equipment.setEquipstatus(EquipmentStatus.PENDING); // 借出状态
        equipment = equipmentRepository.save(equipment);
        int equipmentId = equipment.getEquipid();

        BorrowReturn borrowReturn = new BorrowReturn();
        borrowReturn.setUserid(100);
        borrowReturn.setEquipid(equipmentId);
        borrowReturn.setBorrowdate(new java.util.Date());
        borrowReturn.setEquipstatus(BorrowReturnStatus.PENDING);
        borrowReturn = borrowReturnRepository.save(borrowReturn);
        int borrowRecordId = borrowReturn.getId();

        // 调用归还接口
        String url = "/api/borrow-return/return?recordId=" + borrowRecordId;
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BorrowReturn br = response.getBody();
        assertNotNull(br);
        assertEquals(BorrowReturnStatus.RETURNED, br.getEquipstatus());
        assertNotNull(br.getReturndate());

        // 验证设备状态恢复为 AVAILABLE
        Equipment eq = equipmentRepository.findById(equipmentId).orElse(null);
        assertNotNull(eq);
        assertEquals(EquipmentStatus.AVAILABLE, eq.getEquipstatus());

        equipmentService.deleteById(equipmentId); // 清理测试数据
        borrowReturnRepository.deleteById(br.getId());
    }

    @Test
    @Order(5)
    @Rollback(true)
    void testReturnEquipment_RecordNotFound() {
        String url = "/api/borrow-return/return?recordId=999999";
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(6)
    @Rollback(true)
    void testExtendBorrow_Success() {
        // 先借用一台设备（状态应为 AVAILABLE）
        Equipment equipment = new Equipment();
        equipment.setEquipname("ExtendEquip");
        equipment.setEquipstatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);
        int extEquipId = equipment.getEquipid();

        // 借用这台设备
        String borrowUrl = "/api/borrow-return/borrow?userId=200&equipId=" + extEquipId;
        ResponseEntity<BorrowReturn> borrowResp = restTemplate.postForEntity(borrowUrl, null, BorrowReturn.class);
        assertEquals(HttpStatus.OK, borrowResp.getStatusCode());
        int origRecordId = borrowResp.getBody().getId();

        // 延期借用
        String extendUrl = "/api/borrow-return/extend?recordId=" + origRecordId + "&userId=200";
        ResponseEntity<BorrowReturn> extendResp = restTemplate.postForEntity(extendUrl, null, BorrowReturn.class);

        assertEquals(HttpStatus.OK, extendResp.getStatusCode());
        BorrowReturn newRecord = extendResp.getBody();
        assertNotNull(newRecord);
        assertEquals(200, newRecord.getUserid());
        assertEquals(extEquipId, newRecord.getEquipid());

        // 旧记录状态应为 RETURNED
        BorrowReturn oldRecord = borrowReturnRepository.findById(origRecordId).orElse(null);
        assertNotNull(oldRecord);
        assertEquals(BorrowReturnStatus.RETURNED, oldRecord.getEquipstatus());

        equipmentService.deleteById(extEquipId); // 清理测试数据
        borrowReturnRepository.deleteById(newRecord.getId());
        borrowReturnRepository.deleteById(origRecordId); // 清理旧记录
    }

    @Test
    @Order(7)
    @Rollback(true)
    void testExtendBorrow_RecordNotFound() {
        String extendUrl = "/api/borrow-return/extend?recordId=999999&userId=1";
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(extendUrl, null, BorrowReturn.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(8)
    @Rollback(true)
    void testGetToReturnRecords() {
        String url = "/api/borrow-return/to-return?userId=1";
        ResponseEntity<BorrowReturn[]> response = restTemplate.getForEntity(url, BorrowReturn[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(9)
    @Rollback(true)
    void testGetUserRecords() {
        String url = "/api/borrow-return/user-records?userId=1";
        ResponseEntity<BorrowReturn[]> response = restTemplate.getForEntity(url, BorrowReturn[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(10)
    @Rollback(true)
    void testGetEquipmentRecords() {
        int equipmentId = 1; // 假设设备ID为1
        String url = "/api/borrow-return/equipment-records?equipId=" + equipmentId;
        ResponseEntity<BorrowReturn[]> response = restTemplate.getForEntity(url, BorrowReturn[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(11)
    @Rollback(true)
    void testGetPendingRequests() {
        // 先创建一台设备及借用记录
        Equipment eq = new Equipment();
        eq.setEquipname("CompanyEquip");
        eq.setEquipstatus(EquipmentStatus.AVAILABLE);
        eq.setCompany("TestCompany");
        equipmentRepository.save(eq);
        int cEquipId = eq.getEquipid();

        BorrowReturn br = new BorrowReturn();
        br.setUserid(300);
        br.setEquipid(cEquipId);
        br.setBorrowdate(new java.util.Date());
        br.setEquipstatus(BorrowReturnStatus.PENDING);
        borrowReturnRepository.save(br);

        String url = "/api/borrow-return/pending-requests?company=TestCompany";
        ResponseEntity<BorrowReturn[]> response = restTemplate.getForEntity(url, BorrowReturn[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);

        // 清理测试数据
        equipmentRepository.deleteById(cEquipId);
        borrowReturnRepository.deleteById(br.getId());
    }

    @Test
    @Order(12)
    @Rollback(true)
    void testApproveBorrow_Success() {
        Equipment eq = new Equipment();
        eq.setEquipname("ApproveEquip");
        eq.setEquipstatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(eq);
        int approveEquipId = eq.getEquipid();

        BorrowReturn br = new BorrowReturn();
        br.setUserid(400);
        br.setEquipid(approveEquipId);
        br.setBorrowdate(new java.util.Date());
        br.setEquipstatus(BorrowReturnStatus.PENDING);
        borrowReturnRepository.save(br);
        int recordId = br.getId();

        String url = "/api/borrow-return/approve/" + recordId;
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BorrowReturn updated = response.getBody();
        assertNotNull(updated);
        assertEquals(BorrowReturnStatus.BORROWED, updated.getEquipstatus());

        Equipment eqUpdated = equipmentRepository.findById(approveEquipId).orElse(null);
        assertNotNull(eqUpdated);
        assertEquals(EquipmentStatus.IN_USE, eqUpdated.getEquipstatus());

        // 清理测试数据
        equipmentService.deleteById(approveEquipId);
        borrowReturnRepository.deleteById(updated.getId());
    }

    @Test
    @Order(13)
    @Rollback(true)
    void testApproveBorrow_RecordNotFound() {
        String url = "/api/borrow-return/approve/999999";
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(14)
    @Rollback(true)
    void testRejectBorrow_Success() {
        Equipment eq = new Equipment();
        eq.setEquipname("RejectEquip");
        eq.setEquipstatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(eq);
        int rejectEquipId = eq.getEquipid();

        BorrowReturn br = new BorrowReturn();
        br.setUserid(500);
        br.setEquipid(rejectEquipId);
        br.setBorrowdate(new java.util.Date());
        br.setEquipstatus(BorrowReturnStatus.PENDING);
        borrowReturnRepository.save(br);
        int recordId = br.getId();

        String url = "/api/borrow-return/reject/" + recordId;
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BorrowReturn updated = response.getBody();
        assertNotNull(updated);
        assertEquals(BorrowReturnStatus.REFUSED, updated.getEquipstatus());

        Equipment eqUpdated = equipmentRepository.findById(rejectEquipId).orElse(null);
        assertNotNull(eqUpdated);
        assertEquals(EquipmentStatus.AVAILABLE, eqUpdated.getEquipstatus());

        // 清理测试数据
        equipmentService.deleteById(rejectEquipId);
        borrowReturnRepository.deleteById(updated.getId());
    }

    @Test
    @Order(15)
    void testRejectBorrow_RecordNotFound() {
        String url = "/api/borrow-return/reject/999999";
        ResponseEntity<BorrowReturn> response = restTemplate.postForEntity(url, null, BorrowReturn.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
