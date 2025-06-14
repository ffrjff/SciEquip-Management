package com.seproject.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seproject.demo.entity.UserInfo;
import com.seproject.demo.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserInfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserInfo testUserInfo;

    @BeforeEach
    public void setUp() {
        testUserInfo = new UserInfo();
        testUserInfo.setUserid(1);
        testUserInfo.setEmployeeid("E001");
        testUserInfo.setEmail("test@example.com");
        testUserInfo.setPhone("1234567890");
        testUserInfo.setGender(0);
        testUserInfo.setAge(30);
        userInfoRepository.save(testUserInfo);
    }

    @Test
    public void getUserInfo_existingUser_returnsOk() throws Exception {
        mockMvc.perform(get("/userinfo/{userid}", testUserInfo.getUserid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeid").value("E001"));
    }

    @Test
    public void getUserInfo_notFound_returns404() throws Exception {
        mockMvc.perform(get("/userinfo/{userid}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveOrUpdateUserInfo_insertNewUser_returnsOk() throws Exception {
        UserInfo newUser = new UserInfo();
        newUser.setUserid(2);
        newUser.setEmployeeid("E002");
        newUser.setEmail("new@example.com");
        newUser.setPhone("9876543210");
        newUser.setGender(1);
        newUser.setAge(28);

        mockMvc.perform(post("/userinfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeid").value("E002"));
    }

    @Test
    public void saveOrUpdateUserInfo_updateExistingUser_returnsOk() throws Exception {
        testUserInfo.setEmail("updated@example.com");

        mockMvc.perform(post("/userinfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    public void uploadAvatar_success_returnsOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "avatar", "head.png", "image/png", "fake image content".getBytes());

        mockMvc.perform(multipart("/userinfo/avatar")
                        .file(file)
                        .param("userid", String.valueOf(testUserInfo.getUserid())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarPath").exists())
                .andExpect(jsonPath("$.message").value("头像上传成功"));

        Optional<UserInfo> updated = userInfoRepository.findByUserid(testUserInfo.getUserid());
        assertThat(updated).isPresent();
        assertThat(updated.get().getAvatar()).contains("/avatar/");
    }

    @Test
    public void uploadAvatar_userNotFound_returnsNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "avatar", "head.png", "image/png", "fake image".getBytes());

        mockMvc.perform(multipart("/userinfo/avatar")
                        .file(file)
                        .param("userid", "9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void uploadAvatar_exception_returns500() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "avatar", "head.png", "image/png", new byte[0]) {
            @Override
            public void transferTo(File dest) {
                throw new RuntimeException("Simulated error");
            }
        };

        mockMvc.perform(multipart("/userinfo/avatar")
                        .file(file)
                        .param("userid", String.valueOf(testUserInfo.getUserid())))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").exists());
    }
}
