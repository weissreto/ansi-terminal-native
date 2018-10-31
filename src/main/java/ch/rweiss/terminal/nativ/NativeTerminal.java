package ch.rweiss.terminal.nativ;

import com.sun.jna.Platform;

import ch.rweiss.terminal.nativ.linux.NativeTerminalForLinux;
import ch.rweiss.terminal.nativ.windows.NativeTerminalForWindows;

public class NativeTerminal
{
  public static void enableAnsi() throws NativeTerminalException
  {
    if (Platform.isWindows())
    {
      NativeTerminalForWindows.enableVirtualTerminalProcessing();
      NativeTerminalForWindows.enableVirtualTerminalInput();
      NativeTerminalForWindows.disableLineAndEchoInput();
      NativeTerminalForWindows.changeToUtf8CodePage();
      return;
    }
    if (Platform.isLinux())
    {
      NativeTerminalForLinux.disableLineAndEchoInput();
      return;
    }
    throw new NativeTerminalException(
        "Unsupported operating system "+
        System.getProperty("os.name")+
        ". Supported are Windows and Linux.");
  }
}
