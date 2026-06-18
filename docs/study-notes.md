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
- `Result<T>`：统一返回对象，`T` 表示 `data` 中真正数据的类型。
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
- `@RestControllerAdvice`：全局处理 Controller 抛出的异常。
- `@ExceptionHandler`：指定某种异常应该由哪个方法处理。
- `@Aspect`：声明一个 AOP 切面类，用来统一增强某一类方法。
- `@Around`：AOP 环绕通知，可以在目标方法执行前后都加入逻辑。
- `@Component`：把普通 Java 类交给 Spring 管理，这样 Spring 才能发现并使用它。

## 后端分层

- Controller：接收 HTTP 请求。
- Service：处理业务逻辑。
- Mapper：访问数据库。
- Entity：对应数据库表。
- DTO：接收请求参数。
- VO：返回给前端的数据，可以把多张表的数据组合成一个返回结果。
- Common：通用代码，目前放了统一返回对象 `Result<T>`。

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
- `user_info`：用户表。
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

当前商品接口已经改成统一返回。例如 `GET /product/list`：

```java
public Result<List<Product>> listProducts() {
    return Result.success(productService.listProducts());
}
```

返回结果从原来的商品数组，变成：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 4,
      "productName": "修改后的商品00"
    }
  ]
}
```

真正的数据仍然是商品列表，只是被放进了 `data` 字段里。

## 统一返回 Result

真实项目里，不同接口最好有统一返回格式。

如果没有统一返回，接口可能是这样：

```text
新增商品返回 Long
商品列表返回 List<Product>
创建订单返回 String
取消订单返回 Boolean
失败时返回 Spring Boot 默认错误
```

这样前端或测试工具每个接口都要单独判断，不够规范。

所以项目开始引入：

```java
Result<T>
```

统一格式是：

```json
{
  "code": 0,
  "message": "success",
  "data": "真正的数据"
}
```

字段含义：

```text
code：业务状态码，0 表示成功
message：提示信息
data：真正返回的数据
```

`T` 是泛型，可以理解为“先占个位，具体类型以后再决定”。

例如：

```text
Result<String>        data 是字符串
Result<Long>          data 是商品 ID
Result<Boolean>       data 是 true/false
Result<List<Product>> data 是商品列表
```

当前已完成：

```text
GET /hello
POST /product/add
GET /product/list
GET /product/{id}
PUT /product/{id}
POST /order/create
GET /order/{orderNo}
POST /order/cancel/{orderNo}
```

也就是说，成功接口现在都会返回 `Result<T>`。

## 全局异常处理

最开始业务失败时，代码直接抛：

```java
throw new RuntimeException("订单已取消");
```

这种写法有两个问题：

```text
1. RuntimeException 太宽泛，不知道是业务失败还是系统错误。
2. Spring Boot 默认会返回 500，看不出真正原因。
```

所以项目新增了：

```text
BusinessException
GlobalExceptionHandler
```

`BusinessException` 专门表示业务失败：

```java
throw new BusinessException(4004, "订单已取消");
```

它包含：

```text
code：业务错误码
message：错误提示
```

`GlobalExceptionHandler` 会统一接住异常：

```java
@ExceptionHandler(BusinessException.class)
public Result<Void> handleBusinessException(BusinessException e) {
    return Result.fail(e.getCode(), e.getMessage());
}
```

所以业务失败会返回：

```json
{
  "code": 4004,
  "message": "订单已取消",
  "data": null
}
```

如果是未知系统异常，会返回：

```json
{
  "code": 5000,
  "message": "系统异常",
  "data": null
}
```

这样做的好处：

```text
成功返回统一
业务失败返回统一
系统异常不会暴露内部细节
Apifox 和前端更容易判断结果
```

当前业务错误码：

```text
4000 参数错误
4001 商品不存在
4002 库存不足或商品已下架
4003 订单不存在
4004 订单已取消
4005 订单取消失败
4006 恢复库存失败
5000 系统异常
```

`4000 参数错误` 当前主要处理路径参数类型错误和请求体参数校验错误。

例如接口需要：

```text
GET /product/{id}
```

其中 `id` 是 `Long`。如果请求：

```text
GET /product/notExist
```

Spring 会在进入 Service 前就发现 `notExist` 不能转换成 Long，于是抛出 `MethodArgumentTypeMismatchException`。项目现在会把它统一返回成：

```json
{
  "code": 4000,
  "message": "参数错误",
  "data": null
}
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
- `OrderDetailVO`：订单详情返回 VO，包含订单主信息和订单明细列表。
- `OrderMapper`：订单数据库操作接口。
- `OrderMapper.xml`：订单 SQL。
- `OrderService`：订单业务逻辑。
- `OrderController`：订单接口。

