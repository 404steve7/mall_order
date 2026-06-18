# API 文档

## 基础说明

本项目当前接口使用 JSON 请求体。测试工具建议使用 Apifox。

本地启动地址：

```text
http://localhost:8080
```

当前接口已统一使用 `Result<T>` 返回对象。成功和业务失败都会返回 `code / message / data` 格式。

当前业务异常和请求体参数校验异常已通过 `GlobalExceptionHandler` 统一处理。

当前项目已接入 AOP 请求日志。它不会新增对外接口，而是在请求进入 Controller 方法前后记录请求方式、请求路径和接口耗时。示例日志：

```text
request method: GET, uri: /product/list, cost time: 604ms
```

当前通用错误码：

| code | message | 说明 |
| --- | --- | --- |
| 0 | success | 请求成功 |
| 4000 | 参数错误 | 路径参数类型错误，或请求体字段不符合校验规则 |
| 4001 | 商品不存在 | 商品 ID 在数据库中不存在 |
| 4002 | 库存不足或商品已下架 | 创建订单时库存不足或商品不可下单 |
| 4003 | 订单不存在 | 订单号不存在 |
| 4004 | 订单已取消 | 重复取消订单 |
| 4010 | 未登录 | 未携带 token 或 token 无效 |
| 4011 | 用户名或密码错误 | 登录失败 |
| 4012 | 用户名已存在 | 注册时用户名重复 |
| 5000 | 系统异常 | 未单独处理的系统错误 |

## 健康检查

### GET /hello

用途：确认 Spring Boot 项目已经启动。

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": "Hello Mall Order"
}
```

## 商品接口

### POST /product/add

用途：新增商品。

请求体：

```json
{
  "productName": "测试商品",
  "price": 99.90,
  "stock": 100
}
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": 4
}
```

参数说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| productName | string | 是 | 商品名称，不能为空 |
| price | number | 是 | 商品价格，必须大于 0 |
| stock | number | 是 | 商品库存，不能小于 0 |

### GET /product/list

用途：查询商品列表。

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 4,
      "productName": "修改后的商品00",
      "price": 88.80,
      "stock": 50,
      "status": 1,
      "createTime": "2026-06-09T23:16:57",
      "updateTime": "2026-06-11T23:44:05"
    }
  ]
}
```

说明：真正的商品列表放在 `data` 字段中。

### GET /product/{id}

用途：查询商品详情。

路径参数：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | number | 商品 ID |

示例：

```text
GET /product/4
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 4,
    "productName": "修改后的商品00",
    "price": 88.80,
    "stock": 50,
    "status": 1,
    "createTime": "2026-06-09T23:16:57",
    "updateTime": "2026-06-11T23:44:05"
  }
}
```

### PUT /product/{id}

用途：修改商品。

路径参数：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | number | 商品 ID |

请求体：

