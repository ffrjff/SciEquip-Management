package com.seproject.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import com.seproject.demo.entity.UserInfo;

@SpringBootTest
@Transactional
public class UserInfoServiceTests {

    @Autowired
    private UserInfoService userInfoService;

    @Test
    void saveUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserid(1001);
        userInfo.setEmployeeid("E12345");
        userInfo.setEmail("test@example.com");
        userInfo.setGender(1);
        userInfo.setAge(30);
        userInfo.setPhone("1234567890");
        userInfo.setAvatar("/images/avatar1.png");

        UserInfo saved = userInfoService.saveUserInfo(userInfo);
        assertNotNull(saved, "保存后的UserInfo不应为null");
        assertEquals(1001, saved.getUserid(), "Userid应一致");
        assertEquals("E12345", saved.getEmployeeid(), "Employeeid应一致");
    }

    @Test
    void findByUserid() {
        // 先保存一个用于查找
        UserInfo userInfo = new UserInfo();
        userInfo.setUserid(2002);
        userInfo.setEmployeeid("E54321");
        userInfo.setEmail("find@test.com");
        userInfo.setGender(0);
        userInfo.setAge(25);
        userInfo.setPhone("0987654321");
        userInfo.setAvatar("/images/avatar2.png");

        userInfoService.saveUserInfo(userInfo);

        Optional<UserInfo> found = userInfoService.findByUserid(2002);
        assertTrue(found.isPresent(), "应该能根据userid查找到UserInfo");
        assertEquals("E54321", found.get().getEmployeeid(), "Employeeid应一致");

        Optional<UserInfo> notFound = userInfoService.findByUserid(9999);
        assertTrue(notFound.isEmpty(), "不存在的userid应返回空");
    }

    @Test
    void createDefaultUserInfo() {
        int testUserid = 3003;
        UserInfo defaultInfo = userInfoService.createDefaultUserInfo(testUserid);

        assertNotNull(defaultInfo, "默认UserInfo不应为null");
        assertEquals(testUserid, defaultInfo.getUserid(), "Userid应设置正确");
        assertEquals("", defaultInfo.getEmployeeid(), "默认Employeeid应为空字符串");
        assertEquals("", defaultInfo.getEmail(), "默认Email应为空字符串");
        assertEquals(2, defaultInfo.getGender(), "默认Gender应为2");
        assertEquals(0, defaultInfo.getAge(), "默认Age应为0");
        assertEquals("", defaultInfo.getPhone(), "默认Phone应为空字符串");
        assertEquals("/images/default-avatar.png", defaultInfo.getAvatar(), "默认头像路径应正确");
    }
}
