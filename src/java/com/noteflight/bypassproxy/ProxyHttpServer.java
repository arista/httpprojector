package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;

public class ProxyHttpServer
  implements Runnable
{
  private int _port;
  private ServerSocket _serverSocket;
  private IAgentConnections _agentConnections;
  private IProxyConnections _proxyConnections;
  
  public ProxyHttpServer(int port,
                         IAgentConnections agentConnections,
                         IProxyConnections proxyConnections)
  {
    this._port = port;
    this._agentConnections = agentConnections;
    this._proxyConnections = proxyConnections;
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
        System.out.println("running!");
        while(true) {
          try {
            Socket s = _serverSocket.accept();
            ProxyHttpConnection c = new ProxyHttpConnection(s, _agentConnections, _proxyConnections);
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
