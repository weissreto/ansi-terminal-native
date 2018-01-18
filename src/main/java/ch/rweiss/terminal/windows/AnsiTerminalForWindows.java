package ch.rweiss.terminal.windows;

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

import ch.rweiss.bitset.IntBitSet;

/**
 * Windows native terminal manipulation functions that allows to write 
 * better console programs. 
 * @author Reto Weiss
 */
public class AnsiTerminalForWindows
{
  private static final int UTF_8_CODE_PAGE = 65001;
  private static final IntBitSet ENABLE_VIRTUAL_TERMINAL_PROCESSING = IntBitSet.fromInt(0x0004);
  private static final IntBitSet ENABLE_LINE_INPUT = IntBitSet.fromInt(0x0002);
  private static final IntBitSet ENABLE_ECHO_INPUT = IntBitSet.fromInt(0x0004);
  private static final IntBitSet ENABLE_LINE_AND_ECHO_INPUT = IntBitSet.EMPTY.add(ENABLE_LINE_INPUT).add(ENABLE_ECHO_INPUT);

  /**
   * Enables the virtual terminal (VT) mode for the Windows console. Note that VT
   * mode is only supported by newer versions of Windows 10 and later. 
   * For details see here https://docs.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences
   * @return true if successful, false if this Windows version does not yet support VT mode
   * @throws UnsupportedOperationException if OS is not Windows
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
    
    IntBitSet originalMode = IntBitSet.fromInt(dwModeRef.getValue());
    if (originalMode.containsAllOf(ENABLE_VIRTUAL_TERMINAL_PROCESSING))
    {
      return true;
    }

    IntBitSet newMode = originalMode.add(ENABLE_VIRTUAL_TERMINAL_PROCESSING);
    result = windowsConsoleApi.SetConsoleMode(hConsole, newMode.toInt());
    if (!result)
    {
      return false;
    }
    return true;
  }

  /**
   * Disables line input and echo of input characters. 
   * By default only whole lines can be read from standard in ({@link System#in}).
   * This method allows to read each key pressed on the terminal individual from standard in. 
   * @return true if successful
   * @throws UnsupportedOperationException if OS is not Windows
   */
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
    
    IntBitSet originalMode = IntBitSet.fromInt(dwModeRef.getValue());
    if (originalMode.containsNoneOf(ENABLE_LINE_AND_ECHO_INPUT))
    {
      return true;
    }

    IntBitSet newMode = originalMode.remove(ENABLE_LINE_AND_ECHO_INPUT);
    result = windowsConsoleApi.SetConsoleMode(hConsole, newMode.toInt());
    if (!result)
    {
      return false;
    }
    return true;
  }

  /**
   * <p>Changes the code page (charset) of the terminal to UTF-8.</p>
   * <p>By default the Windows terminal works with the standard Windows code page (charset). 
   * Therefore special Unicode characters that are not supported by the standard code page cannot be used. </p>
   * <p>By changing to UTF-8 more Unicode characters are support. 
   * Note, that the supported Unicode characters are depending on the current terminal's font.</p>  
   * @return true if successful
   * @throws UnsupportedOperationException if OS is not Windows
   */
  public static boolean changeToUtf8CodePage()
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
}
