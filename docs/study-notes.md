# 学习笔记

## 项目根目录

项目根目录应该是包含 `pom.xml` 的目录。以后打开项目、运行 Maven、提交 Git，都优先在这个目录下完成。

## Java 主包名

当前主包名是：

```text
com.henry.mallorder
```

`MallOrderApplication` 放在这个包下，Spring Boot 会默认扫描它所在包以及子包里的组件。

## 后端分层

- Controller：接收 HTTP 请求。
- Service：处理业务逻辑。
- Mapper：访问数据库。
- Entity：通常对应数据库表。
- DTO：通常对应请求参数。
- VO：通常对应返回给前端的数据。

## Java 17 和 Maven

项目使用 Java 17。运行 Maven 前要确认当前终端使用的是 JDK，不是 JRE：

```powershell
java -version
javac -version
```

如果 `java` 是 17，但没有 `javac`，说明当前环境不能编译 Java。可以在当前 PowerShell 里临时指定 JDK 17：

```powershell
$env:JAVA_HOME="C:\Users\henry\.jdks\ms-17.0.19"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

`pom.xml` 是 Maven 的依赖清单。修改 Spring Boot 或 MyBatis 版本后，第一次运行测试可能会重新下载依赖，这是正常现象。

## Spring Boot 版本调整

项目从 Spring Boot 4 调整到 Spring Boot 3.3.13，主要是为了和当前学习项目、常见企业项目习惯保持一致。

调整后常用依赖是：

```text
spring-boot-starter-web
spring-boot-starter-validation
spring-boot-starter-test
mybatis-spring-boot-starter 3.0.4
```

Spring Boot 3 使用 `jakarta.validation` 包名，例如：

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
```

测试里的 `AutoConfigureMockMvc` 包名是：

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

`application-local.yml` 已经被 `.gitignore` 忽略，不能提交到 GitHub。这样可以避免把本地数据库密码上传出去。

`application.yml` 中通过下面配置启用本地配置：

```yaml
spring:
  profiles:
    include: local
```

## MySQL 商品表

当前数据库名是：

```text
mall_order_demo
```

商品表是：

```text
product
```

主要字段：

- `id`：商品 ID，自增主键。
- `product_name`：商品名称。
- `price`：商品价格，使用 `DECIMAL`，不要用 `double` 存金额。
- `stock`：库存数量。
- `status`：商品状态，`1` 表示上架，`0` 表示下架。
- `create_time`：创建时间。
- `update_time`：更新时间。

建表语句统一保存在：

```text
sql/init.sql
```

## 商品模块分层

当前商品模块路径：

```text
com.henry.mallorder.product
```

分层如下：

- `controller`：提供 HTTP 接口，例如新增商品、查询商品。
- `service`：处理业务逻辑，例如新增商品默认上架。
- `mapper`：定义数据库操作方法。
- `entity`：对应数据库表，例如 `Product`。
- `dto`：接收请求参数，例如 `ProductCreateRequest`、`ProductUpdateRequest`。

一次新增商品请求的大概流程：

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

## Entity 和 DTO

`Product` 是 Entity，对应数据库里的 `product` 表。

一个 `Product` 对象通常对应数据库表里的一行数据。

`ProductCreateRequest` 是新增商品请求参数，只包含新增时前端需要传的字段：

```text
productName
price
stock
```

`ProductUpdateRequest` 是修改商品请求参数，包含：

```text
productName
price
stock
status
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

表示请求体中的 JSON 会被转换成 Java 对象，并触发参数校验。

当前还没有全局异常处理，所以校验失败时返回的是 Spring Boot 默认的 `400 Bad Request`。后续会通过 `GlobalExceptionHandler` 优化错误提示。

## MyBatis Mapper 和 XML

`ProductMapper.java` 定义数据库操作方法：

```java
int insert(Product product);
List<Product> selectList();
Product selectById(Long id);
int updateById(Product product);
```

`ProductMapper.xml` 写真正的 SQL。

关键对应关系：

```text
namespace = Mapper 接口完整路径
id = Mapper 接口里的方法名
parameterType = SQL 接收的参数类型
resultType = 查询结果封装成的 Java 类型
```

例如：

```xml
<select id="selectById" resultType="com.henry.mallorder.product.entity.Product">
```

对应 Java 方法：

```java
Product selectById(Long id);
```

`#{id}`、`#{productName}` 是 MyBatis 的安全传参方式，不是简单字符串拼接。

新增商品 SQL 中：

```xml
useGeneratedKeys="true" keyProperty="id"
```

表示插入成功后，把数据库自动生成的自增 ID 回填到 `Product.id` 字段。

## 商品接口测试

当前已通过 Apifox 测通：

```text
POST /product/add
GET  /product/list
GET  /product/{id}
PUT  /product/{id}
```

PowerShell 测中文 JSON 时可能出现乱码或问号。中文接口建议优先用 Apifox 测试。

参数校验也已测试：

```json
{
  "productName": "",
  "price": -1,
  "stock": -5
}
```

当前会返回 `400 Bad Request`，说明校验已经生效。
