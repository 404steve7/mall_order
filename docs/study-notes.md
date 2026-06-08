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
