package com.seproject.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import com.seproject.demo.entity.BorrowReturn;
import com.seproject.demo.entity.BorrowReturnStatus;

@SpringBootTest
@Transactional
public class BorrowReturnServiceTests {

    @Autowired
    private BorrowReturnService borrowReturnService;

    @Test
    void save() {
        BorrowReturn br = new BorrowReturn();
        br.setUserid(101);
        br.setEquipid(202);
        br.setEquipstatus(BorrowReturnStatus.BORROWED);
        BorrowReturn saved = borrowReturnService.save(br);

        assertNotNull(saved, "保存的BorrowReturn不应为null");
        assertEquals(101, saved.getUserid(), "Userid应一致");
        assertEquals(202, saved.getEquipid(), "Equipid应一致");
        assertEquals(BorrowReturnStatus.BORROWED, saved.getEquipstatus(), "Equipstatus应一致");
    }

    @Test
    void findById() {
        BorrowReturn br = new BorrowReturn();
        br.setUserid(103);
        br.setEquipid(204);
        br.setEquipstatus(BorrowReturnStatus.RETURNED);
        BorrowReturn saved = borrowReturnService.save(br);

        BorrowReturn found = borrowReturnService.findById(saved.getId());
        assertNotNull(found, "应找到对应的BorrowReturn");
        assertEquals(saved.getId(), found.getId(), "ID应一致");

        BorrowReturn notFound = borrowReturnService.findById(99999);
        assertNull(notFound, "不存在的ID应返回null");
    }

    @Test
    void findByUserid() {
        BorrowReturn br1 = new BorrowReturn();
        br1.setUserid(110);
        br1.setEquipid(210);
        br1.setEquipstatus(BorrowReturnStatus.BORROWED);
        borrowReturnService.save(br1);

        BorrowReturn br2 = new BorrowReturn();
        br2.setUserid(110);
        br2.setEquipid(211);
        br2.setEquipstatus(BorrowReturnStatus.RETURNED);
        borrowReturnService.save(br2);

        List<BorrowReturn> list = borrowReturnService.findByUserid(110);
        assertNotNull(list, "查询结果不应为null");
        assertTrue(list.size() >= 2, "应查询到至少两个记录");
    }

    @Test
    void findByUseridAndEquipstatus() {
        BorrowReturn br1 = new BorrowReturn();
        br1.setUserid(120);
        br1.setEquipid(220);
        br1.setEquipstatus(BorrowReturnStatus.BORROWED);
        borrowReturnService.save(br1);

        BorrowReturn br2 = new BorrowReturn();
        br2.setUserid(120);
        br2.setEquipid(221);
        br2.setEquipstatus(BorrowReturnStatus.RETURNED);
        borrowReturnService.save(br2);

        List<BorrowReturn> borrowedList = borrowReturnService.findByUseridAndEquipstatus(120, BorrowReturnStatus.BORROWED);
        assertNotNull(borrowedList, "查询结果不应为null");
        assertTrue(borrowedList.stream().allMatch(br -> br.getEquipstatus() == BorrowReturnStatus.BORROWED), "所有结果状态应为BORROWED");
    }

    @Test
    void findByEquipid() {
        BorrowReturn br1 = new BorrowReturn();
        br1.setUserid(130);
        br1.setEquipid(230);
        br1.setEquipstatus(BorrowReturnStatus.BORROWED);
        borrowReturnService.save(br1);

        List<BorrowReturn> list = borrowReturnService.findByEquipid(230);
        assertNotNull(list, "查询结果不应为null");
        assertTrue(list.stream().allMatch(br -> br.getEquipid() == 230), "所有结果的Equipid应为230");
    }

    @Test
    void findByEquipidInAndEquipstatus() {
        BorrowReturn br1 = new BorrowReturn();
        br1.setUserid(140);
        br1.setEquipid(240);
        br1.setEquipstatus(BorrowReturnStatus.BORROWED);
        borrowReturnService.save(br1);

        BorrowReturn br2 = new BorrowReturn();
        br2.setUserid(141);
        br2.setEquipid(241);
        br2.setEquipstatus(BorrowReturnStatus.BORROWED);
        borrowReturnService.save(br2);

        BorrowReturn br3 = new BorrowReturn();
        br3.setUserid(142);
        br3.setEquipid(242);
        br3.setEquipstatus(BorrowReturnStatus.RETURNED);
        borrowReturnService.save(br3);

        List<Integer> equipIds = Arrays.asList(240, 241, 999);
        List<BorrowReturn> list = borrowReturnService.findByEquipidInAndEquipstatus(equipIds, BorrowReturnStatus.BORROWED);

        assertNotNull(list, "查询结果不应为null");
        assertTrue(list.size() >= 2, "应查询到至少两个BORROWED状态的记录");
        assertTrue(list.stream().allMatch(br -> equipIds.contains(br.getEquipid())), "所有结果的Equipid应在给定列表中");
        assertTrue(list.stream().allMatch(br -> br.getEquipstatus() == BorrowReturnStatus.BORROWED), "所有结果状态应为BORROWED");
    }
}
