package ch.rweiss.terminal.linux;

import java.io.IOException;

import ch.rweiss.terminal.nativ.NativeTerminalException;

public class ManualTestAnsiTerminalForLinux
{
  public static void main(String[] args) throws IOException
  {
    disableLineAndEchoInput();
  }

  private static void disableLineAndEchoInput() throws IOException
  {
    printTest("Test Disable Line And Echo Input");
    try
    {
      AnsiTerminalForLinux.disableLineAndEchoInput();
      System.out.println("Line and Echo Input disabled.");
      System.out.println();
      System.out.println("Press keys to test (type x to exit)");
      char ch;
      do
      {
        ch = (char)System.in.read();
        System.out.println("You pressed key: "+ch);
      } while (ch != 'x');
    }
    catch(NativeTerminalException ex)
    {
      printError("Line and Echo Input not disabled!", ex);
    }
    System.out.println();
  }
  
  private static void printTest(String test)
  {
    System.out.println();
    System.out.println(test);
    System.out.println();
  }
  
  private static void printError(String message, NativeTerminalException ex)
  {
    System.err.println(message);
    printOsNameAndVersion();
    ex.printStackTrace();
  }

  private static void printOsNameAndVersion()
  {
    System.err.print("Operating System: ");
    System.err.print(System.getProperty("os.name"));
    System.err.print(" ");
    System.err.println(System.getProperty("os.version"));
  }
}
