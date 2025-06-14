package com.seproject.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import com.seproject.demo.entity.User;

@SpringBootTest
@Transactional
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Test
    void findAll() {
        List<User> users = userService.findAll();
        assertNotNull(users, "用户列表不应该为null");
        assertTrue(users.size() > 0, "用户列表不应该为空");
    }

    @Test
    void findById() {
        // 假设数据库里有ID为1的用户
        Optional<User> userOpt = userService.findById(1);
        assertTrue(userOpt.isPresent(), "ID为1的用户应该存在");
        assertEquals(1, userOpt.get().getUserid(), "用户ID应为1");

        Optional<User> nonExist = userService.findById(9999);
        assertTrue(nonExist.isEmpty(), "不存在的用户ID应返回空");
    }

    @Test
    void save() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");
        // 其他属性根据你的User实体自行补充

        User saved = userService.save(user);
        assertNotNull(saved, "保存的用户不应该为null");
        assertTrue(saved.getUserid() > 0, "保存后应有数据库生成的ID");

        Optional<User> fetched = userService.findById(saved.getUserid());
        assertTrue(fetched.isPresent(), "保存后查询应该能找到用户");
        assertEquals("testuser", fetched.get().getUsername(), "用户名应一致");
    }

    @Test
    void deleteById() {
        User user = new User();
        user.setUsername("tobedeleted");
        user.setPassword("pass");
        User saved = userService.save(user);

        userService.deleteById(saved.getUserid());
        Optional<User> deleted = userService.findById(saved.getUserid());
        assertTrue(deleted.isEmpty(), "删除后用户应不存在");
    }

    @Test
    void findByUsername() {

        Optional<User> userOpt = userService.findByUsername("zry");
        assertTrue(userOpt.isPresent(), "用户名'zry'应该存在");

        Optional<User> nonExist = userService.findByUsername("nonexistuser");
        assertTrue(nonExist.isEmpty(), "不存在的用户名应返回空");
    }

    @Test
    void existsByUsername() {
        // 假设"user1"存在
        assertTrue(userService.existsByUsername("zry"), "zry 应该存在");

        assertFalse(userService.existsByUsername("notexists"), "notexists 应该不存在");
    }

    @Test
    void changePassword() {
        // 先保存一个用户用于测试
        User user = new User();
        user.setUsername("changepwuser");
        user.setPassword("oldpass");
        User saved = userService.save(user);

        // 正确旧密码
        User updated = userService.changePassword(saved.getUserid(), "oldpass", "newpass");
        assertNotNull(updated, "密码修改成功应返回更新后的用户");
        assertEquals("newpass", updated.getPassword(), "密码应该被更新");

        // 错误旧密码
        User failedUpdate = userService.changePassword(saved.getUserid(), "wrongold", "whatever");
        assertNull(failedUpdate, "错误旧密码应返回null");
    }
}
