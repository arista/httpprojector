package com.noteflight.bypassproxy;

import java.io.*;

public class AgentInitiatePacket
  extends Packet
{
  public String hostname;
  
  public AgentInitiatePacket() {}

  public AgentInitiatePacket(String hostname)
  {
    this.hostname = hostname;
  }

  public Type getPacketType()
  {
    return Type.AgentInitiatePacket;
  }
  
  public void write(DataOutput dout) throws IOException
  {
    dout.writeInt(getPacketType().getTypeNum());
    dout.writeUTF(hostname);
  }
  
  public void read(DataInput din) throws IOException
  {
    hostname = din.readUTF();
  }

  public String toString()
  {
    return "AgentInitiatePacket(hostname: \"" + hostname + "\")";
  }
}
