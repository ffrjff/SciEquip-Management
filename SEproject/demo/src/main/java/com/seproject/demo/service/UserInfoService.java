package com.seproject.demo.service;

import com.seproject.demo.entity.UserInfo;
import com.seproject.demo.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;

    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    public Optional<UserInfo> findByUserid(int userid) {
        return userInfoRepository.findByUserid(userid);
    }

    public UserInfo createDefaultUserInfo(int userid) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserid(userid);
        userInfo.setEmployeeid("");
        userInfo.setEmail("");
        userInfo.setGender(2); // 默认未知
        userInfo.setAge(0);
        userInfo.setPhone("");
        userInfo.setAvatar("/images/default-avatar.png"); // 设置默认头像路径
        return userInfoRepository.save(userInfo);
    }
}