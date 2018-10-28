package ch.rweiss.terminal.linux;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import ch.rweiss.bitset.IntBitSet;
import ch.rweiss.terminal.linux.LibC.Termios;
import ch.rweiss.terminal.nativ.NativeTerminalException;

/**
 * Linux native terminal manipulation functions that allows to write 
 * better console programs. 
 * @author Reto Weiss
 */
public class AnsiTerminalForLinux
{
  private static final IntBitSet ICANON = IntBitSet.fromInt(LibC.ICANON);
  private static final IntBitSet ECHO = IntBitSet.fromInt(LibC.ECHO);
  private static final IntBitSet ECHO_NL = IntBitSet.fromInt(LibC.ECHONL);
  private static final IntBitSet ICANON_ECHO_ECHONL = IntBitSet.EMPTY.add(ICANON).add(ECHO).add(ECHO_NL);
  private static final int STANDARD_IN_FILE_DESCRIPTOR = 0;
  
  /**
   * Disables line input and echo of input characters. 
   * By default only whole lines can be read from standard in ({@link System#in}).
   * This method allows to read each key pressed on the terminal individual from standard in. 
   * @throws NativeTerminalException if disabling fails
   */
  public static void disableLineAndEchoInput() throws NativeTerminalException
  {
    checkLinux();

    try
    {
      LibC libC = Native.load(Platform.C_LIBRARY_NAME, LibC.class);
      
      checkTerminalAvailable(libC);
      
      Termios originalTermios = getTerminalAttributes(libC);      
      IntBitSet originalMode = IntBitSet.fromInt(originalTermios.c_lflag);
      if (originalMode.containsNoneOf(ICANON_ECHO_ECHONL))
      {
        return;
      }
      
      Termios noLineAndEchoInputTermios = new Termios(originalTermios);
      noLineAndEchoInputTermios.c_lflag = originalMode.remove(ICANON_ECHO_ECHONL).toInt();
      setTerminalAttributes(libC, noLineAndEchoInputTermios);
      
      installShutdownHookToResetTerminalMode(libC, STANDARD_IN_FILE_DESCRIPTOR, originalTermios);
    }
    catch(LastErrorException ex)
    {
      throw new NativeTerminalException("Failed to disable line and echo input", ex);
    }
  }

  private static boolean setTerminalAttributes(LibC libC, Termios noLineAndEchoInputTermios) throws NativeTerminalException
  {
    int returnCode = libC.tcsetattr(STANDARD_IN_FILE_DESCRIPTOR, LibC.TCSANOW, noLineAndEchoInputTermios);
    if (returnCode != 0)
    {
      throw new NativeTerminalException("Failed to set terminal console attributes", returnCode);
    }  
    return returnCode == 0;
  }

  private static void checkTerminalAvailable(LibC libC) throws NativeTerminalException
  {
    if (libC.isatty(STANDARD_IN_FILE_DESCRIPTOR) == 0)
    {
      throw new NativeTerminalException("No console terminal available");
    }
  }
  
  private static Termios getTerminalAttributes(LibC libC) throws NativeTerminalException
  {
    Termios originalTermios = new Termios();
    int returnCode = libC.tcgetattr(STANDARD_IN_FILE_DESCRIPTOR, originalTermios);
    if (returnCode != 0)
    {
      throw new NativeTerminalException("Failed to get terminal console attributes", returnCode);
    }
    return originalTermios;
  }


  private static void installShutdownHookToResetTerminalMode(LibC libC, final int stdInFd, Termios originalTermios)
  {
    Runtime.getRuntime().addShutdownHook(
        new Thread(
            () -> libC.tcsetattr(stdInFd, LibC.TCSANOW, originalTermios)));
  }

  private static void checkLinux() throws NativeTerminalException
  {
    if (!Platform.isLinux())
    {
      throw new NativeTerminalException(
          AnsiTerminalForLinux.class.getSimpleName()+" is not supported on " + System.getProperty("os.name"));
    }
  }
}
