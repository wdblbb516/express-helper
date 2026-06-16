package com.example.orderservice.dto;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long orderId;
    private String reviewerOpenid;
    private String reviewerNickname;
    private String reviewerAvatar;
    private String fromNickname;
    private String fromAvatarUrl;
    private String revieweeOpenid;
    private Integer rating;
    private Integer communication;
    private Integer speed;
    private Integer careful;
    private Integer carefulness;
    private String comment;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getReviewerOpenid() { return reviewerOpenid; }
    public void setReviewerOpenid(String reviewerOpenid) { this.reviewerOpenid = reviewerOpenid; }
    public String getReviewerNickname() { return reviewerNickname; }
    public void setReviewerNickname(String reviewerNickname) { this.reviewerNickname = reviewerNickname; }
    public String getReviewerAvatar() { return reviewerAvatar; }
    public void setReviewerAvatar(String reviewerAvatar) { this.reviewerAvatar = reviewerAvatar; }
    public String getFromNickname() { return fromNickname; }
    public void setFromNickname(String fromNickname) { this.fromNickname = fromNickname; }
    public String getFromAvatarUrl() { return fromAvatarUrl; }
    public void setFromAvatarUrl(String fromAvatarUrl) { this.fromAvatarUrl = fromAvatarUrl; }
    public String getRevieweeOpenid() { return revieweeOpenid; }
    public void setRevieweeOpenid(String revieweeOpenid) { this.revieweeOpenid = revieweeOpenid; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getCommunication() { return communication; }
    public void setCommunication(Integer communication) { this.communication = communication; }
    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }
    public Integer getCareful() { return careful; }
    public void setCareful(Integer careful) { this.careful = careful; }
    public Integer getCarefulness() { return carefulness; }
    public void setCarefulness(Integer carefulness) { this.carefulness = carefulness; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}