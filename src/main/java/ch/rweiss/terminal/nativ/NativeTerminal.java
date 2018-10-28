package ch.rweiss.terminal.nativ;

import com.sun.jna.Platform;

import ch.rweiss.terminal.linux.AnsiTerminalForLinux;
import ch.rweiss.terminal.windows.AnsiTerminalForWindows;

public class NativeTerminal
{
  public static void enableAnsi() throws NativeTerminalException
  {
    if (Platform.isWindows())
    {
      AnsiTerminalForWindows.enableVirtualTerminalProcessing();
      AnsiTerminalForWindows.enableVirtualTerminalInput();
      AnsiTerminalForWindows.disableLineAndEchoInput();
      AnsiTerminalForWindows.changeToUtf8CodePage();
    }
    else if (Platform.isLinux())
    {
      AnsiTerminalForLinux.disableLineAndEchoInput();
    }
    throw new NativeTerminalException(
        "Unsupported operating system "+
        System.getProperty("os.name")+
        ". Supported are Windows and Linux.");
  }
}
