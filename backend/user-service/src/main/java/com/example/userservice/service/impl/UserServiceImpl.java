package com.example.userservice.service.impl;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserUpdateRequest;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponse login(LoginRequest request) {
        // 先查找用户是否存在
        User existingUser = userMapper.selectByOpenid(request.getOpenid());

        if (existingUser != null) {
            // 用户存在，更新信息
            existingUser.setNickname(request.getNickname());
            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
                existingUser.setAvatarUrl(request.getAvatarUrl());
            }
            existingUser.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(existingUser);
            return convertToResponse(existingUser);
        }

        // 用户不存在，创建新用户
        User newUser = new User();
        newUser.setOpenid(request.getOpenid());
        newUser.setNickname(request.getNickname());
        newUser.setAvatarUrl(request.getAvatarUrl());
        newUser.setDormBuilding("未设置");
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        userMapper.insert(newUser);
        return convertToResponse(newUser);
    }

    @Override
    public UserResponse getUserByOpenid(String openid) {
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            return null;
        }
        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String openid, UserUpdateRequest request) {
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            return null;
        }

        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setNickname(request.getNickname());
        }
        if (request.getDormBuilding() != null) {
            user.setDormBuilding(request.getDormBuilding());
        }
        if (request.getWechat() != null) {
            user.setWechat(request.getWechat());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}