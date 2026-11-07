# tc-soa-util

Teamcenter SOA 工具库

Teamcenter 版本：2506

Maven 版本：2506.2611.01

英文主文档：[README.md](README.md)

## Security Token 集成测试

`SecurityTokenIntegrationTest` 用于验证 Teamcenter 的会话交接流程：

1. 使用用户名和密码登录。
2. 通过 `TcUtil#getSecurityToken(int)` 获取短时有效的 Security Token。
3. 注销签发 Token 的原会话。
4. 使用 Security Token 建立新会话。
5. 注销 Token 会话。

测试凭据只从环境变量读取，不会写入仓库。未配置凭据时测试会自动跳过。

```powershell
$env:TC_URL = "http://192.168.80.107:7001/tc"
$env:TC_USERNAME = "<用户名>"
$env:TC_PASSWORD = "<密码>"
$env:TC_LOCALE = "zh_CN"

mvn -Dtest=SecurityTokenIntegrationTest test
```

可选环境变量：

- `TC_URL`：默认值为 `http://192.168.80.107:7001/tc`。
- `TC_LOCALE`：默认值为 `zh_CN`。
