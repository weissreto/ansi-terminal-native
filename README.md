# ansi-terminal-for-windows
Enables ansi escape sequences in the windows console (Windows 10 and later)

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