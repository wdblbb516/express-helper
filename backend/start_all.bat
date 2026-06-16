@echo off
chcp 65001
echo ==============================================
echo          快递帮分布式系统 - 启动脚本
echo ==============================================
echo.

echo 1. 启动 Nacos 服务注册中心...
start "Nacos" cmd /k "cd d:\Neusoft\express-helper\backend\nacos\nacos\bin && startup.cmd -m standalone"
timeout /t 10 /nobreak >nul

echo 2. 启动 MySQL 数据库...
echo 请确保 MySQL 已在本地运行 (端口: 3306)
timeout /t 3 /nobreak >nul

echo 3. 启动 user-service (端口: 8081)...
start "User Service" cmd /k "cd d:\Neusoft\express-helper\backend && mvn spring-boot:run -pl user-service"
timeout /t 8 /nobreak >nul

echo 4. 启动 order-service (端口: 8082)...
start "Order Service" cmd /k "cd d:\Neusoft\express-helper\backend && mvn spring-boot:run -pl order-service"
timeout /t 8 /nobreak >nul

echo 5. 启动 station-service (端口: 8083)...
start "Station Service" cmd /k "cd d:\Neusoft\express-helper\backend && mvn spring-boot:run -pl station-service"

echo.
echo ==============================================
echo           所有服务启动完成！
echo ==============================================
echo.
echo Nacos:       http://localhost:8848/nacos
echo user-service: http://localhost:8081
echo order-service: http://localhost:8082
echo station-service: http://localhost:8083
echo.
echo 按任意键退出...
pause >nul