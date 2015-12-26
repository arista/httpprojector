package com.noteflight.bypassproxy;

import java.io.*;

public class ResponseDataPacket
  extends ConnectionDataPacket
{
  public ResponseDataPacket() {}

  public ResponseDataPacket(String connectionName, byte[] data)
  {
    super(connectionName, data);
  }

  public Type getPacketType()
  {
    return Type.ResponseDataPacket;
  }

  public String toString()
  {
    return "ResponseDataPacket(connectionName: \"" + connectionName + "\", data: [" + data.length + " bytes])";
  }
}
