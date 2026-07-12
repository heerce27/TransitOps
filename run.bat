@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\com\transitops\model\User.java src\com\transitops\service\AuthService.java src\com\transitops\TransitOpsServer.java
java -cp out com.transitops.TransitOpsServer
