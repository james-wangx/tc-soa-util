# tc-soa-util 中文文档

英文主文档：[README.md](README.md)

Teamcenter SOA Util

Teamcenter 版本：2512

Maven 制品版本：2512.2611.01

## 依赖打包

业务工程只需要引入 `tc-soa-util` 一个依赖。工具库制品内部已经打包
Java EE JAXB 2.3 运行库，并注册 Teamcenter SOA 客户端所需的 Log4j Provider。
默认 SOA 异常处理器还会记录完整异常栈，便于业务服务返回经过脱敏的具体根因。

### Log4j 兼容性

| 运行环境 | Log4j API / Core |
| --- | --- |
| Teamcenter 2512 `soa_client/java/libs` | `2.17.1` |
| 配合 Spring Boot 4 的 `tc-soa-util` | `2.25.5` |

Teamcenter 独立 SOA 介质使用 `2.17.1`，但这套完整 Log4j 与 Spring Boot 4
共享的 Log4j API 不兼容。`log4j-core 2.17.1` 搭配 API `2.25.5` 会报
`NoSuchMethodError: LoaderUtil.getClassLoaders()`。因此 util 在 Spring Boot
进程内使用兼容的 `2.25.5` API/Core，并注册所需的 Core Provider。消费方可通过
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
