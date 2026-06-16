package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    // 前端用 _id，我们返回 id
    @JsonProperty("_id")
    private Long id;

    private String orderNo;
    private Long publisherId;
    private String publisherOpenid;
    private String publisherNickname;
    private String publisherAvatar;

    // 前端用 wechat，我们返回 publisherWechat
    @JsonProperty("wechat")
    private String publisherWechat;

    private Long receiverId;
    private String receiverOpenid;
    private String receiverNickname;
    private String receiverAvatar;
    private String receiverWechat;
    // 前端用 expressPoint，我们返回 stationName
    @JsonProperty("expressPoint")
    private String stationName;

    // 前端用 size，我们返回 itemSize
    @JsonProperty("size")
    private String itemSize;

    private BigDecimal reward;
    private String pickupCode;

    // 前端用 dormBuilding，我们返回 deliveryAddress
    @JsonProperty("dormBuilding")
    private String deliveryAddress;

    private String status;
    private String deliveryPhoto;
    private LocalDateTime createTime;
    private LocalDateTime claimedTime;
    private LocalDateTime completedTime;

    // Getter和Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
    public String getPublisherOpenid() { return publisherOpenid; }
    public void setPublisherOpenid(String publisherOpenid) { this.publisherOpenid = publisherOpenid; }
    public String getPublisherNickname() { return publisherNickname; }
    public void setPublisherNickname(String publisherNickname) { this.publisherNickname = publisherNickname; }
    public String getPublisherAvatar() { return publisherAvatar; }
    public void setPublisherAvatar(String publisherAvatar) { this.publisherAvatar = publisherAvatar; }
    public String getPublisherWechat() { return publisherWechat; }
    public void setPublisherWechat(String publisherWechat) { this.publisherWechat = publisherWechat; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getReceiverOpenid() { return receiverOpenid; }
    public void setReceiverOpenid(String receiverOpenid) { this.receiverOpenid = receiverOpenid; }
    public String getReceiverNickname() { return receiverNickname; }
    public void setReceiverNickname(String receiverNickname) { this.receiverNickname = receiverNickname; }
    public String getReceiverAvatar() { return receiverAvatar; }
    public void setReceiverAvatar(String receiverAvatar) { this.receiverAvatar = receiverAvatar; }
    public String getReceiverWechat() { return receiverWechat; }
    public void setReceiverWechat(String receiverWechat) { this.receiverWechat = receiverWechat; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getItemSize() { return itemSize; }
    public void setItemSize(String itemSize) { this.itemSize = itemSize; }
    public BigDecimal getReward() { return reward; }
    public void setReward(BigDecimal reward) { this.reward = reward; }
    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliveryPhoto() { return deliveryPhoto; }
    public void setDeliveryPhoto(String deliveryPhoto) { this.deliveryPhoto = deliveryPhoto; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getClaimedTime() { return claimedTime; }
    public void setClaimedTime(LocalDateTime claimedTime) { this.claimedTime = claimedTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
}
