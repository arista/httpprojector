package com.noteflight.httpprojector;

import java.io.*;
import java.net.*;

public class ProjectedHttpServer
  implements Runnable
{
  private int _port;
  private ServerSocket _serverSocket;
  
  public ProjectedHttpServer(int port)
  {
    this._port = port;
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
            ProjectedHttpConnection c = new ProjectedHttpConnection(s);
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