已实现接口：

```text
POST /order/create
GET  /order/{orderNo}
POST /order/cancel/{orderNo}
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

## 订单详情 VO

最开始查询订单时，只返回了 `order_info` 订单主表。

但是一个订单通常不只需要主表信息，还要知道这个订单买了哪些商品，所以后来加了：

```text
OrderDetailVO
```

它不是数据库表，而是专门返回给前端的数据结构。

当前 `OrderDetailVO` 包含：

```text
orderNo
userId
totalAmount
status
createTime
updateTime
items
```

其中 `items` 是 `List<OrderItem>`，表示订单明细列表。

查询订单详情的流程：

```text
GET /order/{orderNo}
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
返回给 Apifox
```

小白理解：

```text
Entity 更像数据库表的一行数据。
DTO 更像前端传进来的请求参数。
VO 更像后端整理好后返回给前端看的结果。
```

## 取消订单

取消订单不是简单地把订单删掉。

真实业务里订单一般不会物理删除，而是修改状态：

```text
1 = 已创建
2 = 已取消
```

取消订单时要做两件关键事：

```text
1. 把 order_info.status 从 1 改成 2
2. 把这个订单买走的商品库存加回去
```

取消订单流程：

```text
POST /order/cancel/{orderNo}
↓
查询订单是否存在
↓
判断订单是否已经取消
↓
查询订单明细
↓
更新订单状态为 2
↓
遍历订单明细，把每个商品库存加回去
↓
返回 true
```

为什么不能重复取消：

```text
如果第一次取消已经把库存 45 加回 50，
第二次取消又加一次，库存就会变成 55。
这就错了。
```

所以代码里要判断：

```java
if (Integer.valueOf(2).equals(orderInfo.getStatus())) {
    throw new BusinessException(4004, "订单已取消");
}
```

同时 SQL 里也限制只更新未取消订单：

```sql
UPDATE order_info
SET status = #{status}
WHERE order_no = #{orderNo}
  AND status = 1
