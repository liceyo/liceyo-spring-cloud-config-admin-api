# Spring Cloud可视化配置中心后台项目

> Spring Cloud可视化配置中心分为前端项目和后端项目，前端提供可视化页面，后端提供功能接口。前端项目地址为[spring-cloud-config-admin-ui](https://github.com/liceyo/spring-cloud-config-admin-ui)

## 使用maven方式集成

> 没有使用远程仓库，自己使用源码打包吧

使用`maven install`打包后引入依赖即可
```
<dependency>
    <groupId>com.liceyo</groupId>
    <artifactId>liceyo-spring-cloud-config-admin-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 前提

1. 配置中心需要配置：`spring.cloud.config.server.prefix`属性，以避免配置获取接口与管理接口冲突。

2. 使用http刷新模式，需要其他实例引入actuator，并确保refresh端点开放`management.endpoints.web.exposure.include=refresh`，确保http刷新接口可用。
    
    ```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    ```
3. 应用信息：服务名、环境名和标签名都以文件方式存储，请务必赋予读写权限。

4. 配置中心需要注册到注册中心。

5. 目前没有做鉴权，考虑是用jwt做，也想不做鉴权，让自己实现的鉴权服务来做，还在考虑中，以后有时间再做吧。

## 实例配置刷新

> 目前只实现了http接口调用具体实例刷新接口的方式，通过消息队列的刷新没有实现，个人感觉消息队列刷新方式耦合太高，后续考虑实现、

默认刷新接口为`/actuator/refresh`

## 服务自动上线

> 新注册的服务可以被感知到，使用`/api/storage/services`接口可以得到所有服务列表。注意，新上线得服务默认是没有保存到文件的，需要手动保存。

## License

[MIT](https://github.com/liceyo/liceyo-spring-cloud-config-admin-api/blob/master/LICENSE)

Copyright (c) 2019-present liceyo