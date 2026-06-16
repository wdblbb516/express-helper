package com.example.orderservice.service.impl;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.Review;
import com.example.orderservice.feign.UserServiceFeign;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.mapper.ReviewMapper;
import com.example.orderservice.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ReviewMapper reviewMapper;
    private final UserServiceFeign userServiceFeign;

    public OrderServiceImpl(OrderMapper orderMapper, ReviewMapper reviewMapper, UserServiceFeign userServiceFeign) {
        this.orderMapper = orderMapper;
        this.reviewMapper = reviewMapper;
        this.userServiceFeign = userServiceFeign;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // 获取发布者信息
        Long publisherId = null;
        try {
            ApiResponse<UserResponse> publisherResponse = userServiceFeign.getUserByOpenid(request.getPublisherOpenid());
            if (publisherResponse != null && publisherResponse.getData() != null) {
                publisherId = publisherResponse.getData().getId();
            }
        } catch (Exception e) {
            // 用户不存在或Feign调用失败，publisherId保持为null
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setPublisherId(publisherId);
        order.setPublisherOpenid(request.getPublisherOpenid());
        order.setStationName(request.getStationName());
        order.setItemSize(request.getItemSize());
        order.setReward(request.getReward());
        order.setPickupCode(request.getPickupCode());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        return convertToResponse(order);
    }

    @Override
    public List<OrderResponse> getPendingOrders(String stationName) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, "pending");
        
        // 如果传入了站点名称，则按站点过滤
        if (stationName != null && !stationName.isEmpty()) {
            queryWrapper.eq(Order::getStationName, stationName);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = orderMapper.selectList(queryWrapper);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse claimOrder(Long orderId, String receiverOpenid) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !"pending".equals(order.getStatus())) {
            return null;
        }

        // 获取接单者信息
        ApiResponse<UserResponse> receiverResponse = userServiceFeign.getUserByOpenid(receiverOpenid);
        Long receiverId = null;
        if (receiverResponse != null && receiverResponse.getData() != null) {
            receiverId = receiverResponse.getData().getId();
        }

        order.setReceiverId(receiverId);
        order.setReceiverOpenid(receiverOpenid);
        order.setStatus("claimed");
        order.setClaimedTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse confirmDelivery(Long orderId, MultipartFile file, String openid) {
        System.out.println("=== 确认送达请求 ===");
        System.out.println("orderId: " + orderId);
        System.out.println("openid: " + openid);
        System.out.println("file: " + (file != null ? file.getOriginalFilename() : "null"));
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            System.out.println("订单不存在");
            return null;
        }
        System.out.println("订单状态: " + order.getStatus());
        System.out.println("接单者Openid: " + order.getReceiverOpenid());
        
        if (!"claimed".equals(order.getStatus())) {
            System.out.println("订单状态不是已接单");
            return null;
        }

        // 验证接单者身份
        if (!openid.equals(order.getReceiverOpenid())) {
            System.out.println("身份验证失败");
            return null;
        }

        // 保存文件到本地
        String photoPath = saveDeliveryPhoto(file, orderId);
        System.out.println("图片保存路径: " + photoPath);
        
        order.setStatus("pending_confirm");
        order.setDeliveryPhoto(photoPath);
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);
        System.out.println("订单更新成功");
        return convertToResponse(order);
    }

    private String saveDeliveryPhoto(MultipartFile file, Long orderId) {
        System.out.println("=== 保存送达照片 ===");
        System.out.println("file is null: " + (file == null));
        
        if (file == null) {
            System.out.println("错误：文件为null");
            return null;
        }
        
        System.out.println("文件原始名称: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");
        System.out.println("文件是否为空: " + file.isEmpty());
        
        // 使用项目内部目录，避免权限问题
        String uploadDir = "./uploads/delivery_photos/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            System.out.println("创建目录: " + dir.getAbsolutePath());
            boolean created = dir.mkdirs();
            System.out.println("目录创建结果: " + created);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".jpg";
        String filename = "delivery_" + orderId + "_" + System.currentTimeMillis() + extension;
        String filePath = uploadDir + filename;

        try {
            File targetFile = new File(filePath);
            System.out.println("保存文件到: " + targetFile.getAbsolutePath());
            System.out.println("目标文件父目录: " + targetFile.getParent());
            System.out.println("目标文件父目录是否存在: " + targetFile.getParentFile().exists());
            
            file.transferTo(targetFile);
            
            if (targetFile.exists()) {
                System.out.println("文件保存成功，文件大小: " + targetFile.length() + " bytes");
            } else {
                System.out.println("文件保存失败，目标文件不存在");
                return null;
            }
            
            String relativePath = "/uploads/delivery_photos/" + filename;
            System.out.println("返回相对路径: " + relativePath);
            return relativePath;
        } catch (IOException e) {
            System.out.println("文件保存失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public OrderResponse confirmReceipt(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !"pending_confirm".equals(order.getStatus())) {
            return null;
        }

        order.setStatus("completed");
        order.setCompletedTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.updateById(order);
        return convertToResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByPublisher(String openid) {
        List<Order> orders = orderMapper.selectByPublisherOpenid(openid);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByReceiver(String openid) {
        List<Order> orders = orderMapper.selectByReceiverOpenid(openid);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse submitReview(ReviewRequest request) {
        // 检查是否已评价
        if (hasReviewed(request.getOrderId(), request.getReviewerOpenid())) {
            return null;
        }

        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setReviewerOpenid(request.getReviewerOpenid());
        review.setRevieweeOpenid(request.getRevieweeOpenid());
        review.setRating(request.getRating());
        review.setCommunication(request.getCommunication());
        review.setSpeed(request.getSpeed());
        review.setCareful(request.getCareful());
        review.setComment(request.getComment());
        review.setCreateTime(LocalDateTime.now());

        reviewMapper.insert(review);
        return convertToReviewResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByReviewee(String openid) {
        List<Review> reviews = reviewMapper.selectByRevieweeOpenid(openid);
        return reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByReviewer(String openid) {
        List<Review> reviews = reviewMapper.selectByReviewerOpenid(openid);
        return reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasReviewed(Long orderId, String reviewerOpenid) {
        Review review = reviewMapper.selectByOrderIdAndReviewer(orderId, reviewerOpenid);
        return review != null;
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);

        // 转换包裹大小为中文
        String itemSize = order.getItemSize();
        if (itemSize != null) {
            response.setItemSize(convertSizeToChinese(itemSize));
        }

        // 获取发布者信息
        if (order.getPublisherOpenid() != null) {
            try {
                ApiResponse<UserResponse> publisherResponse = userServiceFeign.getUserByOpenid(order.getPublisherOpenid());
                if (publisherResponse != null && publisherResponse.getData() != null) {
                    UserResponse publisher = publisherResponse.getData();
                    response.setPublisherNickname(publisher.getNickname());
                    response.setPublisherAvatar(publisher.getAvatarUrl());
                    // 只有接单者可以看到微信号
                    if (!"pending".equals(order.getStatus())) {
                        response.setPublisherWechat(publisher.getWechat());
                    }
                }
            } catch (Exception e) {
                // 忽略Feign调用异常
            }
        }

        // 获取接单者信息
        if (order.getReceiverOpenid() != null) {
            try {
                ApiResponse<UserResponse> receiverResponse = userServiceFeign.getUserByOpenid(order.getReceiverOpenid());
                if (receiverResponse != null && receiverResponse.getData() != null) {
                    UserResponse receiver = receiverResponse.getData();
                    response.setReceiverNickname(receiver.getNickname());
                    response.setReceiverAvatar(receiver.getAvatarUrl());
                    // 只有发布者可以看到微信号
                    response.setReceiverWechat(receiver.getWechat());
                }
            } catch (Exception e) {
                // 忽略Feign调用异常
            }
        }

        // 将图片路径转换为完整URL
        String deliveryPhoto = order.getDeliveryPhoto();
        System.out.println("=== 转换订单响应 ===");
        System.out.println("订单ID: " + order.getId());
        System.out.println("订单状态: " + order.getStatus());
        System.out.println("deliveryPhoto 原始值: " + deliveryPhoto);
        
        if (deliveryPhoto != null && !deliveryPhoto.isEmpty()) {
            String baseUrl = "http://localhost:8082";
            String photoPath = deliveryPhoto;
            if (!photoPath.startsWith("/")) {
                photoPath = "/" + photoPath;
            }
            String fullUrl = baseUrl + photoPath;
            response.setDeliveryPhoto(fullUrl);
            System.out.println("deliveryPhoto 完整URL: " + fullUrl);
        }

        return response;
    }

    private String convertSizeToChinese(String size) {
        return switch (size.toLowerCase()) {
            case "small", "小件", "小件（如信封）" -> "小件（如信封）";
            case "medium", "中件", "中件（鞋盒大小）" -> "中件（鞋盒大小）";
            case "large", "大件", "大件（行李箱）" -> "大件（行李箱）";
            default -> size;
        };
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);
        
        // 设置carefulness字段以兼容前端
        response.setCarefulness(review.getCareful());

        // 获取评价者信息
        if (review.getReviewerOpenid() != null) {
            try {
                ApiResponse<UserResponse> reviewerResponse = userServiceFeign.getUserByOpenid(review.getReviewerOpenid());
                if (reviewerResponse != null && reviewerResponse.getData() != null) {
                    UserResponse reviewer = reviewerResponse.getData();
                    response.setReviewerNickname(reviewer.getNickname());
                    response.setReviewerAvatar(reviewer.getAvatarUrl());
                    response.setFromNickname(reviewer.getNickname());
                    response.setFromAvatarUrl(reviewer.getAvatarUrl());
                }
            } catch (Exception e) {
                // 忽略Feign调用异常
            }
        }

        return response;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}