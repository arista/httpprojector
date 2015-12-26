package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;

public class OriginConnection
  implements Runnable
{
  private IOriginConnectionEvents _events;
  private Socket _socket;
  private InputStream _in;
  private OutputStream _out;
  private String _connectionName;
  
  public OriginConnection(Socket socket, String connectionName, IOriginConnectionEvents events)
  {
    this._socket = socket;
    this._connectionName = connectionName;
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
        _in = _socket.getInputStream();
        _out = _socket.getOutputStream();

        byte[] buf = new byte[4096];
        int numread;
        while((numread = _in.read(buf)) >= 0) {
          byte[] sendBuf = new byte[numread];
          System.arraycopy(buf, 0, sendBuf, 0, numread);
          _events.responseData(_connectionName, sendBuf);
        }
        _events.originConnectionClosed(_connectionName);
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

  public void sendData(byte[] data)
  {
    try {
      _out.write(data);
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }

  public void closeConnection()
  {
    try {
      _out.close();
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }
}
