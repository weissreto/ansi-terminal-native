SET JAVA_HOME=c:\program files\java\jdk-9.0.1
SET MAVEN=C:\Tools\maven\apache-maven-3.5.2\bin\mvn

%MAVEN% exec:java -Dexec.mainClass=ch.weiss.terminal.windows.ManualTestAnsiTerminalForWindows -Dexec.arguments="%*" -Dexec.classpathScope=test
