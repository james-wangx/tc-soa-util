# tc-soa-util 中文文档

英文主文档：[README.md](README.md)

Teamcenter SOA Util

Teamcenter 版本：2512

Maven 制品版本：2512.2611.01

## 依赖打包

业务工程只需要引入 `tc-soa-util` 一个依赖。工具库制品内部已经打包
Teamcenter 兼容的 JAXB 2.3 运行库，对外提供 Teamcenter SOA 客户端所需的
`log4j-core`，并注册高优先级 Log4j Provider。因此 Spring Boot 业务工程
不需要额外配置 JAXB 版本、日志排除项或 Provider。

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
