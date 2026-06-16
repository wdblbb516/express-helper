package com.example.orderservice.dto;

import java.math.BigDecimal;

public class OrderCreateRequest {

    private String publisherOpenid;
    private String stationName;
    private String itemSize;
    private BigDecimal reward;
    private String pickupCode;
    private String deliveryAddress;

    public String getPublisherOpenid() {
        return publisherOpenid;
    }

    public void setPublisherOpenid(String publisherOpenid) {
        this.publisherOpenid = publisherOpenid;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public BigDecimal getReward() {
        return reward;
    }

    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}