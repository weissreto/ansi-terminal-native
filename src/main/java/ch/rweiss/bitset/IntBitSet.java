package ch.rweiss.bitset;

/**
 * Bit operations on a int
 * @author Reto Weiss
 */
public class IntBitSet
{
  public static final IntBitSet EMPTY = fromInt(0);
  
  private final int bits;
  
  private IntBitSet(int bits)
  {
    this.bits = bits;
  }
  
  public static IntBitSet fromInt(int bits)
  {
    return new IntBitSet(bits);
  }

  /**
   * @param bitMask
   * @return true if none of the given bits are set
   */
  public boolean containsNoneOf(IntBitSet bitMask)
  {
    return !containsOneOf(bitMask);
  }

  /**
   * @param bitMask
   * @return true if all of the given bits are set
   */
  public boolean containsAllOf(IntBitSet bitMask)
  {
    return (bits & bitMask.bits) == bitMask.bits;
  }
  
  /**
   * @param bitMask
   * @return true if one of the given bits is set
   */
  public boolean containsOneOf(IntBitSet bitMask)
  {
    return (bits & bitMask.bits) > 0;
  }

  public IntBitSet add(IntBitSet bitMask)
  {
    return new IntBitSet(bits | bitMask.bits);
  }
  
  public IntBitSet remove(IntBitSet bitMask)
  {
    return new IntBitSet(bits & ~bitMask.bits);
  }
  
  public int toInt()
  {
    return bits;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (obj.getClass() != IntBitSet.class)
    {
      return false;
    }
    return bits == ((IntBitSet)obj).bits;
  }
  
  @Override
  public int hashCode()
  {
    return bits;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    for(int bitMask = 0x80000000; bitMask != 0; bitMask = bitMask >>> 1)
    {
      if ((bits & bitMask) > 0)
      {
        builder.append("1");
      }
      else
      {
        builder.append("0");
      }
    }
    return builder.toString();
  }
}
