-- 创建数据库
CREATE DATABASE IF NOT EXISTS express_helper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE express_helper;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    openid VARCHAR(100) NOT NULL UNIQUE COMMENT '微信OpenID',
    nickname VARCHAR(100) COMMENT '用户昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    dorm_building VARCHAR(50) DEFAULT NULL COMMENT '宿舍楼',
    wechat VARCHAR(50) COMMENT '微信号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    publisher_id BIGINT COMMENT '发布者ID',
    publisher_openid VARCHAR(100) NOT NULL COMMENT '发布者OpenID',
    receiver_id BIGINT COMMENT '接单者ID',
    receiver_openid VARCHAR(100) COMMENT '接单者OpenID',
    station_name VARCHAR(100) NOT NULL COMMENT '快递站点名称',
    item_size VARCHAR(20) NOT NULL COMMENT '物品大小(small/medium/large)',
    reward DECIMAL(10,2) NOT NULL COMMENT '悬赏金额',
    pickup_code VARCHAR(20) NOT NULL COMMENT '取件码',
    delivery_address VARCHAR(500) NOT NULL COMMENT '送达地址',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态(pending/claimed/pending_confirm/completed/canceled)',
    delivery_photo VARCHAR(500) COMMENT '送达照片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    claimed_time DATETIME COMMENT '接单时间',
    completed_time DATETIME COMMENT '完成时间',
    INDEX idx_publisher_openid (publisher_openid),
    INDEX idx_receiver_openid (receiver_openid),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 评价表
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    reviewer_openid VARCHAR(100) NOT NULL COMMENT '评价者OpenID',
    reviewee_openid VARCHAR(100) NOT NULL COMMENT '被评价者OpenID',
    rating INT NOT NULL COMMENT '综合评分(1-5)',
    communication INT NOT NULL COMMENT '沟通评分(1-5)',
    speed INT NOT NULL COMMENT '速度评分(1-5)',
    careful INT NOT NULL COMMENT '细心评分(1-5)',
    comment VARCHAR(500) COMMENT '评语',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_reviewer_openid (reviewer_openid),
    INDEX idx_reviewee_openid (reviewee_openid),
    UNIQUE KEY uk_order_reviewer (order_id, reviewer_openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 插入测试用户数据
INSERT INTO users (openid, nickname, avatar_url, dorm_building, wechat) VALUES 
('test_openid_1', '测试用户1号(发布者)', '', '3栋', 'wx123456'),
('test_openid_2', '测试用户2号(接单者)', '', '5栋', 'wx654321');

-- 插入测试订单数据
INSERT INTO orders (order_no, publisher_id, publisher_openid, station_name, item_size, reward, pickup_code, delivery_address, status) VALUES 
('ORD20240101001', 1, 'test_openid_1', '菜鸟驿站-东门站', 'medium', 5.00, 'A1234', '3栋302室', 'pending'),
('ORD20240101002', 1, 'test_openid_1', '顺丰速运-中心站', 'small', 3.00, 'B5678', '3栋302室', 'claimed'),
('ORD20240101003', 2, 'test_openid_2', '京东物流-西门站', 'large', 10.00, 'C9012', '5栋501室', 'completed');

-- 更新已接单订单的接单者信息
UPDATE orders SET receiver_id = 2, receiver_openid = 'test_openid_2', status = 'claimed', claimed_time = NOW() WHERE order_no = 'ORD20240101002';

-- 更新已完成订单的状态
UPDATE orders SET receiver_id = 1, receiver_openid = 'test_openid_1', status = 'completed', claimed_time = NOW(), completed_time = NOW() WHERE order_no = 'ORD20240101003';

-- 插入测试评价数据
INSERT INTO reviews (order_id, reviewer_openid, reviewee_openid, rating, communication, speed, careful, comment) VALUES 
(3, 'test_openid_2', 'test_openid_1', 5, 5, 5, 5, '非常好的发布者，沟通顺畅！'),
(3, 'test_openid_1', 'test_openid_2', 4, 5, 4, 5, '接单很及时，包裹完好送达。');