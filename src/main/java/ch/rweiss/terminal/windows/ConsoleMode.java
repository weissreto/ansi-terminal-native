package ch.rweiss.terminal.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.ptr.IntByReference;

import ch.rweiss.bitset.IntBitSet;
import ch.rweiss.terminal.nativ.NativeTerminalException;

class ConsoleMode
{
  private HANDLE hConsole;
  private Wincon windowsConsoleApi;
  
  private ConsoleMode(int stdHandle) throws NativeTerminalException
  {
    windowsConsoleApi = Kernel32.INSTANCE;
    hConsole = windowsConsoleApi.GetStdHandle(stdHandle);
    if (hConsole == WinBase.INVALID_HANDLE_VALUE)
    {
      throw new NativeTerminalException("Failed to get standard handle for terminal");
    }
  }
  
  public static ConsoleMode forStandardIn() throws NativeTerminalException
  {
    return new ConsoleMode(Wincon.STD_INPUT_HANDLE);
  }

  public static ConsoleMode forStandardOut() throws NativeTerminalException
  {
    return new ConsoleMode(Wincon.STD_OUTPUT_HANDLE);
  }

  public void enable(IntBitSet flagsToEnable) throws NativeTerminalException
  {
    IntBitSet originalMode = get();
    if (originalMode.containsAllOf(flagsToEnable))
    {
      return;
    }

    IntBitSet newMode = originalMode.add(flagsToEnable);
    set(newMode);
  }
  
  public void disable(IntBitSet flagsToDisable) throws NativeTerminalException
  {
    IntBitSet originalMode = get();
    if (originalMode.containsNoneOf(flagsToDisable))
    {
      return;
    }

    IntBitSet newMode = originalMode.remove(flagsToDisable);
    set(newMode);
  }

  private IntBitSet get() throws NativeTerminalException
  {
    IntByReference dwModeRef = new IntByReference();
    boolean result = windowsConsoleApi.GetConsoleMode(hConsole, dwModeRef);
    if (!result)
    {
      throw new NativeTerminalException("Failed to get console mode");
    }
    IntBitSet mode = IntBitSet.fromInt(dwModeRef.getValue());
    return mode;
  }

  private void set(IntBitSet newMode) throws NativeTerminalException
  {
    boolean result = windowsConsoleApi.SetConsoleMode(hConsole, newMode.toInt());
    if (!result)
    {
      throw new NativeTerminalException("Failed to set console mode to 0x"+Integer.toHexString(newMode.toInt()));
    }
  }
}
