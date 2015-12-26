package com.noteflight.bypassproxy;

import java.io.*;
import java.util.*;

public abstract class Packet
{
  public static enum Type
  {
    AgentInitiatePacket(1),
    OpenConnectionPacket(2),
    RequestDataPacket(3),
    ResponseDataPacket(4),
    CloseConnectionPacket(5)
    ;
    private int typeNum;
    private Type(int typeNum)
    {
      this.typeNum = typeNum;
    }
    public int getTypeNum() { return typeNum; }
    private static Map<Integer,Type> typesByTypeNum;
    public static Map<Integer,Type> getTypesByTypeNum()
    {
      if(typesByTypeNum == null) {
        typesByTypeNum = new HashMap<>();
        for(Type t : values()) {
          typesByTypeNum.put(t.getTypeNum(), t);
        }
      }
      return typesByTypeNum;
    }
  }

  public abstract Type getPacketType();
  public abstract void write(DataOutput dout) throws IOException;
  public abstract void read(DataInput din) throws IOException;

  public static Packet readPacket(DataInput din) throws IOException
  {
    int typeNum = din.readInt();
    Type type = Type.getTypesByTypeNum().get(typeNum);
    Packet p = null;
    if(type != null) {
      switch(type) {
      case AgentInitiatePacket:
        p = new AgentInitiatePacket();
        break;
      case OpenConnectionPacket:
        p = new OpenConnectionPacket();
        break;
      case RequestDataPacket:
        p = new RequestDataPacket();
        break;
      case ResponseDataPacket:
        p = new ResponseDataPacket();
        break;
      case CloseConnectionPacket:
        p = new CloseConnectionPacket();
        break;
      }
    }
    if(p == null) {
      throw new RuntimeException("Unknown incoming packet type " + typeNum);
    }
    p.read(din);
    return p;
  }
}
