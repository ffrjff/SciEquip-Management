package com.seproject.demo.service;

import com.seproject.demo.entity.User;
import com.seproject.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 查询所有用户
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 根据ID查询用户
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    // 新增用户
    public User save(User user) {
        return userRepository.save(user);
    }

    // 删除用户
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    // 在UserService中添加以下方法
    public User changePassword(int userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                return userRepository.save(user);
            }
        }
        return null;
    }
}
