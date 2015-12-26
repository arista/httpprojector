package com.noteflight.bypassproxy;

import java.io.*;

public abstract class ConnectionDataPacket
  extends ConnectionPacket
{
  public byte[] data;

  public ConnectionDataPacket() {}

  public ConnectionDataPacket(String connectionName, byte[] data)
  {
    super(connectionName);
    this.data = data;
  }

  public void write(DataOutput dout) throws IOException
  {
    super.write(dout);
    dout.writeInt(data.length);
    dout.write(data);
  }
  
  public void read(DataInput din) throws IOException
  {
    super.read(din);
    int length = din.readInt();
    data = new byte[length];
    din.readFully(data);
  }
  
}
