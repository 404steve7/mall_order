# Mall Order Demo

这是一个用于学习 Java 后端基础链路的电商订单库存管理系统。

项目目标不是做复杂系统，而是跑通一个小而完整的后端项目：能启动、能连 MySQL、能写接口、能操作数据库、能用 Apifox 测试，并且能讲清楚代码从请求到数据库的执行过程。

## 技术栈

- Java 17
- Spring Boot 3.3.13
- Maven
- MyBatis
- MySQL
- Apifox
- Git / GitHub

## 已完成功能

- 健康检查接口：`GET /hello`
- 商品新增：`POST /product/add`
- 商品列表：`GET /product/list`
- 商品详情：`GET /product/{id}`
- 商品修改：`PUT /product/{id}`
- 用户注册：`POST /user/register`
- 用户登录：`POST /user/login`
- 查询当前用户：`GET /user/me`
- 创建订单：`POST /order/create`
- 查询订单详情：`GET /order/{orderNo}`
- 取消订单：`POST /order/cancel/{orderNo}`
- 下单时扣减商品库存
- 取消订单时恢复商品库存
- 下单和取消订单接口使用 `@Transactional` 保证事务一致性
- 所有业务接口统一返回 `Result<T>`
- 使用 `BusinessException` 表示业务失败
- 使用 `GlobalExceptionHandler` 统一处理异常返回
- 路径参数类型错误和请求体校验错误统一返回 `4000 参数错误`
- 已补充 17 个自动化测试，覆盖成功返回、业务失败、参数错误、用户登录、成功下单、取消订单和重复取消
- 商品和订单 SQL 初始化脚本

## 项目结构

```text
mall_order_demo
├── docs
│   ├── api.md
│   ├── screenshots
│   └── study-notes.md
├── sql
│   └── init.sql
├── src
│   ├── main
│   │   ├── java/com/henry/mallorder
│   │   │   ├── common
│   │   │   ├── order
│   │   │   └── product
│   │   └── resources
│   │       ├── application.yml
│   │       └── mapper
│   └── test
├── pom.xml
└── README.md
```

## 分层说明

- `controller`：接收 HTTP 请求，定义接口路径。
- `service`：处理业务逻辑，例如下单、扣库存、创建订单。
- `mapper`：定义数据库操作方法。
- `entity`：对应数据库表，例如 `Product`、`OrderInfo`、`OrderItem`。
- `dto`：接收请求参数，例如 `ProductCreateRequest`、`CreateOrderRequest`。
- `vo`：组织返回给前端的数据，例如 `OrderDetailVO`。
- `common`：通用代码，例如统一返回对象 `Result<T>`。
- `common/exception`：统一异常处理，例如 `BusinessException`、`GlobalExceptionHandler`。
- `resources/mapper`：MyBatis XML SQL 文件。
- `sql/init.sql`：数据库初始化 SQL。
- `docs/api.md`：接口文档。
- `docs/study-notes.md`：学习笔记。

## 数据库表

当前数据库名：

```text
mall_order_demo
```

已创建表：

- `product`：商品表。
- `user_info`：用户表。
- `order_info`：订单主表。
- `order_item`：订单明细表。

建表 SQL 见：

```text
sql/init.sql
```

## 本地配置

公共配置放在：

```text
src/main/resources/application.yml
```

本地数据库账号密码放在：

```text
src/main/resources/application-local.yml
```

`application-local.yml` 已被 `.gitignore` 忽略，不会提交到 GitHub。示例：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_order_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 启动步骤

1. 确认 JDK 是 17：

```powershell
java -version
javac -version
```

2. 创建数据库和表：

```sql
SOURCE sql/init.sql;
```

也可以手动复制 `sql/init.sql` 到 MySQL 中执行。

3. 启动项目：

```powershell
.\mvnw.cmd spring-boot:run
```

4. 运行测试：

```powershell
.\mvnw.cmd test
```

## 核心业务流程

### 商品新增

```text
Apifox
↓
ProductController
↓
ProductService
↓
ProductMapper
↓
ProductMapper.xml
↓
MySQL product 表
```

### 创建订单

```text
Apifox
↓
OrderController
↓
OrderService
↓
查询商品
↓
扣减库存
↓
创建 order_info
↓
创建 order_item
↓
返回订单号
```

下单接口使用 `@Transactional`，保证扣库存、创建订单主表、创建订单明细要么都成功，要么都失败。

