package ch.rweiss.terminal.windows;

import java.io.IOException;

import ch.rweiss.terminal.nativ.NativeTerminalException;

public class ManualTestAnsiTerminalForWindows
{
  public static void main(String[] args) throws IOException
  {
    enableVirtualTerminalProcessing();
    disableLineAndEchoInput();
    enableVirtualTerminalInput();
    enableUtf8CodePage();
  }

  private static void enableVirtualTerminalProcessing()
  {
    printTest("Test Enable Virtual Terminal Processing");
    try
    {
      AnsiTerminalForWindows.enableVirtualTerminalProcessing();
      System.out.println("Virtual Terminal Mode enabled");
      System.out.println();
      System.out.println("\033[32mThis text should be in green !!!! \033[0m");
    }
    catch(NativeTerminalException ex)
    {
      printError("Virtual Terminal Mode not support!", ex);
    }
    System.out.println();
  }

  private static void disableLineAndEchoInput() throws IOException
  {
    printTest("Test Disable Line And Echo Input");
    try
    {
      AnsiTerminalForWindows.disableLineAndEchoInput();
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

  private static void enableVirtualTerminalInput() throws IOException
  {
    printTest("Test Enable Virtual Terminal Input");
    try
    {
      AnsiTerminalForWindows.enableVirtualTerminalInput();
      System.out.println("Virtual Terminal Input enabled.");
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
      printError("Virtual Terminal Input not enabled!", ex);
    }
}

  private static void enableUtf8CodePage()
  {
    printTest("Enable Utf8 Code Page");
    try
    {
      AnsiTerminalForWindows.changeToUtf8CodePage();
      System.out.println("Utf8 Code Page enabled");
      System.out.println();
      System.out.println("   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F");
      for (int line = 0; line < 48; line++)
      {
        String lineHex = Integer.toHexString(line).toUpperCase();
        if (line < 16)
        {
          lineHex = "0"+lineHex;
        }
        System.out.print(lineHex);
        System.out.print(" ");
        for (int column = 0; column < 32; column++)
        {
          char ch = (char)(line*32+column);
          if (Character.isAlphabetic(ch))
          {
            System.out.print(ch);
          }
          else
          {
            System.out.print("?");
          }
          
          System.out.print("  ");
        }
        System.out.println();
      }
    }
    catch(NativeTerminalException ex)
    {
      printError("Utf8 Code Page not enabled!", ex);
    }
  }

  private static void printTest(String test)
  {
    System.out.println();
    System.out.println(test);
    System.out.println("------------------------------------------------");
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
