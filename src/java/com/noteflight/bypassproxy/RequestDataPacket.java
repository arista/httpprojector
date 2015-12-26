package com.noteflight.bypassproxy;

import java.io.*;

public class RequestDataPacket
  extends ConnectionDataPacket
{
  public RequestDataPacket() {}

  public RequestDataPacket(String connectionName, byte[] data)
  {
    super(connectionName, data);
  }

  public Type getPacketType()
  {
    return Type.RequestDataPacket;
  }

  public String toString()
  {
    return "RequestDataPacket(connectionName: \"" + connectionName + "\", data: [" + data.length + " bytes])";
  }
}
