package com.example.orderservice.service;

import com.example.orderservice.dto.OrderCreateRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.ReviewRequest;
import com.example.orderservice.dto.ReviewResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderCreateRequest request);

    List<OrderResponse> getPendingOrders(String stationName);

    OrderResponse getOrderById(Long id);

    OrderResponse claimOrder(Long orderId, String receiverOpenid);

    OrderResponse confirmDelivery(Long orderId, org.springframework.web.multipart.MultipartFile file, String openid);

    OrderResponse confirmReceipt(Long orderId);

    List<OrderResponse> getOrdersByPublisher(String openid);

    List<OrderResponse> getOrdersByReceiver(String openid);

    ReviewResponse submitReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByReviewee(String openid);

    List<ReviewResponse> getReviewsByReviewer(String openid);

    boolean hasReviewed(Long orderId, String reviewerOpenid);
}