```

这样可以减少重复取消导致库存多加的风险。

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
UserRegisterRequest
UserLoginRequest
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

当前已经有业务异常和参数校验异常的全局处理。请求体校验失败时，也会返回统一的 `Result<T>` 格式。

例如下单时：

```json
{
  "userId": 1001,
  "productId": 4,
  "quantity": 0
}
```

因为 `quantity` 上有：

```java
@Min(value = 1, message = "购买数量必须大于0")
```

所以会返回：

```json
{
  "code": 4000,
  "message": "购买数量必须大于0",
  "data": null
}
```

这里的关键链路是：

```text
@RequestBody 把 JSON 转成 Java 对象
@Valid 触发 DTO 参数校验
校验失败抛出 MethodArgumentNotValidException
GlobalExceptionHandler 接住异常
返回统一 Result JSON
```

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

## SQL 和索引

后端项目里经常会用到基本 SQL 和索引。当前项目已经用到了：

```text
SELECT 查询
INSERT 新增
UPDATE 修改
WHERE 条件
ORDER BY 排序
INDEX 普通索引
UNIQUE KEY 唯一索引
```

当前建表 SQL 里的索引：

```sql
INDEX idx_product_name (product_name)
UNIQUE KEY uk_order_no (order_no)
KEY idx_user_id (user_id)
KEY idx_order_no (order_no)
KEY idx_product_id (product_id)
```

索引可以理解成数据库的目录。

如果没有索引，数据库可能要从第一行扫到最后一行。  
有了合适索引，数据库可以更快定位数据。

当前项目里几个索引的目的：

```text
idx_product_name：以后按商品名搜索时更快
uk_order_no：保证订单号唯一，并且按订单号查询更快
idx_user_id：以后按用户查询订单时更快
idx_order_no：按订单号查询订单明细更快
idx_product_id：以后按商品查询订单明细更快
```

避坑点：

```text
索引不是越多越好。
索引会加快查询，但会增加写入和维护成本。
真实项目要根据查询场景设计索引。
```

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

取消订单也使用：

```java
@Transactional
```

取消订单涉及：

```text
修改订单状态
恢复商品库存
```

如果订单状态改成功了，但恢复库存失败，事务会回滚，避免出现订单显示已取消但库存没有加回来的问题。

数据库事务可以简单理解成：

```text
一组数据库操作，要么全部成功，要么全部失败。
```

它有四个常见特性，简称 ACID：

```text
A 原子性：要么全做，要么全不做
C 一致性：操作前后数据规则不能被破坏
I 隔离性：多个事务之间尽量互不干扰
D 持久性：提交后数据真正保存下来
```

在当前项目里，事务最重要的是保护订单和库存一致。

## Apifox 接口测试

已通过 Apifox 测通：

```text
POST /product/add
GET  /product/list
GET  /product/{id}
PUT  /product/{id}
POST /order/create
GET  /order/{orderNo}
POST /order/cancel/{orderNo}
```

浏览器地址栏默认是 GET，不能用来测试 POST 新增或下单接口。

PowerShell 测中文 JSON 时可能出现乱码或问号。中文接口建议优先用 Apifox。

## MockMvc JSON 测试

以前 `/hello` 返回纯文本：

```text
Hello Mall Order
```

所以测试可以写：

```java
content().string("Hello Mall Order")
```

现在 `/hello` 返回 JSON：

```json
{
  "code": 0,
  "message": "success",
  "data": "Hello Mall Order"
}
```

所以测试要改成 `jsonPath`：

```java
jsonPath("$.code").value(0)
jsonPath("$.message").value("success")
jsonPath("$.data").value("Hello Mall Order")
```

其中 `$` 表示 JSON 根对象，`$.data` 表示读取根对象里的 `data` 字段。

统一返回以后，测试时通常按这个思路判断字段：

```text
code    判断这次请求整体成功还是失败
message 判断失败原因或提示语是否正确
data    判断真正的业务结果
```

常见例子：

```java
jsonPath("$.code").value(0)
jsonPath("$.data").isString()
jsonPath("$.data.status").value(2)
jsonPath("$.message").value("订单已取消")
```

`$.data` 表示整个业务数据。  
如果 `data` 是订单详情对象，就要继续往里面取字段，例如 `$.data.status`。  
如果失败时 `data` 是 `null`，可以用：

```java
jsonPath("$.data").isEmpty()
```

避坑点：

```text
不要把错误提示放到 $.data 里判断。
业务错误提示在 message 里。
真正的业务结果才在 data 里。
```

## 当前自动化测试

当前测试文件：

```text
src/test/java/com/henry/mallorder/MallOrderApplicationTests.java
```

已经覆盖：

```text
项目能启动
/hello 成功返回
/product/list 返回 Result 且 data 是数组
/product/999999 返回商品不存在
/product/notExist 返回参数错误
/order/create 商品不存在
/order/create 库存不足
/order/create 正常下单成功并返回订单号
/order/create 参数校验失败
/order/OD_NOT_EXIST 订单不存在
/order/cancel/{orderNo} 取消订单成功
重复取消同一订单返回订单已取消
/user/register 用户注册成功
/user/register 重复用户名返回用户名已存在
/user/login 登录成功返回 token
/user/login 密码错误返回用户名或密码错误
/user/me 带 token 查询当前用户成功
/user/me 不带 token 返回未登录
/order/create 不带 token 返回未登录
```

现在一共有 18 个自动化测试。测试覆盖了统一返回、业务失败、参数错误、用户注册登录、登录拦截器、成功下单、取消订单和重复取消订单。

为什么成功下单测试要加 `@Transactional`：

```text
创建订单成功会真实写数据库
会扣商品库存
会插入 order_info
会插入 order_item
如果不处理，每跑一次测试，数据库数据都会变化
```

所以成功下单和取消订单测试使用测试事务回滚：

```java
@Test
@Transactional
void createOrderReturnsOrderNoWhenProductExists() {
    // 测试结束后，本次测试插入的数据会回滚
}
```

这样做的好处：

```text
可以测试真实业务链路
不会污染本地 MySQL 数据
不用手动删除测试产生的商品、订单和订单明细
每次测试之间更独立
```

取消订单测试的思路：

```text
1. 先临时创建一个商品
2. 调用 /order/create 创建订单
3. 从返回 JSON 的 data 中取出订单号
4. 调用 /order/cancel/{orderNo} 取消订单
5. 再查订单详情，确认 status 已经变成 2
```

重复取消测试的思路：

```text
1. 创建订单
2. 第一次取消，应该成功
3. 第二次取消同一个订单，应该返回 4004 订单已取消
4. 这样可以保护库存，避免重复取消导致库存越加越多
```

## 已验证场景

商品模块：

- 新增商品成功。
- 查询商品列表成功，并已改成 `Result<List<Product>>` 统一返回。
- 查询商品详情成功。
- 修改商品成功。
- 路径参数类型错误会返回 `code = 4000`。
- 请求体参数校验异常会返回 `code = 4000`。

订单模块：

- 正常下单成功。
- 下单后 `order_info` 有订单主表记录。
- 下单后 `order_item` 有订单明细记录。
- 下单后商品库存会减少。
- 库存不足时不会创建订单，库存不变。
- 商品不存在时不会创建订单。
- 查询订单详情时，会返回订单主信息和 `items` 明细列表。
- 取消订单后，订单状态会从 `1` 变成 `2`。
- 取消订单后，商品库存会恢复。
- 重复取消订单时会报错，库存不会重复增加。
- 商品不存在会返回 `code = 4001`。
- 库存不足会返回 `code = 4002`。
- 订单不存在会返回 `code = 4003`。
- 重复取消会返回 `code = 4004`。

## 后续学习路线

接下来不是为了把项目做得很复杂，而是把一个 Spring Boot 后端项目常见的关键能力补完整，并且能讲清楚每个能力解决什么问题。

### 参数校验异常统一返回

现在业务异常和参数校验异常都已经可以返回统一格式。例如业务异常：

```json
{
  "code": 4001,
  "message": "商品不存在",
  "data": null
}
```

参数校验异常也会返回类似格式。例如 `quantity = 0` 会返回：

```json
{
  "code": 4000,
  "message": "购买数量必须大于0",
  "data": null
}
```

这样前端或 Apifox 不需要一会儿看 Spring Boot 默认错误，一会儿看 `Result<T>`。

### 用户登录

用户模块当前已经完成：

```text
user_info 表
User Entity
UserRegisterRequest
UserLoginRequest
UserMapper
UserMapper.xml
UserService
UserController
```

已实现接口：

```text
POST /user/register
POST /user/login
GET  /user/me
```

这个模块的目标不是做复杂权限系统，而是理解登录态：

```text
用户提交账号密码
后端校验成功
后端生成 token
前端后续请求带上 token
后端根据 token 判断是谁在访问
```

查询当前用户时，请求头使用：

```text
X-Token: 登录成功后生成的 token
```

如果没有传 token，或者 token 无效，会返回：

```json
{
  "code": 4010,
  "message": "未登录",
  "data": null
}
```

当前 `UserService` 里先使用 `ConcurrentHashMap<String, Long>` 保存 token 和用户 ID 的关系：

```text
token -> userId
```

这属于学习版方案。它能帮助理解登录态，但也有明显限制：

```text
项目重启后 token 会丢
多台服务器之间不能共享
真实项目通常会使用 Redis、JWT 或更完整的认证方案
```

当前密码也是学习版明文保存，后续讲项目时要明确说明：真实项目必须做密码加密，不能明文保存。

查询当前用户时，返回前会执行：

```java
user.setPassword(null);
```

这样做是为了避免把密码返回给前端。哪怕当前只是学习版，也要养成“敏感字段不返回”的习惯。

### 拦截器

拦截器可以理解成“请求进入 Controller 之前的一道门”。

当前订单接口已经要求登录：

```text
请求进入后端
↓
拦截器检查 X-Token
↓
token 有效，放行到 Controller
↓
token 无效，直接返回未登录
```

商品查询可以先放行，因为很多系统里商品浏览不一定需要登录；创建订单、取消订单这种会改变业务数据的接口更适合要求登录。

当前拦截范围是：

```text
/order/**
```

也就是说，下面这些接口都需要 `X-Token`：

```text
POST /order/create
GET  /order/{orderNo}
POST /order/cancel/{orderNo}
```

测试里如果要访问订单接口，也必须先注册登录拿 token：

```text
registerAndLogin()
↓
请求 /order/** 时加 .header("X-Token", token)
```

如果测试不带 token，就会先被拦截器挡住，返回 `4010 未登录`，不会进入 Controller，也不会走参数校验或业务逻辑。

### AOP 请求日志

AOP 可以理解成“不改业务代码，也能在业务方法前后加一些统一动作”。

当前项目已经新增：

```text
RequestLogAspect
```

它负责记录：

```text
请求路径
请求方式
接口耗时
```

例如请求商品列表后，控制台可以看到类似日志：

```text
request method: GET, uri: /product/list, cost time: 604ms
```

这说明请求已经经过了 AOP 切面。

当前切点大致表示：

```text
拦截 com.henry.mallorder 包下面 controller 相关方法
```

也就是说，Controller 里的接口方法执行时，会先进入 AOP 的环绕逻辑。AOP 里调用 `joinPoint.proceed()`，才会继续执行原来的 Controller 方法。

它和拦截器的区别：

```text
拦截器更靠近 HTTP 请求入口，适合做登录检查。
AOP 更靠近方法执行过程，适合做日志、耗时统计、事务这类增强能力。
```

为什么不直接在每个 Controller 方法里手写日志：

```text
如果每个接口都手写日志，代码会重复很多。
以后要调整日志格式，需要改很多地方。
AOP 可以把“记录日志”这种公共动作集中到一个类里，业务代码保持干净。
```

避坑点：

```text
不要记录密码、token、完整请求体这类敏感信息。
日志是辅助排查问题的，不应该泄露隐私或安全信息。
```

### Redis 缓存和锁

Redis 第一阶段会用于商品详情缓存：

```text
第一次查商品：MySQL -> 写入 Redis
第二次查商品：优先从 Redis 返回
修改商品：删除对应缓存
```

这样做是为了理解缓存如何减少数据库压力。

Redis 第二阶段会用于库存锁学习版：

```text
创建订单前尝试获取锁
拿到锁再扣库存
业务结束释放锁
```

这只是学习版，重点是理解 `SET NX EX` 的思想。真实项目里更推荐使用成熟方案，例如 Redisson。

### RocketMQ 订单消息

RocketMQ 会用于下单后的异步消息：

```text
创建订单成功
↓
发送订单消息
↓
消费者收到消息
↓
打印订单日志
```

这个功能的重点是理解：

```text
Producer：消息生产者
Consumer：消息消费者
Topic：消息主题
Message：消息内容
```

## 项目推进计划

### 6 月 17 日：参数校验和用户模块

- 已统一请求体校验异常返回。
- 已新增用户表。
- 已完成用户 Entity、DTO、Mapper、XML、Service。
- 已完成注册、登录、查询当前用户 Controller。
- 已补用户模块基础测试。

### 6 月 18 日：登录拦截器和 AOP

- 已新增登录拦截器。
- 订单接口已要求携带 token。
- 未登录已返回统一错误。
- 新增 AOP 请求日志。
- 已验证请求 `/product/list` 时控制台会打印请求方法、路径和耗时。

### 6 月 19 日：Redis

- 启动 Docker Desktop 和 Redis。
- 商品详情接入缓存。
- 修改商品后删除缓存。
- 下单流程加入库存锁学习版。

### 6 月 20 日：RocketMQ 和功能封版

- 启动 RocketMQ。
- 下单成功后发送订单消息。
- 消费者接收消息并打印日志。
- 回归测试、整理文档、提交代码。

### 6 月 21-24 日：复习和讲解准备

- 复盘 Controller -> Service -> Mapper -> XML -> MySQL 链路。
- 复盘 DTO / Entity / VO / Result / Exception。
- 复盘事务、拦截器、AOP、Redis、RocketMQ。
- 准备 3-5 分钟项目讲解。
- 不再大幅新增功能，主要修 bug、熟悉系统、补文档。
