#!/bin/bash

MAIN_CLASS=ch.rweiss.terminal.linux.ManualTestAnsiTerminalForLinux

mvn exec:java -Dexec.mainClass=$MAIN_CLASS -Dexec.arguments="$*" -Dexec.classpathScope=test
