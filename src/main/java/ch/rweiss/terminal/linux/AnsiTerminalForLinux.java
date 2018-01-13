package ch.rweiss.terminal.linux;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import ch.rweiss.bitset.IntBitSet;
import ch.rweiss.terminal.linux.LibC.Termios;

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
   * @return true if successful
   * @throws UnsupportedOperationException if OS is not Linux
   */
  public static boolean disableLineAndEchoInput()
  {
    checkLinux();

    try
    {
      LibC libC = Native.loadLibrary(Platform.C_LIBRARY_NAME, LibC.class);
      
      if (!isTerminalAvailable(libC))
      {
        System.out.println("No terminal");
        return false;
      }
      
      Termios originalTermios = getTerminalAttributes(libC);
      if (originalTermios == null)
      {
        System.out.println("get");
        return false;
      }
      
      IntBitSet originalMode = IntBitSet.fromInt(originalTermios.c_lflag);
      if (originalMode.doesNotContain(ICANON_ECHO_ECHONL))
      {
        System.out.println("doesNotCntain");
        return true;
      }
      
      Termios noLineAndEchoInputTermios = new Termios(originalTermios);
      noLineAndEchoInputTermios.c_lflag = originalMode.remove(ICANON_ECHO_ECHONL).toInt();

      boolean result = setTerminalAttributes(libC, noLineAndEchoInputTermios);
      if (!result)
      {
        System.out.println("set");
        return false;
      }
      
      installShutdownHookToResetTerminalMode(libC, STANDARD_IN_FILE_DESCRIPTOR, originalTermios);
      return true;
    }
    catch(@SuppressWarnings("unused") LastErrorException ex)
    {
      return false;
    }
  }

  private static boolean setTerminalAttributes(LibC libC, Termios noLineAndEchoInputTermios)
  {
    int returnCode = libC.tcsetattr(STANDARD_IN_FILE_DESCRIPTOR, LibC.TCSANOW, noLineAndEchoInputTermios);
    return returnCode == 0;
  }

  private static boolean isTerminalAvailable(LibC libC)
  {
    return libC.isatty(STANDARD_IN_FILE_DESCRIPTOR) == 1;
  }
  
  private static Termios getTerminalAttributes(LibC libC)
  {
    Termios originalTermios = new Termios();
    int returnCode = libC.tcgetattr(STANDARD_IN_FILE_DESCRIPTOR, originalTermios);
    if (returnCode != 0)
    {
      return null;
    }
    return originalTermios;
  }


  private static void installShutdownHookToResetTerminalMode(LibC libC, final int stdInFd, Termios originalTermios)
  {
    Runtime.getRuntime().addShutdownHook(
        new Thread(
            () -> libC.tcsetattr(stdInFd, LibC.TCSANOW, originalTermios)));
  }

  private static void checkLinux()
  {
    if (!Platform.isLinux())
    {
      throw new UnsupportedOperationException(
          "AnsiTerminalForLinux is not supported on " + System.getProperty("os.name"));
    }
  }
}
