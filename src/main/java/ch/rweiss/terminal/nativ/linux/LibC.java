package ch.rweiss.terminal.nativ.linux;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Structure;

interface LibC extends Library
{
  // termios.h
  static final int ICANON = 0000002;
  static final int ECHO = 0000010;
  static final int ECHONL = 0000100;
  static final int TCSANOW = 0;

  int tcgetattr(int fd, Termios termios) throws LastErrorException;

  int tcsetattr(int fd, int opt, Termios termios) throws LastErrorException;

  // unistd.h
  int isatty(int fd);

  // termios.h
  class Termios extends Structure
  {
    public int c_iflag;
    public int c_oflag;
    public int c_cflag;
    public int c_lflag;
    public byte c_line;
    public byte[] filler = new byte[64]; // actual length is platform dependent

    Termios()
    {
    }

    Termios(Termios t)
    {
      c_iflag = t.c_iflag;
      c_oflag = t.c_oflag;
      c_cflag = t.c_cflag;
      c_lflag = t.c_lflag;
      c_line = t.c_line;
      filler = t.filler.clone();
    }

    @Override
    protected List<String> getFieldOrder()
    {
      return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "filler");
    }
  }
}