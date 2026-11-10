# tc-soa-util

Chinese documentation: [README.zh-CN.md](README.zh-CN.md)

Teamcenter SOA Util

Teamcenter version: 2512

Maven artifact version: 2512.2611.02

## Dependency packaging

Applications only need to declare the `tc-soa-util` dependency. The utility
artifact bundles the Java EE JAXB 2.3 runtime and uses the standard Log4j Core
provider supplied by the consuming application's Log4j2 runtime.
The default SOA exception handler also logs the complete exception stack so
consumer services can expose a meaningful, sanitized root cause.
The utility does not bundle an auto-discovered `log4j2.properties`; the
consuming application owns Log4j configuration and appenders.

### Log4j compatibility

| Runtime | Log4j API / Core |
| --- | --- |
| Teamcenter 2512 `soa_client/java/libs` | `2.17.1` |
| `tc-soa-util` with Spring Boot 4 | `2.25.5` |

Teamcenter's standalone SOA kit ships `2.17.1`, but that complete Log4j stack is
not binary-compatible with Spring Boot 4's shared Log4j API. Loading Core
`2.17.1` with API `2.25.5` fails with
`NoSuchMethodError: LoaderUtil.getClassLoaders()`. The utility therefore uses
the Spring Boot 4-compatible `2.25.5` API/Core pair in-process and uses the
standard Core provider from Log4j. Consumers can verify the resolved version with
`mvn dependency:tree`; they must not downgrade the Log4j stack. Exact `2.17.1`
requires running the Teamcenter client in an isolated process or classloader.

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
