module ch.rweiss.terminal.nativ
{
  exports ch.rweiss.terminal.nativ;
  exports ch.rweiss.terminal.windows;
  exports ch.rweiss.terminal.linux;
  exports ch.rweiss.bitset;
  
  requires transitive com.sun.jna;
  requires com.sun.jna.platform;
}