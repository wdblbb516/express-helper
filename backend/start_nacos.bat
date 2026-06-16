@echo off
chcp 65001
echo ==============================================
echo          启动 Nacos 服务注册中心
echo ==============================================
echo.

cd d:\Neusoft\express-helper\backend\nacos\nacos\bin
startup.cmd -m standalone

echo Nacos 启动成功！访问地址: http://localhost:8848/nacos
echo 用户名: nacos
echo 密码: nacos
echo.
pause