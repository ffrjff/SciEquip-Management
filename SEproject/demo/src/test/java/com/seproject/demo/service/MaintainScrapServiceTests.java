package com.seproject.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import jakarta.transaction.Transactional;

import com.seproject.demo.entity.MaintainScrap;

@SpringBootTest
@Transactional
public class MaintainScrapServiceTests {

    @Autowired
    private MaintainScrapService maintainScrapService;

    @Test
    void save() {
        MaintainScrap record = new MaintainScrap();
        record.setEquipid(101);
        record.setKind(0); // 维修

        MaintainScrap saved = maintainScrapService.save(record);

        assertNotNull(saved, "保存后的记录不应为null");
        assertEquals(101, saved.getEquipid(), "Equipid应保持一致");
        assertEquals(0, saved.getKind(), "Kind应保持一致");
        assertNotNull(saved.getDate(), "保存时应设置日期");
    }

    @Test
    void findByEquipid() {
        MaintainScrap record1 = new MaintainScrap();
        record1.setEquipid(202);
        record1.setKind(0);
        maintainScrapService.save(record1);

        MaintainScrap record2 = new MaintainScrap();
        record2.setEquipid(202);
        record2.setKind(1);
        maintainScrapService.save(record2);

        List<MaintainScrap> list = maintainScrapService.findByEquipid(202);
        assertNotNull(list, "查询结果不应为null");
        assertTrue(list.size() >= 2, "应至少查到2条记录");
        assertTrue(list.stream().allMatch(r -> r.getEquipid() == 202), "所有记录的equipid应为202");
    }

    @Test
    void findAll() {
        // 确保有数据
        MaintainScrap record = new MaintainScrap();
        record.setEquipid(303);
        record.setKind(0);
        maintainScrapService.save(record);

        Page<MaintainScrap> page = maintainScrapService.findAll(0, 10);
        assertNotNull(page, "分页查询结果不应为null");
        assertTrue(page.getContent().size() > 0, "分页结果应包含数据");
    }

}
