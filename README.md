# 优惠券系统

项目技术:
SpringCloud、SpringDataJPA、MySQL、Redis、Kafka 

项目简介：
该系统提供优惠券的模板、分发、结算、权限验证四个微服务

项目内容:
1.MySQL提供数据库支持，利于操作优惠券的各类数据
2 Redis实现用户数据与优惠券码的储存，提高系统的响应速度
3.SpringCloud实现服务注册、分发、熔断降级
4.Kafka实现将过期优惠券异步回写


其中的模板微服务中的查询方法
![image](https://user-images.githubusercontent.com/67719239/119512095-98752680-bda5-11eb-8f00-d00aaec52fad.png)


项目中每个方法的脑图(进群找群主要)：
![image](https://user-images.githubusercontent.com/67719239/119512225-b478c800-bda5-11eb-8612-7859da575821.png)


有木有前端的小伙伴一起搞一个完整的项目，现在我后端基本功能差不多都实现了。
交流QQ群：725936761
