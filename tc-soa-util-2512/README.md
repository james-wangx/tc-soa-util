# tc-soa-util

Chinese documentation: [README.zh-CN.md](README.zh-CN.md)

Teamcenter SOA Util

Teamcenter version: 2512

Maven artifact version: 2512.2611.01

## Dependency packaging

Applications only need to declare the `tc-soa-util` dependency. The utility
artifact bundles the Teamcenter-compatible JAXB 2.3 runtime and does not
propagate a logging implementation. This keeps Spring Boot consumer projects
from adding JAXB version overrides or logging exclusions.

## Security token integration test

`SecurityTokenIntegrationTest` verifies the Teamcenter session handoff flow:

1. Log in with a Teamcenter username and password.
2. Obtain a short-lived security token with `TcUtil#getSecurityToken(int)`.
3. Log out the password-authenticated session immediately.
4. Log in with the same username and the security token as the password.
5. Log out the token-authenticated session.

Set `TC_URL`, `TC_USERNAME`, `TC_PASSWORD`, and optionally `TC_LOCALE` to run
the integration test against a Teamcenter environment:

```powershell
mvn -Dtest=SecurityTokenIntegrationTest test
```
