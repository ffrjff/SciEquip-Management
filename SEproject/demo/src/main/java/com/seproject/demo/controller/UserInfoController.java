package com.seproject.demo.controller;

import com.seproject.demo.entity.UserInfo;
import com.seproject.demo.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/userinfo")
public class UserInfoController {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/{userid}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable int userid) {
        Optional<UserInfo> userInfo = userInfoService.findByUserid(userid);
        return userInfo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserInfo> saveOrUpdateUserInfo(@RequestBody UserInfo userInfo) {
        Optional<UserInfo> existingUserInfo = userInfoService.findByUserid(userInfo.getUserid());

        if (existingUserInfo.isPresent()) {
            // 如果存在，则更新字段
            UserInfo existingUser = existingUserInfo.get();
            existingUser.setEmployeeid(userInfo.getEmployeeid());
            existingUser.setEmail(userInfo.getEmail());
            existingUser.setGender(userInfo.getGender());
            existingUser.setAge(userInfo.getAge());
            existingUser.setPhone(userInfo.getPhone());
            UserInfo updatedInfo = userInfoService.saveUserInfo(existingUser);
            return ResponseEntity.ok(updatedInfo);
        } else {
            // 如果不存在，则新增用户
            UserInfo newInfo = userInfoService.saveUserInfo(userInfo);
            return ResponseEntity.ok(newInfo);
        }
    }

//
//@PostMapping("/avatar")
//public ResponseEntity<Map<String, String>> uploadAvatar(
//        @RequestParam("avatar") MultipartFile file,
//        @RequestParam("userid") int userid) {
//    try {
//        // 1. 确定上传目录
//        String uploadDir;
//
//        // 开发环境 - 保存到项目根目录下的uploads文件夹
//        String projectRoot = System.getProperty("user.dir");
//        uploadDir = projectRoot + "/project-root/avatar/";
//
//        // 或者使用系统临时目录（适用于生产环境）
//        // uploadDir = System.getProperty("java.io.tmpdir") + "/uploads/avatars/";
//
//        // 创建目录（如果不存在）
//        File dir = new File(uploadDir);
//        if (!dir.exists()) dir.mkdirs();
//
//        // 2. 生成唯一文件名
//        String fileExtension = file.getOriginalFilename().substring(
//                file.getOriginalFilename().lastIndexOf("."));
//        String fileName = "avatar_" + userid + "_" + System.currentTimeMillis() + fileExtension;
//        String filePath = uploadDir + fileName;
//
//        // 3. 保存文件
//        file.transferTo(new File(filePath));
//
//        // 4. 更新数据库中的头像路径
//        // 使用相对路径或可访问的URL路径
//        String relativePath = "/avatar/" + fileName;  // 需要通过资源处理器映射
//
//        Optional<UserInfo> userInfoOpt = userInfoService.findByUserid(userid);
//        if (userInfoOpt.isPresent()) {
//            UserInfo userInfo = userInfoOpt.get();
//            userInfo.setAvatar(relativePath);
//            userInfoService.saveUserInfo(userInfo);
//            return ResponseEntity.ok(Map.of(
//                    "avatarPath", relativePath,
//                    "message", "头像上传成功"
//            ));
//        }
//        return ResponseEntity.notFound().build();
//    } catch (Exception e) {
//        return ResponseEntity.status(500).body(Map.of(
//                "message", "头像上传失败: " + e.getMessage()
//        ));
//    }
//}

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            @RequestParam("userid") int userid) {
        try {
            // 1. 确定上传目录
            String projectRoot = System.getProperty("user.dir");
//            String uploadDir = projectRoot + "/project-root/avatar/" + userid + "/";
            String uploadDir = projectRoot + "/../project-root/avatar/" + userid + "/";
            System.out.println("directory directory: " + uploadDir);
            // 创建用户专属目录（如果不存在）
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // 2. 确定文件名
            String fileName = "head.png"; // 文件名固定为 head.png
            String filePath = uploadDir + fileName;

            // 3. 保存文件，覆盖旧文件
            file.transferTo(new File(filePath));

            // 4. 更新数据库中的头像路径
            String relativePath = "/avatar/" + userid + "/" + fileName; // 可通过静态资源映射访问

            Optional<UserInfo> userInfoOpt = userInfoService.findByUserid(userid);
            if (userInfoOpt.isPresent()) {
                UserInfo userInfo = userInfoOpt.get();
                userInfo.setAvatar(relativePath); // 更新数据库中的头像路径
                userInfoService.saveUserInfo(userInfo);

                return ResponseEntity.ok(Map.of(
                        "avatarPath", relativePath,
                        "message", "头像上传成功"
                ));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "头像上传失败: " + e.getMessage()
            ));
        }
    }

}