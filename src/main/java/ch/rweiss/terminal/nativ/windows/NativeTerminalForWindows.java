package ch.rweiss.terminal.nativ.windows;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Wincon;

import ch.rweiss.bitset.IntBitSet;
import ch.rweiss.terminal.nativ.NativeTerminalException;

/**
 * Windows native terminal manipulation functions that allows to write 
 * better console programs. 
 * @author Reto Weiss
 */
public class NativeTerminalForWindows
{
  private static final int UTF_8_CODE_PAGE = 65001;
  private static final IntBitSet ENABLE_VIRTUAL_TERMINAL_PROCESSING = IntBitSet.fromInt(0x0004);
  private static final IntBitSet ENABLE_VIRTUAL_TERMINAL_INPUT = IntBitSet.fromInt(0x0200);
  private static final IntBitSet ENABLE_LINE_INPUT = IntBitSet.fromInt(0x0002);
  private static final IntBitSet ENABLE_ECHO_INPUT = IntBitSet.fromInt(0x0004);
  private static final IntBitSet ENABLE_LINE_AND_ECHO_INPUT = IntBitSet.EMPTY.add(ENABLE_LINE_INPUT).add(ENABLE_ECHO_INPUT);

  /**
   * Enables the virtual terminal (VT) mode for the Windows console. Note that VT
   * mode is only supported by newer versions of Windows 10 and later. 
   * For details see here https://docs.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences
   * @throws NativeTerminalException if enabling fails 
   */
  public static void enableVirtualTerminalProcessing() throws NativeTerminalException
  {
    checkWindows();
    ConsoleMode.forStandardOut().enable(ENABLE_VIRTUAL_TERMINAL_PROCESSING);
  }

  /**
   * Enables the virtual terminal (VT) input mode for the Windows console. Note that VT input
   * mode is only supported by newer versions of Windows 10 and later. 
   * For details see here https://docs.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences
   * @throws NativeTerminalException if enabling fails
   */
  public static void enableVirtualTerminalInput() throws NativeTerminalException
  {
    checkWindows();
    ConsoleMode.forStandardIn().enable(ENABLE_VIRTUAL_TERMINAL_INPUT);
  }

  /**
   * Disables line input and echo of input characters. 
   * By default only whole lines can be read from standard in ({@link System#in}).
   * This method allows to read each key pressed on the terminal individual from standard in. 
   * @throws NativeTerminalException if disabling fails
   */
  public static void disableLineAndEchoInput() throws NativeTerminalException
  {
    checkWindows();
    ConsoleMode.forStandardIn().disable(ENABLE_LINE_AND_ECHO_INPUT);
  }

  /**
   * <p>Changes the code page (charset) of the terminal to UTF-8.</p>
   * <p>By default the Windows terminal works with the standard Windows code page (charset). 
   * Therefore special Unicode characters that are not supported by the standard code page cannot be used. </p>
   * <p>By changing to UTF-8 more Unicode characters are support. 
   * Note, that the supported Unicode characters are depending on the current terminal's font.</p>  
   * @throws NativeTerminalException if changing fails
   */
  public static void changeToUtf8CodePage() throws NativeTerminalException
  {
    checkWindows();

    Wincon windowsConsoleApi = Kernel32.INSTANCE;
    boolean result = windowsConsoleApi.SetConsoleOutputCP(UTF_8_CODE_PAGE);
    if (!result)
    {
      throw new NativeTerminalException("Failed to set console output code page");
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
    catch (UnsupportedEncodingException ex)
    {
      throw new NativeTerminalException("Failed to set UTF-8 encoded print streams to System.out and System.err", ex);
    }
  }
  
  private static PrintStream getUtf8EncodedPrintStream(FileDescriptor file) throws UnsupportedEncodingException
  {
    FileOutputStream fos = new FileOutputStream(file);
    BufferedOutputStream buffered = new BufferedOutputStream(fos, 128);
    String encoding = StandardCharsets.UTF_8.name();
    PrintStream ps = new PrintStream(buffered, true, encoding);
    return ps;
  }
  
  private static void checkWindows() throws NativeTerminalException
  {
    if (!Platform.isWindows())
    {
      throw new NativeTerminalException(NativeTerminalForWindows.class.getSimpleName()+" is not supported on "+System.getProperty("os.name"));
    }
  }
}
