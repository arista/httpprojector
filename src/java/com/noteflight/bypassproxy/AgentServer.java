package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;

public class AgentServer
  implements Runnable
{
  private int _port;
  private ServerSocket _serverSocket;
  private IAgentConnectionEvents _events;
  
  public AgentServer(int port, IAgentConnectionEvents events)
  {
    this._port = port;
    this._events = events;
  }

  public void start()
  {
    new Thread(this).start();
  }

  public void run()
  {
    try {
      try {
        _serverSocket = new ServerSocket(_port);
        while(true) {
          try {
            Socket s = _serverSocket.accept();
            AgentConnection c = new AgentConnection(s, _events);
            c.start();
          }
          catch(IOException exc) {
            System.out.println("Exception while accepting socket: " + exc);
          }
        }
      }
      finally {
        if(_serverSocket != null) {
          _serverSocket.close();
        }
      }
    }    
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }
}
