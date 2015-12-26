package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;

public class ProxyConnection
{
  private String _proxyHostname;
  private int _proxyPort;
  private String _hostname;
  private Socket _socket;

  private InputStream _in;
  private OutputStream _out;
  private DataInputStream _din;
  private DataOutputStream _dout;

  private IProxyConnectionEvents _events;

  public ProxyConnection(String proxyHostname, int proxyPort, String hostname, IProxyConnectionEvents events)
  {
    _proxyHostname = proxyHostname;
    _proxyPort = proxyPort;
    _hostname = hostname;
    _events = events;
  }

  public void start()
  {
    try {
      try {
        _socket = new Socket(_proxyHostname, _proxyPort);
        _in = _socket.getInputStream();
        _din = new DataInputStream(_in);
        _out = _socket.getOutputStream();
        _dout = new DataOutputStream(_out);

        sendPacket(new AgentInitiatePacket(_hostname));
        readFromProxy();
      }
      finally {
        if(_socket != null) {
          _socket.close();
        }
      }
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }

  public void sendPacket(Packet p)
  {
    try {
      p.write(_dout);
      _dout.flush();
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }

  void readFromProxy()
  {
    try {
      while(true) {
        Packet p = Packet.readPacket(_din);
        System.out.println("Read packet " + p);
        switch(p.getPacketType()) {
        case OpenConnectionPacket: {
          OpenConnectionPacket pp = (OpenConnectionPacket) p;
          _events.openConnection(pp.connectionName);
          break;
        }
        case RequestDataPacket: {
          RequestDataPacket pp = (RequestDataPacket) p;
          _events.requestData(pp.connectionName, pp.data);
          break;
        }
        case CloseConnectionPacket: {
          CloseConnectionPacket pp = (CloseConnectionPacket) p;
          _events.closeConnection(pp.connectionName);
          break;
        }
        default:
          throw new RuntimeException("Illegal packet type " + p.getPacketType() + " received from agent");
        }
      }
    }
    catch(IOException exc) {
      throw new RuntimeException("Error reading from Agent");
    }
  }
}
