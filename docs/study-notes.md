# 学习笔记

## 项目根目录

项目根目录应该是包含 `pom.xml` 的目录。打开项目、运行 Maven、提交 Git，都优先在这个目录下完成。

当前项目根目录：

```text
C:\Users\henry\Downloads\mall_order_demo
```

## Java 主包名

当前主包名是：

```text
com.henry.mallorder
```

`MallOrderApplication` 放在这个包下，Spring Boot 默认扫描它所在包以及子包里的组件。

## Java 基础概念

- `package`：声明类所在的包，相当于代码位置。
- `import`：引入其他类，方便在当前文件中使用。
- `class`：类，是创建对象的模板。
- `interface`：接口，只定义方法，具体实现可以交给框架或其他类。
- `private`：私有字段，外部不能直接访问。
- `getter/setter`：读取和设置私有字段的方法。
- `List<T>`：列表，表示多个同类型对象。
- `BigDecimal`：适合表示金额，避免 `double` 的精度问题。
- `LocalDateTime`：表示日期时间。

## 注解

`@` 开头的是注解。注解可以理解为给类、方法或字段贴标签，Spring 和 MyBatis 会读取这些标签并自动做事。

常见注解：

- `@SpringBootApplication`：项目启动类。
- `@RestController`：接口控制器。
- `@RequestMapping`：定义接口公共路径。
- `@GetMapping`：处理 GET 请求。
- `@PostMapping`：处理 POST 请求。
- `@PutMapping`：处理 PUT 请求。
- `@Service`：业务层类。
- `@Mapper`：MyBatis 数据库访问接口。
- `@Transactional`：事务控制。
- `@Valid`：触发参数校验。
- `@RequestBody`：把 JSON 请求体转换成 Java 对象。
- `@PathVariable`：读取 URL 路径里的参数。

## 后端分层

- Controller：接收 HTTP 请求。
- Service：处理业务逻辑。
- Mapper：访问数据库。
- Entity：对应数据库表。
- DTO：接收请求参数。
- VO：返回给前端的数据，目前还没正式使用。

请求大致流程：

```text
Apifox
↓
Controller
↓
Service
↓
Mapper
↓
MyBatis XML
↓
MySQL
```

## Java 17 和 Maven

项目使用 Java 17。运行 Maven 前要确认当前终端使用的是 JDK，不是 JRE：

```powershell
java -version
javac -version
```

`java` 能运行 Java，`javac` 能编译 Java。Maven 编译项目时需要 JDK。

如果当前终端不是 JDK 17，可以临时指定：

```powershell
$env:JAVA_HOME="C:\Users\henry\.jdks\ms-17.0.19"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

`pom.xml` 是 Maven 的依赖清单。修改 Spring Boot 或 MyBatis 版本后，第一次运行测试可能会重新下载依赖，这是正常现象。

## Spring Boot 版本

项目使用：

```text
Spring Boot 3.3.13
Java 17
MyBatis Spring Boot Starter 3.0.4
```

Spring Boot 3 使用 `jakarta.validation` 包名，例如：

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
```

测试中的 `AutoConfigureMockMvc` 包名是：

```java
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
```

## 本地配置和密码

公共配置写在：

```text
src/main/resources/application.yml
```

本地数据库密码写在：

```text
src/main/resources/application-local.yml
```

`application-local.yml` 被 `.gitignore` 忽略，不能提交到 GitHub。

`application.yml` 通过下面配置启用本地配置：

```yaml
spring:
  profiles:
    include: local
```

## 数据库表

当前数据库名：

```text
mall_order_demo
```

当前表：

- `product`：商品表。
- `order_info`：订单主表。
- `order_item`：订单明细表。

建表 SQL 统一保存在：

```text
sql/init.sql
```

金额字段使用 `DECIMAL`，Java 中使用 `BigDecimal`。

## 商品模块

商品模块路径：

```text
com.henry.mallorder.product
```

主要类：

- `Product`：商品 Entity，对应 `product` 表。
- `ProductCreateRequest`：新增商品请求 DTO。
- `ProductUpdateRequest`：修改商品请求 DTO。
- `ProductMapper`：商品数据库操作接口。
- `ProductMapper.xml`：商品 SQL。
- `ProductService`：商品业务逻辑。
- `ProductController`：商品接口。

已实现接口：

```text
POST /product/add
GET  /product/list
GET  /product/{id}
PUT  /product/{id}
```

新增商品流程：

```text
ProductController
↓
ProductService
↓
ProductMapper.insert
↓
ProductMapper.xml 的 INSERT SQL
↓
product 表
```

## 订单模块

订单模块路径：

```text
com.henry.mallorder.order
```

