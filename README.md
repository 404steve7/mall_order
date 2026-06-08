# Mall Order Demo

这是一个用于学习 Java 后端基础链路的电商订单库存管理系统。

当前阶段只完成项目结构整理，后续会逐步补充商品、订单、库存、事务、Redis、RocketMQ 等内容。

## 技术栈

- Java 17
- Spring Boot
- Maven
- MyBatis
- MySQL

## 当前结构

```text
src/main/java/com/henry/mallorder
├── common
├── config
├── product
├── order
└── log
```

## 分层说明

- `common`：统一返回结果、通用异常、通用工具类。
- `config`：Spring 配置，比如拦截器、MyBatis、Redis、RocketMQ 配置。
- `product`：商品相关代码。
- `order`：订单、订单明细、下单事务相关代码。
- `log`：订单日志或操作日志相关代码。
- `src/main/resources/mapper`：MyBatis XML SQL 文件。
- `sql/init.sql`：数据库初始化 SQL。
- `docs`：接口文档、学习笔记和截图。

## 当前可用接口

```text
GET /hello
```

这个接口只是用来确认 Spring Boot 项目能启动，后续业务接口会从商品模块开始补充。
