package com.example.orderservice.dto;

public class ReviewRequest {

    private Long orderId;
    private String reviewerOpenid;
    private String revieweeOpenid;
    private Integer rating;
    private Integer communication;
    private Integer speed;
    private Integer careful;
    private String comment;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getReviewerOpenid() { return reviewerOpenid; }
    public void setReviewerOpenid(String reviewerOpenid) { this.reviewerOpenid = reviewerOpenid; }
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
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}