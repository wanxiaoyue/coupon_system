-- 登录 MySQL 服务器
mysql -hlocalhost -uroot -p123456

-- 创建数据库 imooc_coupon_data
CREATE DATABASE IF NOT EXISTS wanxiaoyuan_coupon_data;

-- 登录 MySQL 服务器, 并进入到 wanxiaoyuan_coupon_data 数据库中
mysql -hlocalhost -uroot -p123456 -Dwanxiaoyuan_coupon_data