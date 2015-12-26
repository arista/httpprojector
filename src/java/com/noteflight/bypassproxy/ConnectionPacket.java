package com.noteflight.bypassproxy;

import java.io.*;

public abstract class ConnectionPacket
  extends Packet
{
  String connectionName;
  
  public ConnectionPacket() {}
  
  public ConnectionPacket(String connectionName)
  {
    this.connectionName = connectionName;
  }

  public void write(DataOutput dout) throws IOException
  {
    dout.writeInt(getPacketType().getTypeNum());
    dout.writeUTF(connectionName);
  }
  
  public void read(DataInput din) throws IOException
  {
    connectionName = din.readUTF();
  }
}
