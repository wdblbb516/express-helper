package com.example.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.orderservice.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    IPage<Order> selectPendingOrders(Page<Order> page);

    List<Order> selectByPublisherOpenid(@Param("openid") String openid);

    List<Order> selectByReceiverOpenid(@Param("openid") String openid);

    Order selectByOrderNo(@Param("orderNo") String orderNo);
}