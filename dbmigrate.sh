#!/bin/bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xms2048m -Xmx2048m -XX:-OmitStackTraceInFastThrow -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+DisableExplicitGC -Dfile.encoding=UTF8 -Dlogback.configurationFile=properties/logback.xml -cp "*" edu.wvup.cs460.db.DatabaseMigration -Dmain.properties=properties/main.properties
