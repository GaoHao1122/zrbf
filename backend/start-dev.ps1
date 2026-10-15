# 开发环境启动脚本：使用 JDK 17 + Maven 3.9 启动后端
$env:JAVA_HOME = 'D:\Software\jdk-17\jdk-17.0.20+8'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Set-Location 'E:\zrbf\backend'
& 'D:\Software\apache-maven-3.9.9\bin\mvn.cmd' spring-boot:run