主要类：

- `OrderInfo`：订单主表 Entity，对应 `order_info`。
- `OrderItem`：订单明细表 Entity，对应 `order_item`。
- `CreateOrderRequest`：创建订单请求 DTO。
- `OrderMapper`：订单数据库操作接口。
- `OrderMapper.xml`：订单 SQL。
- `OrderService`：订单业务逻辑。
- `OrderController`：订单接口。

已实现接口：

```text
POST /order/create
GET  /order/{orderNo}
```

创建订单流程：

```text
接收 userId、productId、quantity
↓
查询商品
↓
计算总金额
↓
扣减库存
↓
生成订单号
↓
插入 order_info
↓
插入 order_item
↓
返回订单号
```

当前订单号生成方式：

```text
OD + 年月日时分秒毫秒
```

例如：

```text
OD20260610200621967
```

## Entity 和 DTO

Entity 对应数据库表。

例如：

```text
Product      -> product
OrderInfo    -> order_info
OrderItem    -> order_item
```

DTO 对应请求参数。

例如：

```text
ProductCreateRequest
ProductUpdateRequest
CreateOrderRequest
```

不要让前端传 `id`、`createTime`、`updateTime` 这种应该由系统或数据库控制的字段。

## 参数校验

DTO 中使用参数校验注解：

- `@NotBlank`：字符串不能为空，也不能全是空格。
- `@NotNull`：字段不能是 `null`。
- `@DecimalMin`：数字不能小于指定值。
- `@Min`：整数不能小于指定值。
- `@Max`：整数不能大于指定值。

Controller 参数上加：

```java
@Valid @RequestBody ProductCreateRequest request
```

表示请求体中的 JSON 会转换成 Java 对象，并触发参数校验。

当前还没有全局异常处理，校验失败时返回 Spring Boot 默认的 `400 Bad Request`。

## MyBatis Mapper 和 XML

Mapper 接口定义方法，XML 写真正的 SQL。

关键对应关系：

```text
namespace = Mapper 接口完整路径
id = Mapper 接口里的方法名
parameterType = SQL 接收的参数类型
resultType = 查询结果封装成的 Java 类型
```

例如 Java 方法：

```java
Product selectById(Long id);
```

对应 XML：

```xml
<select id="selectById" resultType="com.henry.mallorder.product.entity.Product">
```

`#{id}`、`#{productName}` 是 MyBatis 的安全传参方式，不是简单字符串拼接。

新增时：

```xml
useGeneratedKeys="true" keyProperty="id"
```

表示插入成功后，把数据库自动生成的自增 ID 回填到 Java 对象的 `id` 字段。

## 扣库存 SQL

下单时使用一条 SQL 安全扣库存：

```sql
UPDATE product
SET stock = stock - #{quantity}
WHERE id = #{productId}
  AND stock >= #{quantity}
  AND status = 1
```

它的作用：

```text
商品必须存在
商品必须上架
库存必须足够
扣库存和判断库存放在同一条 SQL 中
```

如果返回 `1`，表示扣库存成功。  
如果返回 `0`，表示库存不足、商品下架或商品不存在。

## 事务

创建订单方法上使用：

```java
@Transactional
```

它保证下单过程中的数据库操作要么都成功，要么都失败。

下单涉及：

```text
扣库存
插入订单主表
插入订单明细表
```

如果扣库存成功，但插入订单失败，事务会回滚，避免出现库存少了但订单不存在的问题。

## Apifox 接口测试

已通过 Apifox 测通：

```text
POST /product/add
GET  /product/list
GET  /product/{id}
PUT  /product/{id}
POST /order/create
GET  /order/{orderNo}
```

浏览器地址栏默认是 GET，不能用来测试 POST 新增或下单接口。

PowerShell 测中文 JSON 时可能出现乱码或问号。中文接口建议优先用 Apifox。

## 已验证场景

商品模块：

- 新增商品成功。
- 查询商品列表成功。
- 查询商品详情成功。
- 修改商品成功。
- 错误参数会返回 `400 Bad Request`。

订单模块：

- 正常下单成功。
- 下单后 `order_info` 有订单主表记录。
- 下单后 `order_item` 有订单明细记录。
- 下单后商品库存会减少。
- 库存不足时不会创建订单，库存不变。
- 商品不存在时不会创建订单。

## 后续计划

后续会继续补：

- 订单取消。
- 订单详情 VO，同时返回订单主表和明细。
- 统一返回结果 `Result<T>`。
- 全局异常处理 `GlobalExceptionHandler`。
- 登录拦截器。
- AOP 请求日志。
- Redis 商品缓存和库存锁。
- RocketMQ 订单消息。
