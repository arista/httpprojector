package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;

public class AgentConnection
  implements Runnable
{
  private IAgentConnectionEvents _events;
  private Socket _socket;
  private InputStream _in;
  private OutputStream _out;
  private DataInputStream _din;
  private DataOutputStream _dout;
  private String _hostname;
  
  public AgentConnection(Socket socket,
                         IAgentConnectionEvents events)
  {
    this._socket = socket;
    this._events = events;
  }

  public void start()
  {
    new Thread(this).start();
  }

  public void run()
  {
    System.out.println("Got agent connection from " + _socket.getInetAddress());
    try {
      try {
        _in = _socket.getInputStream();
        _din = new DataInputStream(_in);
        _out = _socket.getOutputStream();
        _dout = new DataOutputStream(_out);

        readFromAgent();
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

  void readFromAgent()
  {
    try {
      while(true) {
        Packet p = Packet.readPacket(_din);
        System.out.println("Read packet " + p);
        switch(p.getPacketType()) {
        case AgentInitiatePacket: {
          AgentInitiatePacket pp = (AgentInitiatePacket) p;
          _hostname = pp.hostname;
          _events.registerAgent(this, _hostname);
          break;
        }
        case ResponseDataPacket: {
          ResponseDataPacket pp = (ResponseDataPacket) p;
          _events.responseData(pp.connectionName, pp.data);
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

  void sendPacket(Packet p)
  {
    try {
      System.out.println("Sending packet " + p);
      p.write(_dout);
      _dout.flush();
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }
}