```json
{
  "productName": "修改后的商品",
  "price": 88.80,
  "stock": 50,
  "status": 1
}
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

参数说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| productName | string | 是 | 商品名称，不能为空 |
| price | number | 是 | 商品价格，必须大于 0 |
| stock | number | 是 | 商品库存，不能小于 0 |
| status | number | 是 | 商品状态：1-上架，0-下架 |

### 商品参数校验示例

错误请求：

```json
{
  "productName": "",
  "price": -1,
  "stock": -5
}
```

当前响应示例：

```json
{
  "code": 4000,
  "message": "商品名不能为空",
  "data": null
}
```

说明：请求体参数校验失败时，也会统一返回 `Result<T>` 格式。当前会返回第一个校验失败字段的提示。

### 商品路径参数错误示例

错误请求：

```text
GET /product/notExist
```

当前响应示例：

```json
{
  "code": 4000,
  "message": "参数错误",
  "data": null
}
```

说明：`GET /product/{id}` 中的 `id` 需要是数字，`notExist` 不能转换成 `Long`。

## 订单接口

订单接口当前已接入登录拦截器，请求 `/order/**` 时需要在 Header 中携带登录返回的 token：

```text
X-Token: 登录成功后生成的 token
```

未携带 token 或 token 无效时，会返回：

```json
{
  "code": 4010,
  "message": "未登录",
  "data": null
}
```

### POST /order/create

用途：创建订单。当前版本一次订单只购买一个商品。

请求体：

```json
{
  "userId": 1001,
  "productId": 4,
  "quantity": 5
}
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": "OD20260614162049773"
}
```

参数说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| userId | number | 是 | 用户 ID |
| productId | number | 是 | 商品 ID |
| quantity | number | 是 | 购买数量，必须大于 0 |

创建订单时会执行：

```text
查询商品
计算订单金额
扣减商品库存
插入订单主表 order_info
插入订单明细表 order_item
返回订单号
```

### GET /order/{orderNo}

用途：根据订单号查询订单详情，返回订单主信息和订单明细列表。

路径参数：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| orderNo | string | 订单号 |

示例：

```text
GET /order/OD20260610200621967
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderNo": "OD20260614162049773",
    "userId": 1001,
    "totalAmount": 88.80,
    "status": 1,
    "createTime": "2026-06-14T16:20:49",
    "updateTime": "2026-06-14T16:20:49",
    "items": [
      {
        "id": 3,
        "orderNo": "OD20260614162049773",
        "productId": 4,
        "productName": "修改后的商品00",
        "productPrice": 88.80,
        "quantity": 1,
        "totalAmount": 88.80,
        "createTime": "2026-06-14T16:20:49"
      }
    ]
  }
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| orderNo | string | 订单号 |
| userId | number | 用户 ID |
| totalAmount | number | 订单总金额 |
| status | number | 订单状态：1-已创建，2-已取消 |
| items | array | 订单明细列表 |

### POST /order/cancel/{orderNo}

用途：取消订单，并把订单中商品的库存恢复回去。

路径参数：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| orderNo | string | 订单号 |

示例：

```text
POST /order/cancel/OD20260610200621967
```

请求体：

```text
不需要 Body
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

取消订单时会执行：

```text
查询订单是否存在
判断订单是否已经取消
查询订单明细
把 order_info.status 改成 2
把商品库存加回去
返回 true
```

已验证结果：

```text
订单状态：1 -> 2
商品库存：45 -> 50
重复取消：返回错误，库存仍然是 50
```

### 库存不足示例

错误请求：

```json
{
  "userId": 1001,
  "productId": 4,
  "quantity": 999
}
```

当前响应示例：

```json
{
  "code": 4002,
  "message": "库存不足或商品已下架",
  "data": null
}
```

说明：不会创建订单，也不会扣减库存。

### 下单参数校验示例

错误请求：

```json
{
  "userId": 1001,
  "productId": 4,
  "quantity": 0
}
```

当前响应示例：

```json
{
  "code": 4000,
  "message": "购买数量必须大于0",
  "data": null
}
```

说明：`quantity` 必须大于等于 1。这个错误会在进入订单业务逻辑前被参数校验拦住。

### 商品不存在示例

错误请求：

```json
{
  "userId": 1001,
  "productId": 999999,
  "quantity": 1
}
```

当前响应示例：

```json
{
  "code": 4001,
  "message": "商品不存在",
  "data": null
}
```

说明：不会创建订单。

### 订单不存在示例

错误请求：

```text
GET /order/OD_NOT_EXISTS
```

当前响应示例：

```json
{
  "code": 4003,
  "message": "订单不存在",
  "data": null
}
```

### 重复取消订单示例

当订单已经是取消状态时，再次请求：

```text
POST /order/cancel/OD20260610200621967
```

当前响应示例：

```json
{
  "code": 4004,
  "message": "订单已取消",
  "data": null
}
```

说明：不会再次恢复库存。

## 用户接口

当前用户模块是学习版登录实现，用于理解注册、登录、token 和查询当前用户的基本链路。

当前 token 暂时保存在内存中，项目重启后会失效。后续会结合 Redis 继续优化。

### POST /user/register

用途：注册一个本地测试用户。

请求体：

```json
{
  "username": "henry",
  "password": "123456",
  "nickname": "Henry"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": 1
}
```

重复用户名响应示例：

```json
{
  "code": 4012,
  "message": "用户名已存在",
  "data": null
}
```

### POST /user/login

用途：用户登录，返回一个学习版 token。

请求体：

```json
{
  "username": "henry",
  "password": "123456"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": "登录成功后生成的 token"
}
```

登录失败响应示例：

```json
{
  "code": 4011,
  "message": "用户名或密码错误",
  "data": null
}
```

后续请求可以通过请求头传递 token：

```text
X-Token: 登录成功后生成的 token
```

### GET /user/me

用途：根据 token 查询当前登录用户信息。

请求头：

```text
X-Token: 登录成功后生成的 token
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "henry",
    "password": null,
    "nickname": "Henry",
    "status": 1,
    "createTime": "2026-06-18T11:55:52",
    "updateTime": "2026-06-18T11:55:52"
  }
}
```

未登录响应示例：

```json
{
  "code": 4010,
  "message": "未登录",
  "data": null
}
```

说明：返回当前用户信息前，后端会把 `password` 设置为 `null`，避免把密码返回给前端。

## 后续内部能力

Redis 和 RocketMQ 不一定表现为新的对外接口，它们更多是在业务内部生效。

- Redis 商品详情缓存：查询商品详情时优先读取缓存，缓存没有再查 MySQL。
- Redis 库存锁学习版：创建订单时围绕商品库存加锁，理解并发控制思路。
- RocketMQ 订单消息：创建订单成功后发送消息，消费者接收消息并打印日志。
