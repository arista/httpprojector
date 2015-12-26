package com.noteflight.bypassproxy;

import java.io.*;

public class CloseConnectionPacket
  extends ConnectionPacket
{
  public CloseConnectionPacket() {}

  public CloseConnectionPacket(String connectionName)
  {
    super(connectionName);
  }

  public Type getPacketType()
  {
    return Type.CloseConnectionPacket;
  }

  public String toString()
  {
    return "CloseConnectionPacket(connectionName: \"" + connectionName + "\")";
  }
}
