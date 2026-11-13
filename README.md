# tc-soa-util

Teamcenter SOA Utilities

Source repository: [https://github.com/james-wangx/tc-soa-util](https://github.com/james-wangx/tc-soa-util)

## Modules

All modules share the same Maven artifact ID and are distinguished by their Teamcenter-compatible versions.

| Module | Teamcenter version | Maven version |
| --- | --- | --- |
| `tc-soa-util-11.2` | 11.2 | `11.2.2611.01` |
| `tc-soa-util-13.3` | 13.3 | `13.3.2611.01` |
| `tc-soa-util-2506` | 2506 | `2506.2611.01` |
| `tc-soa-util-2512` | 2512 | `2512.2611.03` |

Dependency coordinates:

```xml
<groupId>com.plm.tc.soa</groupId>
<artifactId>tc-soa-util</artifactId>
<version>2512.2611.01</version>
```

## Install dependencies

Install Teamcenter SOA java dependencies into the Maven repository.

Specify libraries home and maven repo path, example:

```powershell
.\install-deps.ps1 -LibHome "C:\Program Files\Siemens\TC13.3_ROOT\soa_client\java\libs" -MavenRepo "C:\Users\james\.m2\repository"
```
