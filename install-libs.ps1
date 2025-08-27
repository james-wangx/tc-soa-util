$artifacts = @(
    "TcSoaCommon",
    "TcSoaClient",
    "TcSoaStrongModel",
    "TcSoaCoreStrong",
    "TcSoaQueryStrong",
    "TcSoaImportExportStrong",
    "TcSoaCadStrong",
    "TcSoaClassificationStrong",
    "TcSoaAdministrationStrong",
    "TcSoaWorkflowStrong",
    "TcSoaStructureManagementStrong"
)

$libHome = "C:\Program Files\Siemens\TC2412_ROOT\soa_client\java\libs"
$version = "2412"

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

mvn install:install-file `
            -DgroupId="com.teamcenter.net" `
            -DartifactId="tcserver" `
            -Dversion="$version" `
            -Dpackaging="jar" `
            -Dfile="${libHome}\tcserverjavabinding.jar"

mvn install:install-file `
            -DgroupId="com.teamcenter.logging" `
            -DartifactId="tclogging" `
            -Dversion="$version" `
            -Dpackaging="jar" `
            -Dfile="${libHome}\TcLogging.jar"
