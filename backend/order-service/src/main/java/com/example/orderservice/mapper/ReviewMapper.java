package com.example.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.orderservice.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    Review selectByOrderIdAndReviewer(@Param("orderId") Long orderId, @Param("reviewerOpenid") String reviewerOpenid);

    List<Review> selectByRevieweeOpenid(@Param("revieweeOpenid") String revieweeOpenid);

    List<Review> selectByReviewerOpenid(@Param("reviewerOpenid") String reviewerOpenid);

    List<Review> selectByOrderId(@Param("orderId") Long orderId);
}