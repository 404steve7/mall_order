# API 文档

## 基础说明

本项目当前接口使用 JSON 请求体。测试工具建议使用 Apifox。

本地启动地址：

```text
http://localhost:8080
```

当前正在逐步引入统一返回对象 `Result<T>`。已经完成统一返回的接口会返回 `code / message / data` 格式。

当前还没有全局异常处理。参数错误或业务异常可能返回 Spring Boot 默认错误格式，后续会统一优化。

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

```text
4
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

说明：当前 `GET /product/list` 已改为统一返回格式，真正的商品列表放在 `data` 字段中。

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
  "id": 4,
  "productName": "修改后的商品",
  "price": 88.80,
  "stock": 45,
  "status": 1,
  "createTime": "2026-06-09T23:16:57",
  "updateTime": "2026-06-10T20:06:21"
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

```text
true
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
  "timestamp": "2026-06-09T15:20:28.289+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/product/add"
}
```

## 订单接口

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

```text
OD20260610200621967
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
  "orderNo": "OD20260610200621967",
  "userId": 1001,
  "totalAmount": 444.00,
  "status": 2,
  "createTime": "2026-06-10T20:06:21",
  "updateTime": "2026-06-11T23:44:05",
  "items": [
    {
      "id": 1,
      "orderNo": "OD20260610200621967",
      "productId": 4,
      "productName": "修改后的商品00",
      "productPrice": 88.80,
      "quantity": 5,
      "totalAmount": 444.00,
      "createTime": "2026-06-10T20:06:21"
    }
  ]
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

```text
true
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

当前行为：

```text
不会创建订单
不会扣减库存
可能返回 Spring Boot 默认 500 错误
```

说明：后续会通过全局异常处理把这类错误改成更清楚的业务错误返回。

### 商品不存在示例

错误请求：

```json
{
  "userId": 1001,
  "productId": 999999,
  "quantity": 1
}
```

当前行为：

```text
不会创建订单
可能返回 Spring Boot 默认 500 错误
```

### 重复取消订单示例

当订单已经是取消状态时，再次请求：

```text
POST /order/cancel/OD20260610200621967
```

当前行为：

```text
不会再次恢复库存
可能返回 Spring Boot 默认 500 错误
```

说明：当前业务逻辑已经能阻止重复取消，但错误格式还没有统一。后续会用全局异常处理把它改成更清晰的业务错误返回。
