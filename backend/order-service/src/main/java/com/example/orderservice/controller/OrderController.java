package com.example.orderservice.controller;

import com.example.orderservice.dto.*;
import com.example.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "订单管理", description = "订单创建、接单、确认送达、评价等核心业务接口")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "创建订单", description = "发布者创建新的快递代取订单")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Parameter(description = "订单创建请求体", required = true)
            @RequestBody OrderCreateRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待接单订单", description = "获取所有待接单的订单列表，可按站点筛选")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getPendingOrders(
            @Parameter(description = "快递站点名称（可选）")
            @RequestParam(required = false) String stationName) {
        List<OrderResponse> orders = orderService.getPendingOrders(stationName);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询订单", description = "通过订单ID获取订单详细信息")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "订单不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PostMapping("/{id}/claim")
    @Operation(summary = "接单", description = "接单者确认接单，将订单状态改为已接单")
    public ResponseEntity<ApiResponse<OrderResponse>> claimOrder(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "接单者openid", required = true)
            @RequestParam String receiverOpenid) {
        OrderResponse order = orderService.claimOrder(id, receiverOpenid);
        if (order == null) {
            return ResponseEntity.ok(ApiResponse.error(400, "无法接单"));
        }
        return ResponseEntity.ok(ApiResponse.success("接单成功", order));
    }

    @PostMapping(value = "/{id}/delivery", consumes = "multipart/form-data")
    @Operation(summary = "确认送达", description = "接单者上传送达照片，确认订单已送达")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmDelivery(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "送达照片文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "接单者openid", required = true)
            @RequestParam("openid") String openid) {
        OrderResponse order = orderService.confirmDelivery(id, file, openid);
        if (order == null) {
            return ResponseEntity.ok(ApiResponse.error(400, "无法确认送达"));
        }
        return ResponseEntity.ok(ApiResponse.success("确认送达成功，请等待对方确认", order));
    }

    @PostMapping("/{id}/receipt")
    @Operation(summary = "确认收货", description = "发布者确认收到快递，订单完成")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmReceipt(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long id) {
        OrderResponse order = orderService.confirmReceipt(id);
        if (order == null) {
            return ResponseEntity.ok(ApiResponse.error(400, "无法确认收货"));
        }
        return ResponseEntity.ok(ApiResponse.success("确认收货成功", order));
    }

    @GetMapping("/publisher/{openid}")
    @Operation(summary = "获取发布者订单", description = "获取指定发布者创建的所有订单")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByPublisher(
            @Parameter(description = "发布者openid", required = true)
            @PathVariable String openid) {
        List<OrderResponse> orders = orderService.getOrdersByPublisher(openid);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/receiver/{openid}")
    @Operation(summary = "获取接单者订单", description = "获取指定接单者接取的所有订单")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByReceiver(
            @Parameter(description = "接单者openid", required = true)
            @PathVariable String openid) {
        List<OrderResponse> orders = orderService.getOrdersByReceiver(openid);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping("/review")
    @Operation(summary = "提交评价", description = "用户对订单进行评价")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @Parameter(description = "评价请求体", required = true)
            @RequestBody ReviewRequest request) {
        if (orderService.hasReviewed(request.getOrderId(), request.getReviewerOpenid())) {
            return ResponseEntity.ok(ApiResponse.error(400, "您已评价过该订单"));
        }
        ReviewResponse review = orderService.submitReview(request);
        if (review == null) {
            return ResponseEntity.ok(ApiResponse.error(400, "评价失败"));
        }
        return ResponseEntity.ok(ApiResponse.success("评价成功", review));
    }

    @GetMapping("/reviews/{openid}")
    @Operation(summary = "获取他人评价", description = "获取指定用户收到的所有评价")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByReviewee(
            @Parameter(description = "被评价者openid", required = true)
            @PathVariable String openid) {
        List<ReviewResponse> reviews = orderService.getReviewsByReviewee(openid);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/reviews/reviewer/{openid}")
    @Operation(summary = "获取我的评价", description = "获取指定用户发出的所有评价")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByReviewer(
            @Parameter(description = "评价者openid", required = true)
            @PathVariable String openid) {
        List<ReviewResponse> reviews = orderService.getReviewsByReviewer(openid);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}