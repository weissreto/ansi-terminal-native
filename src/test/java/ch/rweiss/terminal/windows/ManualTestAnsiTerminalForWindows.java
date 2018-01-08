package ch.rweiss.terminal.windows;

import java.io.IOException;

import ch.rweiss.terminal.windows.AnsiTerminalForWindows;

public class ManualTestAnsiTerminalForWindows
{
  public static void main(String[] args) throws IOException
  {
    enableVirtualTerminalProcessing();
    disableLineAndEchoInput();
    enableUtf8CodePage();
  }

  private static void enableVirtualTerminalProcessing()
  {
    printTest("Test Enable Virtual Terminal Processing");
    boolean enabled = AnsiTerminalForWindows.enableVirtualTerminalProcessing();
    if (enabled)
    {
      System.out.println("Virtual Terminal Mode enabled");
      System.out.println();
      System.out.println("\033[32mThis text should be in green !!!! \033[0m");
    }
    else
    {
      System.err.println("Virtual Terminal Mode not support!");
      printOsNameAndVersion();
    }
    System.out.println();
  }

  private static void disableLineAndEchoInput() throws IOException
  {
    printTest("Test Disable Line And Echo Input");
    boolean disabled = AnsiTerminalForWindows.disableLineAndEchoInput();
    if (disabled)
    {
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
    else
    {
      System.err.println("Line and Echo Input not disabled!");
      printOsNameAndVersion();
    }
    System.out.println();
  }
  
  private static void enableUtf8CodePage()
  {
    printTest("Enable Utf8 Code Page");
    boolean enabled = AnsiTerminalForWindows.enableUtf8CodePage();
    if (enabled)
    {
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
    else
    {
      System.err.println("Utf8 Code Page not enabled!");
      printOsNameAndVersion();
    }
  }

  private static void printTest(String test)
  {
    System.out.println();
    System.out.println(test);
    System.out.println();
  }

  private static void printOsNameAndVersion()
  {
    System.err.print("Operating System: ");
    System.err.print(System.getProperty("os.name"));
    System.err.print(" ");
    System.err.println(System.getProperty("os.version"));
  }
}
