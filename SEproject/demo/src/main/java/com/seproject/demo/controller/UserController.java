package com.seproject.demo.controller;

import com.seproject.demo.entity.User;
import com.seproject.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 构造器注入UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 查询所有用户 GET /user
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // 根据ID查询用户 GET /user/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 修改用户 PUT /user/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());

        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // 删除用户 DELETE /user/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping
//    public ResponseEntity<String> createUser(@RequestBody User user) {
//        // 验证用户名是否已存在
//        if (userService.findByUsername(user.getUsername()).isPresent()) {
//            return ResponseEntity.badRequest().body("用户名已存在！");
//        }
//
//        // 验证输入的完整性
//        if (user.getUsername() == null || user.getUsername().isEmpty() ||
//                user.getPassword() == null || user.getPassword().isEmpty()) {
//            return ResponseEntity.badRequest().body("用户名或密码不能为空！");
//        }
//
//        // 保存用户
//        userService.save(user);
//        return ResponseEntity.ok("注册成功！");
//    }
@PostMapping
public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
    // 验证用户名是否已存在
    if (userService.findByUsername(user.getUsername()).isPresent()) {
        return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在！"));
    }

    // 验证输入的完整性
    if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码不能为空！"));
    }

    // 保存用户
    User savedUser = userService.save(user);
    return ResponseEntity.ok(Map.of(
            "message", "注册成功！",
            "userid", savedUser.getUserid()
    ));
}

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User loginUser) {
    if (loginUser.getUsername() == null || loginUser.getPassword() == null) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", "用户名或密码不能为空"));
    }
    Optional<User> userOpt = userService.findByUsername(loginUser.getUsername());
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        if (user.getPassword().equals(loginUser.getPassword())) {
            // 返回userid和提示信息
            return ResponseEntity.ok(
                    Map.of(
                            "message", "登录成功",
                            "userid", user.getUserid(),
                            "username", user.getUsername()
                    ));
        } else {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("message", "密码错误"));
        }
    } else {
        return ResponseEntity
                .status(401)
                .body(Map.of("message", "用户不存在"));
    }
}
    // 在UserController中添加以下端点
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        User updatedUser = userService.changePassword(userId, oldPassword, newPassword);
        if (updatedUser != null) {
            return ResponseEntity.ok("密码修改成功");
        } else {
            return ResponseEntity.status(400).body("原密码错误或用户不存在");
        }
    }

}
