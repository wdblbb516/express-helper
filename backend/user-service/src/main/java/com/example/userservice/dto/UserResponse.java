package com.example.userservice.dto;

public class UserResponse {

    private Long id;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private String dormBuilding;
    private String wechat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDormBuilding() {
        return dormBuilding;
    }

    public void setDormBuilding(String dormBuilding) {
        this.dormBuilding = dormBuilding;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
}