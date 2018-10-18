SET JAVA_HOME=c:\program files\java\jdk-11
SET MAVEN=C:\Tools\maven\apache-maven-3.5.2\bin\mvn

%MAVEN% exec:java -Dexec.mainClass=ch.rweiss.terminal.windows.ManualTestAnsiTerminalForWindows -Dexec.arguments="%*" -Dexec.classpathScope=test
