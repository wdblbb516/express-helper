package com.example.userservice.controller;

import com.example.userservice.dto.ApiResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserUpdateRequest;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "用户管理", description = "用户注册、登录、信息查询与修改接口")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录或注册，根据openid判断是否为新用户")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Parameter(description = "登录请求体", required = true)
            @RequestBody LoginRequest request) {
        UserResponse user = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/openid/{openid}")
    @Operation(summary = "根据openid查询用户", description = "通过用户openid获取用户详细信息")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByOpenid(
            @Parameter(description = "用户openid", required = true)
            @PathVariable String openid) {
        UserResponse user = userService.getUserByOpenid(openid);
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户", description = "通过用户ID获取用户详细信息")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/openid/{openid}")
    @Operation(summary = "更新用户信息", description = "根据openid更新用户的昵称、头像、宿舍楼栋、微信号等信息")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "用户openid", required = true)
            @PathVariable String openid,
            @Parameter(description = "更新请求体", required = true)
            @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(openid, request);
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("更新成功", user));
    }
}