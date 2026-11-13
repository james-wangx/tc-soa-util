# tc-soa-util 中文文档

英文主文档：[README.md](README.md)

源码仓库：[https://github.com/james-wangx/tc-soa-util](https://github.com/james-wangx/tc-soa-util)

Teamcenter SOA Util

Teamcenter 版本：2512

Maven 制品版本：2512.2611.03

## 服务账号全局会话

`AppXSharedSession` 持有一个进程级 Teamcenter Connection。应用启动时使用服务
账号初始化，并通过 `AppXSharedSession.execute(...)` 执行共享操作；由于
Teamcenter Connection 内含可变的客户端状态，所有调用必须串行执行。

共享会话只用于不需要模拟目标用户身份的操作。签发用户登录 Token 等操作仍需
独立用户会话；为某个用户创建对象后，必须完成所有权转移再返回。

## 依赖打包

业务工程只需要引入 `tc-soa-util` 一个依赖。工具库制品内部已经打包
Java EE JAXB 2.3 运行库，Log4j Provider 使用消费工程 Log4j2 运行时自带的
标准 Core Provider。
默认 SOA 异常处理器还会记录完整异常栈，便于业务服务返回经过脱敏的具体根因。
工具库不携带会被自动发现的 `log4j2.properties`，Log4j 配置和 Appender 由
消费工程统一维护。

### Log4j 兼容性

| 运行环境 | Log4j API / Core |
| --- | --- |
| Teamcenter 2512 `soa_client/java/libs` | `2.17.1` |
| 配合 Spring Boot 4 的 `tc-soa-util` | `2.25.5` |

Teamcenter 独立 SOA 介质使用 `2.17.1`，但这套完整 Log4j 与 Spring Boot 4
共享的 Log4j API 不兼容。`log4j-core 2.17.1` 搭配 API `2.25.5` 会报
`NoSuchMethodError: LoaderUtil.getClassLoaders()`。因此 util 在 Spring Boot
进程内使用兼容的 `2.25.5` API/Core 和 Log4j 自带的标准 Core Provider。消费方可通过
`mvn dependency:tree` 查看实际版本，不应降级 Log4j。若必须使用完整 `2.17.1`，
需要将 Teamcenter 客户端放入独立进程或独立类加载器运行。

## Security Token 集成测试

`SecurityTokenIntegrationTest` 用于验证 Teamcenter 的会话交接流程：

1. 使用 Teamcenter 用户名和密码登录。
2. 通过 `TcUtil#getSecurityToken(int)` 获取短时 Security Token。
3. 立即注销密码认证会话。
4. 使用同一用户名和 Security Token 作为密码登录。
5. 注销 Token 认证会话。

设置 `TC_URL`、`TC_USERNAME`、`TC_PASSWORD`，可选设置 `TC_LOCALE`，对目标
Teamcenter 环境运行集成测试：

```powershell
mvn -Dtest=SecurityTokenIntegrationTest test
```

`SharedSessionPasswordChangeIntegrationTest` 用于验证服务账号共享会话可以在
两次操作之间不重新登录的情况下修改并恢复测试用户密码。除 `TC_URL` 外，还需
设置 `TC_SERVICE_USERNAME`、`TC_SERVICE_PASSWORD`、`TC_TARGET_USERNAME`
和 `TC_TARGET_PASSWORD`。
