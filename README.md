# Mall Order Demo

一个基于 Spring Boot 的电商订单库存管理后端项目，围绕商品、用户、订单、库存一致性、缓存和消息队列搭建了一条完整的后端业务链路。

项目重点不是堆复杂业务，而是把后端开发中的常见能力串起来：HTTP 接口、参数校验、分层架构、数据库操作、事务、登录拦截、AOP 日志、Redis 缓存与锁、RocketMQ 消息、自动化测试。

## 技术栈

- Java 17
- Spring Boot 3.3.13
- Maven
- MyBatis
- MySQL
- Redis / Spring Data Redis
- RocketMQ
- Spring Validation
- Spring AOP
- Spring MVC Interceptor
- JUnit 5 / MockMvc
- Docker
- Git / GitHub

## 核心功能

- 商品管理：新增商品、查询列表、查询详情、修改商品。
- 用户模块：注册、登录、根据 token 查询当前用户。
- 订单模块：创建订单、查询订单详情、取消订单。
- 库存控制：下单扣减库存，取消订单恢复库存，避免重复取消导致库存多加。
- 统一返回：所有业务接口返回 `code / message / data` 结构。
- 全局异常：业务异常、参数校验异常、路径参数错误统一转换为 JSON。
- 登录拦截：订单接口通过 `X-Token` 做登录校验。
- 请求日志：使用 AOP 记录请求方式、路径和耗时。
- Redis 缓存：商品详情使用 Redis String 缓存，商品变更后删除缓存。
- Redis 锁：创建订单时使用 `SET NX EX` 思路实现学习版库存锁。
- RocketMQ：创建订单成功后发送订单消息，消费者接收并打印订单号。
- 自动化测试：覆盖接口成功场景、业务失败、参数校验、登录拦截、下单和取消订单。

## 业务链路

项目采用典型的后端分层结构：

```text
Controller -> Service -> Mapper -> MyBatis XML -> MySQL
```

创建订单的主链路：

```text
POST /order/create
-> 登录拦截器校验 X-Token
-> OrderController 接收请求
-> OrderService 获取 Redis 商品锁
-> 查询商品并校验库存
-> 扣减商品库存
-> 写入 order_info 和 order_item
-> 删除商品详情缓存
-> 发送 RocketMQ 订单消息
-> Consumer 接收消息并打印日志
```

取消订单的主链路：

```text
POST /order/cancel/{orderNo}
-> 查询订单和订单明细
-> 校验订单是否已取消
-> 修改订单状态
-> 恢复商品库存
-> 删除商品详情缓存
```

## 项目结构

```text
src/main/java/com/henry/mallorder
├── common      # 统一返回和异常处理
├── config      # 登录拦截器配置
├── log         # AOP 请求日志
├── product     # 商品模块
├── order       # 订单模块和 RocketMQ 消息
└── user        # 用户模块

src/main/resources
├── application.yml
└── mapper      # MyBatis XML
```

## 数据库设计

当前使用 4 张核心表：

- `product`：商品表。
- `user_info`：用户表。
- `order_info`：订单主表。
- `order_item`：订单明细表。

项目中使用了基础 SQL、唯一索引和普通索引，例如：

- `uk_order_no`：保证订单号唯一。
- `uk_username`：保证用户名唯一。
- `idx_product_name`：支持商品名称查询场景。
- `idx_user_id`：支持用户订单查询场景。

金额字段使用 MySQL `DECIMAL`，Java 中使用 `BigDecimal`，避免浮点数精度问题。

## 本地运行

### 1. 准备环境

- JDK 17
- MySQL 8.x
- Docker Desktop
- Redis
- RocketMQ NameServer / Broker

检查 Java：

```powershell
java -version
javac -version
```

### 2. 初始化数据库

创建数据库并执行初始化 SQL：

```sql
CREATE DATABASE mall_order_demo DEFAULT CHARACTER SET utf8mb4;
```

然后执行：

```text
sql/init.sql
```

### 3. 本地配置

本地数据库账号密码放在：

```text
src/main/resources/application-local.yml
```

该文件已被 Git 忽略，不会提交到仓库。

示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_order_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4. 启动依赖服务

Redis 默认连接：

```text
localhost:6379
```

RocketMQ 默认连接：

```text
NameServer: localhost:9876
```

项目里的 RocketMQ Producer group：

```text
mall-order-producer-group
```

订单消息 Topic：

```text
order-topic
```

### 5. 启动项目

```powershell
.\mvnw.cmd spring-boot:run
```

### 6. 运行测试

```powershell
.\mvnw.cmd test
```

当前测试覆盖：

- 统一返回结构。
- 商品查询和商品不存在。
- 用户注册、登录、查询当前用户。
- 未登录访问订单接口。
- 创建订单成功、商品不存在、库存不足、参数错误。
- 查询订单不存在。
- 取消订单成功和重复取消。

## 接口概览

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 健康检查 | GET | `/hello` | 验证服务启动 |
| 商品 | POST | `/product/add` | 新增商品 |
| 商品 | GET | `/product/list` | 商品列表 |
| 商品 | GET | `/product/{id}` | 商品详情，接入 Redis 缓存 |
| 商品 | PUT | `/product/{id}` | 修改商品并删除缓存 |
| 用户 | POST | `/user/register` | 注册用户 |
| 用户 | POST | `/user/login` | 登录并返回 token |
| 用户 | GET | `/user/me` | 根据 token 查询当前用户 |
| 订单 | POST | `/order/create` | 创建订单，扣库存并发送 MQ 消息 |
| 订单 | GET | `/order/{orderNo}` | 查询订单详情 |
| 订单 | POST | `/order/cancel/{orderNo}` | 取消订单并恢复库存 |

订单接口需要在 Header 中携带：

```text
X-Token: 登录后返回的 token
```

## 项目亮点

- 使用 `Result<T>` 统一接口返回，降低前端和测试判断成本。
- 使用 `BusinessException` 和全局异常处理区分业务失败与系统异常。
- 使用 `@Transactional` 保证订单、库存数据一致性。
- 使用登录拦截器保护订单接口，体现 Controller 前置校验能力。
- 使用 AOP 统一记录请求日志，避免在每个接口里重复写日志代码。
- 使用 Redis 缓存商品详情，并在商品变更、下单、取消订单后主动删除缓存。
- 使用 Redis `SET NX EX` 思路实现学习版库存锁，理解并发下单控制。
- 使用 RocketMQ 跑通订单创建后的生产者 / 消费者链路。
- 使用 MockMvc 测试核心接口和典型失败场景，保证后续改动不破坏已有功能。

## 后续优化方向

- 下单时不再从请求体传 `userId`，而是从登录 token 中解析当前用户。
- 将错误码集中到枚举或常量类中，避免业务代码里散落数字。
- RocketMQ 消息发送可以调整到数据库事务提交成功后执行，避免事务回滚但消息已发送。
- 测试可以继续拆分为单元测试和集成测试，减少对 MySQL、Redis、RocketMQ 同时在线的依赖。
- 用户密码改为 BCrypt 等加密存储方式，避免明文密码。
- 登录 token 可迁移到 Redis 或 JWT，解决项目重启后 token 失效的问题。
- RocketMQ 消费端可以补充幂等处理，避免重复消费带来的业务副作用。

## 当前定位

这是一个面向后端基础能力训练和项目展示的学习型项目。当前实现刻意保持业务规模可控，重点放在链路完整、代码分层清晰、关键中间件能跑通，并且每个模块都能讲清楚为什么这样设计。
