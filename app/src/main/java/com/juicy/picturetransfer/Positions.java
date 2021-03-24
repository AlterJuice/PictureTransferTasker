package com.juicy.picturetransfer;

public class Positions {
    public static final byte posNone = 0;
    public static final byte posStart = 1;
    public static final byte posTop = 1 << 1;
    public static final byte posEnd = 1 << 2;
    public static final byte posBottom = 1 << 3;
    byte position;
    /*
             |    TOP 0   |
          ---|------------|---
    START 270|            |90 END
          ---|------------|---
             | BOTTOM 180 |
    */


    public Positions(){ position = posNone; }
    public Positions(byte position){ this.position = position; }
    public int[] getDegreesFromTo(){ return getAllowedDegreesFromTo(position); }

    static int[] getAllowedDegreesFromTo(byte positions){
        int[] angleFromTo;
        if (Flags.isFlagSet(posStart, positions))
            angleFromTo = new int[]{ Flags.isFlagSet(posTop, positions) ? 90 : 0, Flags.isFlagSet(posBottom, positions) ? 90 : 180};
        else if (Flags.isFlagSet(posTop, positions))
            angleFromTo = new int[]{ Flags.isFlagSet(posEnd, positions) ? 180 : 90, Flags.isFlagSet(posStart, positions) ? 180 : 270};
        else if (Flags.isFlagSet(posEnd, positions))
            angleFromTo = new int[]{ Flags.isFlagSet(posBottom, positions) ? 270 : 180, Flags.isFlagSet(posTop, positions) ? 270 : 360};
        else if (Flags.isFlagSet(posBottom, positions))
            angleFromTo = new int[]{ Flags.isFlagSet(posStart, positions) ? 360 : 270, Flags.isFlagSet(posEnd, positions) ? 360 : 90};
        else
            return new int[] {0, 360};
        //angleFromTo[0] -= 45;
        //angleFromTo[1] += 45;
        return angleFromTo;
    }


}
