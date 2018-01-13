# ansi-terminal-for-windows
Provides classes that allows to modify terminal modes for Windows and Linux:
- Enables ansi escape sequences in the windows console (Windows 10 and later)
- Disables line and echo input so that single characters can be read individual from standard IN (Linux, Windows)
- Change code page of Windows terminal to UTF-8 (Windows)

## Example

````
    boolean enabled = AnsiTerminalForWindows.enableVirtualTerminalProcessing();
    if (enabled)
    {
      System.out.println("\033[31m This text should be in red !!!! \033[0m");
    }
    else
    {
      System.err.println("Virtual Terminal Mode not support!");
      System.err.print("Operating System: ");
      System.err.print(System.getProperty("os.name"));
      System.err.print(" ");
      System.err.println(System.getProperty("os.version"));
    }
````