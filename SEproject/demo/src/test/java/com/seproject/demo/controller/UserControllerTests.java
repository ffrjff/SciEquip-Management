package com.seproject.demo.controller;

import com.seproject.demo.entity.User;
import com.seproject.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private static int createdUserId;

    @Test
    @Order(1)
    void testCreateUser_Success() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("123456");

        ResponseEntity<Map> response = restTemplate.postForEntity("/user", user, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("注册成功！", response.getBody().get("message"));
        assertNotNull(response.getBody().get("userid"));

        createdUserId = (Integer) response.getBody().get("userid");
    }

    @Test
    @Order(2)
    void testCreateUser_UsernameExists() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("newpass");

        ResponseEntity<Map> response = restTemplate.postForEntity("/user", user, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("用户名已存在！", response.getBody().get("message"));
    }

    @Test
    @Order(3)
    void testCreateUser_MissingFields() {
        User user = new User();
        user.setUsername("");
        user.setPassword(null);

        ResponseEntity<Map> response = restTemplate.postForEntity("/user", user, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("用户名或密码不能为空！", response.getBody().get("message"));
    }

    @Test
    @Order(4)
    void testLogin_Success() {
        User login = new User();
        login.setUsername("newuser");
        login.setPassword("123456");

        ResponseEntity<Map> response = restTemplate.postForEntity("/user/login", login, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("登录成功", response.getBody().get("message"));
    }

    @Test
    @Order(5)
    void testLogin_WrongPassword() {
        User login = new User();
        login.setUsername("newuser");
        login.setPassword("wrong");

        ResponseEntity<Map> response = restTemplate.postForEntity("/user/login", login, Map.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("密码错误", response.getBody().get("message"));
    }

    @Test
    @Order(6)
    void testLogin_UserNotExist() {
        User login = new User();
        login.setUsername("nouser");
        login.setPassword("123");

        ResponseEntity<Map> response = restTemplate.postForEntity("/user/login", login, Map.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("用户不存在", response.getBody().get("message"));
    }

    @Test
    @Order(7)
    void testGetAllUsers() {
        ResponseEntity<User[]> response = restTemplate.getForEntity("/user", User[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(8)
    void testGetUserById_Found() {
        ResponseEntity<User> response = restTemplate.getForEntity("/user/" + createdUserId, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newuser", response.getBody().getUsername());
    }

    @Test
    @Order(9)
    void testGetUserById_NotFound() {
        ResponseEntity<User> response = restTemplate.getForEntity("/user/999999", User.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(10)
    void testCheckUsername() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity("/user/check-username?username=newuser", Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @Order(11)
    void testUpdateUser_Success() {
        User updated = new User();
        updated.setUsername("newuser_updated");
        updated.setPassword("updated123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<User> response = restTemplate.exchange("/user/" + createdUserId, HttpMethod.PUT, entity, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newuser_updated", response.getBody().getUsername());
    }

    @Test
    @Order(12)
    void testUpdateUser_NotFound() {
        User updated = new User();
        updated.setUsername("test");
        updated.setPassword("test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updated, headers);

        ResponseEntity<User> response = restTemplate.exchange("/user/999999", HttpMethod.PUT, entity, User.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(13)
    void testChangePassword_Success() {
        Map<String, String> body = Map.of(
                "userId", String.valueOf(createdUserId),
                "oldPassword", "updated123",
                "newPassword", "newPass"
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/user/change-password", body, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("密码修改成功", response.getBody());
    }

    @Test
    @Order(14)
    void testChangePassword_Failure() {
        Map<String, String> body = Map.of(
                "userId", "999999",
                "oldPassword", "x",
                "newPassword", "y"
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/user/change-password", body, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("原密码错误或用户不存在", response.getBody());
    }

    @Test
    @Order(15)
    void testDeleteUser_Success() {
        ResponseEntity<Void> response = restTemplate.exchange("/user/" + createdUserId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(16)
    void testDeleteUser_NotFound() {
        ResponseEntity<Void> response = restTemplate.exchange("/user/999999", HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