### 查询订单详情

```text
Apifox
↓
OrderController
↓
OrderService
↓
查询 order_info
↓
查询 order_item
↓
组装 OrderDetailVO
↓
返回订单主信息 + 明细列表
```

### 取消订单

```text
Apifox
↓
OrderController
↓
OrderService
↓
查询订单
↓
查询订单明细
↓
把 order_info.status 改为 2
↓
把订单明细中的商品库存加回去
↓
返回 true
```

取消订单接口也使用 `@Transactional`，保证修改订单状态和恢复库存要么都成功，要么都失败。

## 项目能力覆盖

| 能力点 | 当前体现 | 后续补充 |
| --- | --- | --- |
| MVC 分层 | Controller / Service / Mapper 分层清楚 | 继续保持新增模块同样分层 |
| 依赖注入 | 构造器注入 Service、Mapper | 用户模块继续使用构造器注入 |
| 参数校验 | DTO 中使用 `@NotBlank`、`@NotNull`、`@Min`，校验失败统一返回 `Result<T>` | 用户模块继续沿用参数校验 |
| 统一返回 | `Result<T>` 包装 `code / message / data` | 新接口继续沿用统一格式 |
| 全局异常处理 | `BusinessException` + `GlobalExceptionHandler` | 新异常继续统一转换成 `Result<T>` |
| 事务 | 创建订单、取消订单使用 `@Transactional` | 继续围绕库存一致性复盘 |
| 数据库 SQL | 商品表、用户表、订单主表、订单明细表 | 后续继续补缓存和消息相关能力 |
| 数据库索引 | `idx_product_name`、`uk_order_no`、`uk_username`、`idx_user_id` 等 | 后续按查询场景继续补充 |
| 缓存 | 暂未接入 | Redis 商品详情缓存 |
| 分布式锁 | 暂未接入 | Redis 库存锁学习版 |
| 拦截器 | 暂未接入 | 登录拦截器保护订单接口 |
| AOP | 暂未接入 | 请求日志和接口耗时统计 |
| 消息队列 | 暂未接入 | RocketMQ 订单消息学习版 |
| 接口测试 | Apifox 手工测试，MockMvc 自动化测试 | 后续补拦截器、缓存、消息测试 |
| Git | 按阶段 commit 并 push 到 GitHub | 封版前整理文档并提交 |

## 当前说明

当前商品接口、订单接口和健康检查接口都已统一返回 `Result<T>` 格式。

当前业务错误和请求体参数校验错误已通过 `GlobalExceptionHandler` 返回统一 JSON。后续会继续补充登录拦截器、AOP 日志、Redis 缓存和 RocketMQ 消息。

用户模块已完成注册、登录和查询当前用户接口。当前登录使用学习版 token，token 暂时保存在内存 `ConcurrentHashMap` 中，后续会结合 Redis 继续优化。

当前自动化测试已经覆盖：

- `/hello` 成功返回。
- `/product/list` 成功返回数组结构。
- `/product/999999` 商品不存在。
- `/product/notExist` 路径参数类型错误。
- `/user/register` 用户注册成功。
- `/user/register` 重复用户名返回 `4012 用户名已存在`。
- `/user/login` 登录成功返回 token。
- `/user/login` 密码错误返回 `4011 用户名或密码错误`。
- `/user/me` 带 token 查询当前用户成功，且不返回密码。
- `/user/me` 不带 token 返回 `4010 未登录`。
- `/order/create` 请求体参数校验错误。
- `/order/create` 商品不存在。
- `/order/create` 库存不足。
- `/order/create` 正常下单成功并返回订单号。
- `/order/OD_NOT_EXIST` 订单不存在。
- `/order/cancel/{orderNo}` 取消订单成功，订单状态变为已取消。
- 重复取消同一订单返回 `4004 订单已取消`，避免库存重复恢复。

## 后续规划

后续会围绕“项目完整性”和“能讲清楚链路”继续完善，不追求复杂业务堆叠。

- 登录拦截器：进入 Controller 前检查登录状态，订单接口需要登录。
- AOP 请求日志：记录接口路径、请求方式和耗时，不侵入业务代码。
- Redis 缓存和库存锁：商品详情加缓存，下单流程加入库存锁学习版。
- RocketMQ 订单消息：下单成功后发送订单消息，消费者接收并打印日志。
- 文档与复习封版：同步 README、接口文档、学习笔记，并准备项目讲解。
