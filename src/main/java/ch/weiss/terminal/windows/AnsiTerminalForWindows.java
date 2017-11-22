package ch.weiss.terminal.windows;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.ptr.IntByReference;

public class AnsiTerminalForWindows
{
  private static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;

  /**
   * Enables the virtual terminal (VT) mode for the windows console. Note that VT
   * mode is only supported by newer versions of Windows 10 and later. 
   * For details see here https://docs.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences
   * @return true if successful, false if OS is not windows or this windows
   *         version does not yet support VT mode
   */
  public static boolean enableVirtualTerminalProcessing()
  {
    if (!Platform.isWindows())
    {
      return false;
    }

    Wincon windowsConsoleApi = Kernel32.INSTANCE;
    HANDLE hConsole = windowsConsoleApi.GetStdHandle(Wincon.STD_OUTPUT_HANDLE);
    if (hConsole == WinBase.INVALID_HANDLE_VALUE)
    {
      return false;
    }

    IntByReference dwModeRef = new IntByReference();
    boolean result = windowsConsoleApi.GetConsoleMode(hConsole, dwModeRef);
    if (!result)
    {
      return result;
    }

    int dwMode = dwModeRef.getValue() + ENABLE_VIRTUAL_TERMINAL_PROCESSING;
    result = windowsConsoleApi.SetConsoleMode(hConsole, dwMode);
    if (!result)
    {
      return result;
    }
    return true;
  }
}
