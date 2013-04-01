@echo off
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xms512m -Xmx512m -XX:-OmitStackTraceInFastThrow -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+DisableExplicitGC -Dfile.encoding=UTF8 -Dlogback.configurationFile=properties/logback.xml -cp ".;*" edu.wvup.cs460.NTPAppServer -Dmain.properties=properties/main.properties
