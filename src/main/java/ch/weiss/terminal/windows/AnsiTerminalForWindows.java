package ch.weiss.terminal.windows;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.ptr.IntByReference;

public class AnsiTerminalForWindows
{
  private static final int UTF_8_CODE_PAGE = 65001;
  private static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;
  private static final int ENABLE_LINE_INPUT = 0x0002;
  private static final int ENABLE_ECHO_INPUT = 0x0004;
  private static final int ENABLE_LINE_AND_ECHO_INPUT = ENABLE_LINE_INPUT + ENABLE_ECHO_INPUT;

  /**
   * Enables the virtual terminal (VT) mode for the windows console. Note that VT
   * mode is only supported by newer versions of Windows 10 and later. 
   * For details see here https://docs.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences
   * @return true if successful, false if OS is not windows or this windows
   *         version does not yet support VT mode
   */
  public static boolean enableVirtualTerminalProcessing()
  {
    checkWindows();

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
      return false;
    }
    
    if (areBitsSet(dwModeRef.getValue(), ENABLE_VIRTUAL_TERMINAL_PROCESSING))
    {
      return true;
    }

    int dwMode = setBits(dwModeRef.getValue(), ENABLE_VIRTUAL_TERMINAL_PROCESSING);
    result = windowsConsoleApi.SetConsoleMode(hConsole, dwMode);
    if (!result)
    {
      return false;
    }
    return true;
  }

  public static boolean disableLineAndEchoInput()
  {
    checkWindows();

    Wincon windowsConsoleApi = Kernel32.INSTANCE;
    HANDLE hConsole = windowsConsoleApi.GetStdHandle(Wincon.STD_INPUT_HANDLE);
    if (hConsole == WinBase.INVALID_HANDLE_VALUE)
    {
      return false;
    }

    IntByReference dwModeRef = new IntByReference();
    boolean result = windowsConsoleApi.GetConsoleMode(hConsole, dwModeRef);
    if (!result)
    {
      return false;
    }
    
    if (areBitsNotSet(dwModeRef.getValue(), ENABLE_LINE_AND_ECHO_INPUT))
    {
      return true;
    }

    int dwMode = unsetBits(dwModeRef.getValue(), ENABLE_LINE_AND_ECHO_INPUT);
    result = windowsConsoleApi.SetConsoleMode(hConsole, dwMode);
    if (!result)
    {
      return false;
    }
    return true;
  }

  public static boolean enableUtf8CodePage()
  {
    checkWindows();

    Wincon windowsConsoleApi = Kernel32.INSTANCE;
    boolean result = windowsConsoleApi.SetConsoleOutputCP(UTF_8_CODE_PAGE);
    if (!result)
    {
      return false;
    }
    try
    {
      @SuppressWarnings("resource")
      PrintStream out = getUtf8EncodedPrintStream(FileDescriptor.out);
      System.setOut(out);
      @SuppressWarnings("resource")
      PrintStream err = getUtf8EncodedPrintStream(FileDescriptor.err);
      System.setOut(err);
    }
    catch (@SuppressWarnings("unused") UnsupportedEncodingException ex)
    {
      return false;
    }
    return true;
  }
  
  private static PrintStream getUtf8EncodedPrintStream(FileDescriptor file) throws UnsupportedEncodingException
  {
    FileOutputStream fos = new FileOutputStream(file);
    BufferedOutputStream buffered = new BufferedOutputStream(fos, 128);
    String encoding = StandardCharsets.UTF_8.name();
    PrintStream ps = new PrintStream(buffered, true, encoding);
    return ps;
  }
  
  private static void checkWindows()
  {
    if (!Platform.isWindows())
    {
      throw new UnsupportedOperationException("AnsiTerminalForWindows is not supported on "+System.getProperty("os.name"));
    }
  }
  
  private static boolean areBitsNotSet(int value, int bitMask)
  {
    return !areBitsSet(value, bitMask);
  }

  private static boolean areBitsSet(int value, int bitMask)
  {
    return (value & bitMask) == bitMask;
  }
  
  private static int setBits(int value, int bitMask)
  {
    return value | bitMask;
  }
  
  private static int unsetBits(int value, int bitMask)
  {
    return value & ~bitMask;
  }
}
