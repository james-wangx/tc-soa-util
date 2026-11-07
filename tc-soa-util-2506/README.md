# tc-soa-util

Teamcenter SOA Util

Teamcenter version: 2506

Maven artifact version: 2506.2611.01

Chinese version: [README.zh-CN.md](README.zh-CN.md)

## Security token integration test

`SecurityTokenIntegrationTest` verifies the Teamcenter session handoff flow:

1. Log in with a user name and password.
2. Obtain a short-lived security token.
3. Log out the session that issued the token.
4. Log in to a new session with the token.
5. Log out the token session.

Credentials are read from environment variables and are never stored in the repository.
The test is skipped when credentials are not configured.

```powershell
$env:TC_URL = "http://192.168.80.107:7001/tc"
$env:TC_USERNAME = "<user>"
$env:TC_PASSWORD = "<password>"
$env:TC_LOCALE = "zh_CN"

mvn -Dtest=SecurityTokenIntegrationTest test
```

Optional variables:

- `TC_URL`: defaults to `http://192.168.80.107:7001/tc`.
- `TC_LOCALE`: defaults to `zh_CN`.
