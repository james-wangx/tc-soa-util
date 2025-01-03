$artifacts = @(
    "TcSoaCommon",
    "TcSoaClient",
    "TcSoaStrongModel",
    "TcSoaCoreStrong",
    "TcSoaQueryStrong",
    "TcSoaImportExportStrong",
    "TcSoaCadStrong",
    "TcSoaClassificationStrong"
)

$libHome = "C:\Program Files\Siemens\TC13.3_ROOT\soa_client\java\libs"
$version = "13000.3.0"

foreach ($artifact in $artifacts)
{
    mvn install:install-file `
            -DgroupId="com.teamcenter" `
            -DartifactId="$artifact" `
            -Dversion="$version" `
            -Dpackaging="jar" `
            -Dfile="${libHome}\${artifact}_${version}.jar"
}

mvn install:install-file `
            -DgroupId="com.teamcenter.fms" `
            -DartifactId="fccclient" `
            -Dversion="$version" `
            -Dpackaging="jar" `
            -Dfile="${libHome}\fccclient.jar"
