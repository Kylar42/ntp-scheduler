#!/bin/bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5050 -Xms2048m -Xmx2048m -XX:-OmitStackTraceInFastThrow -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+DisableExplicitGC -Dfile.encoding=UTF8 -Dlogback.configurationFile=properties/logback.xml -cp "jars/*" edu.wvup.monitor.Monitor -Dmain.properties=properties/main.properties
