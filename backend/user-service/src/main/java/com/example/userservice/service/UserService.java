package com.example.userservice.service;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserUpdateRequest;

public interface UserService {

    UserResponse login(LoginRequest request);

    UserResponse getUserByOpenid(String openid);

    UserResponse getUserById(Long id);

    UserResponse updateUser(String openid, UserUpdateRequest request);
}