package com.noteflight.bypassproxy;

import java.io.*;

public class OpenConnectionPacket
  extends ConnectionPacket
{
  public OpenConnectionPacket() {}

  public OpenConnectionPacket(String connectionName)
  {
    super(connectionName);
  }

  public Type getPacketType()
  {
    return Type.OpenConnectionPacket;
  }

  public String toString()
  {
    return "OpenConnectionPacket(connectionName: \"" + connectionName + "\")";
  }
}
