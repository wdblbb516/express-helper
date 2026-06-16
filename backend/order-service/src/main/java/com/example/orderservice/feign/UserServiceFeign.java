package com.example.orderservice.feign;

import com.example.orderservice.dto.ApiResponse;
import com.example.orderservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceFeign {

    @GetMapping("/api/users/openid/{openid}")
    ApiResponse<UserResponse> getUserByOpenid(@PathVariable String openid);

    @GetMapping("/api/users/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable Long id);
}