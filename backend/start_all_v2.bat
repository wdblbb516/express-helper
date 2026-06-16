@echo off
chcp 65001
echo ==============================================
echo          快递帮分布式系统 - 启动脚本
echo ==============================================
echo.

echo 1. 检查 Nacos 服务注册中心...
netstat -an | findstr "8848" >nul
if %errorlevel% neq 0 (
    echo    Nacos 未运行，正在启动...
    start "Nacos" cmd /k "cd d:\Neusoft\express-helper\backend\nacos\nacos\bin && startup.cmd -m standalone"
    timeout /t 15 /nobreak >nul
) else (
    echo    Nacos 已运行 (端口: 8848)
)

echo.
echo 2. 检查 MySQL 数据库...
netstat -an | findstr "3306" >nul
if %errorlevel% neq 0 (
    echo    警告: MySQL 未运行，请先启动 MySQL
) else (
    echo    MySQL 已运行 (端口: 3306)
)

echo.
echo 3. 启动 user-service (端口: 8081)...
start "User Service" cmd /k "cd d:\Neusoft\express-helper\backend && java -jar user-service\target\user-service-1.0.0.jar"
timeout /t 10 /nobreak >nul

echo 4. 启动 order-service (端口: 8082)...
start "Order Service" cmd /k "cd d:\Neusoft\express-helper\backend && java -jar order-service\target\order-service-1.0.0.jar"
timeout /t 10 /nobreak >nul

echo 5. 启动 station-service (端口: 8083)...
start "Station Service" cmd /k "cd d:\Neusoft\express-helper\backend && java -jar station-service\target\station-service-1.0.0.jar"

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
echo 等待服务启动...
timeout /t 10 /nobreak >nul
echo.
echo 请检查服务状态。
echo.
pause
