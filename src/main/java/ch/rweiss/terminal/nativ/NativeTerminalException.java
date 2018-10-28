package ch.rweiss.terminal.nativ;

public class NativeTerminalException extends Exception
{
  public NativeTerminalException(String msg)
  {
    super(msg);
  }

  public NativeTerminalException(String msg, Throwable ex)
  {
    super(msg, ex);
  }

  public NativeTerminalException(String msg, int returnCode)
  {
    super(msg+" (return code was "+returnCode+")");
  }
}
