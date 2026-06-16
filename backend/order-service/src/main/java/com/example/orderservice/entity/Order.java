package com.example.orderservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long publisherId;
    private String publisherOpenid;
    private Long receiverId;
    private String receiverOpenid;
    private String stationName;
    private String itemSize;
    private BigDecimal reward;
    private String pickupCode;
    private String deliveryAddress;
    private String status;
    private String deliveryPhoto;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime claimedTime;
    private LocalDateTime completedTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
    public String getPublisherOpenid() { return publisherOpenid; }
    public void setPublisherOpenid(String publisherOpenid) { this.publisherOpenid = publisherOpenid; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getReceiverOpenid() { return receiverOpenid; }
    public void setReceiverOpenid(String receiverOpenid) { this.receiverOpenid = receiverOpenid; }
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
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public LocalDateTime getClaimedTime() { return claimedTime; }
    public void setClaimedTime(LocalDateTime claimedTime) { this.claimedTime = claimedTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
}