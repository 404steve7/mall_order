# API 文档

## 健康检查

### GET /hello

用途：确认 Spring Boot 项目已经启动。

响应示例：

```text
Hello Mall Order
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
[
  {
    "id": 4,
    "productName": "ssffe",
    "price": 99.90,
    "stock": 100,
    "status": 1,
    "createTime": "2026-06-09T23:16:57",
    "updateTime": "2026-06-09T23:16:57"
  }
]
```

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
  "productName": "ssffe",
  "price": 99.90,
  "stock": 100,
  "status": 1,
  "createTime": "2026-06-09T23:16:57",
  "updateTime": "2026-06-09T23:16:57"
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

### 参数校验示例

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

说明：后续会通过全局异常处理优化错误返回格式。
