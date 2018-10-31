# ansi-terminal-native [![Build Status](https://travis-ci.org/weissreto/ansi-terminal-native.svg?branch=master)](https://travis-ci.org/weissreto/ansi-terminal-native)

Provides classes to modify console terminal modes for Windows and Linux to write Java application that behave like console terminal native applications. 

## Enable ANSI terminal 

Enables all necessary console terminal modes needed to write terminal native applications in Java for Windows 10 and Linux.

```java
NativeTerminal.enableAnsi();
```

If you want to control directly which console terminal modes are enabled you can use classes NativeTerminalForWindows or NativeTerminalForLinux directly. 

## Enable processing of ANSI escape sequences in the windows console

Enable virtual terminal processing in Windows 10 so that you can write ANSI escape sequences to the windows console. 
ANSI escape sequences can be used to change the text color, the background color, move the cursor around, clear the screen, etc.  

```java
NativeTerminalForWindows.enableVirtualTerminalProcessing();
System.out.println("\033[31m This text should be in red !!!! \033[0m");
```

![Virtual Terminal Processing](doc/VirtualTerminalProcessing.png)

## Enable input of ANSI escape sequences in the windows console

Enable virtual terminal input processing in Windows 10 so that you can read ANSI escape sequences in the windows console if a control key is pressed (Up, Down, Left, Right, Insert, Delete, Home, End, PageUp, F1, etc).

```java
NativeTerminalForWindows.enableVirtualTerminalInput();
```

## Disable line and echo input

Disable line and echo input in Windows 10 and Linux so that you can read a single character as soon as the user press a key.

By default line and echo input is enabled for Java. Therefore an application can only read a whole line at once from standard IN after the user entered the line and pressed ENTER.

```java
NativeTerminalForLinux.disableLineAndEchoInput();
System.out.println("Line and Echo Input disabled.");
System.out.println();
System.out.println("Press keys to test (type x to exit)");
char ch;
do
{
  ch = (char)System.in.read();
  System.out.println("You pressed key: "+ch);
} while (ch != 'x');
```

![Disabled Line And Echo Input](doc/DisabledLineAndEchoInput.png)

## Change code page of Windows console to UTF-8 

Change the code page of the Windows console to UTF-8 so that you can write non ASCII characters.

```java
NativeTerminalForWindows.changeToUtf8CodePage();
System.out.println("Utf8 Code Page enabled");
System.out.println();
System.out.println("Special characters: \u0100  \u0101");
```

![UTF-8 Code Page](doc/Utf8CodePage.png)