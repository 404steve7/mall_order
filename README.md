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
- 创建订单：`POST /order/create`
- 查询订单详情：`GET /order/{orderNo}`
- 取消订单：`POST /order/cancel/{orderNo}`
- 下单时扣减商品库存
- 取消订单时恢复商品库存
- 下单和取消订单接口使用 `@Transactional` 保证事务一致性
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

## 知识点对应

| 知识点 | 项目体现 |
| --- | --- |
| MVC 分层 | Controller / Service / Mapper |
| 依赖注入 | 构造器注入 Service、Mapper |
| MySQL | 商品表、订单主表、订单明细表 |
| MyBatis | Mapper 接口 + XML SQL |
| 参数校验 | DTO 中使用 `@NotBlank`、`@NotNull`、`@Min` |
| VO 返回对象 | `OrderDetailVO` 返回订单主信息和明细列表 |
| 事务 | 创建订单、取消订单接口使用 `@Transactional` |
| SQL 安全扣库存 | `UPDATE product SET stock = stock - quantity WHERE stock >= quantity` |
| 库存恢复 | 取消订单时 `UPDATE product SET stock = stock + quantity` |
| 接口测试 | 使用 Apifox 测商品和订单接口 |
| Git | 按阶段 commit 并 push 到 GitHub |

## 当前说明

当前错误返回仍使用 Spring Boot 默认格式。后续会补充统一返回 `Result<T>`、全局异常处理、登录拦截器、AOP 日志、Redis 缓存和 RocketMQ 消息。
