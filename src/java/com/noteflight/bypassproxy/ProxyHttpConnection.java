package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;

public class ProxyHttpConnection
  implements Runnable
{
  private Socket _socket;
  private InputStream _in;
  private OutputStream _out;
  
  private ByteArrayOutputStream _initialBuffer = new ByteArrayOutputStream();
  private IAgentConnections _agentConnections;
  private IProxyConnections _proxyConnections;
  private String _connectionName;

  public ProxyHttpConnection(Socket socket,
                             IAgentConnections agentConnections,
                             IProxyConnections proxyConnections)
  {
    this._socket = socket;
    this._agentConnections = agentConnections;
    this._proxyConnections = proxyConnections;
  }

  public void start()
  {
    new Thread(this).start();
  }

  public void run()
  {
    System.out.println("Got http connection from " + _socket.getInetAddress());
    try {
      try {
        _in = _socket.getInputStream();
        _out = _socket.getOutputStream();

        String host = readHost(_initialBuffer);
        System.out.println("host = " + host);

        AgentConnection agent = _agentConnections.getAgent(host);
        if(agent != null) {
          _connectionName = _proxyConnections.registerProxyConnection(this);
          agent.sendPacket(new OpenConnectionPacket(_connectionName));
          agent.sendPacket(new RequestDataPacket(_connectionName, _initialBuffer.toByteArray()));

          // Keep reading data
          byte[] buf = new byte[4096];
          int numread;
          while((numread = _in.read(buf)) >= 0) {
            byte[] sendBuf = new byte[numread];
            System.arraycopy(buf, 0, sendBuf, 0, numread);
            agent.sendPacket(new RequestDataPacket(_connectionName, sendBuf));
          }
          agent.sendPacket(new CloseConnectionPacket(_connectionName));
          _proxyConnections.removeProxyConnection(_connectionName);
        }
        else {
          // FIXME - handle null agent
        }
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

  /**
   * Starts reading the request all the way up to the "Host: " header,
   * then returns the value of that header.  Returns null if the
   * request ends before any such header is found.  Any data read
   * during this time is placed into readInput.
   *
   * @param readInput the OutputStream to which any input read should be sent
   * @return the host header, or null if no header found
   **/
  String readHost(OutputStream readInput)
    throws IOException
  {
    StringBuilder currentLine = new StringBuilder();

    // Read enough from the input stream to get the "Host:" header
    byte[] buf = new byte[4096];
    while(true) {
      int numread = _in.read(buf);
      if(numread >= 0) {
        // Write anything we've read into the buffer
        readInput.write(buf, 0, numread);
        for(int i = 0; i < numread; i++) {
          char ch = (char) (buf[i] & 0xff);
          if(ch == 13 || ch == 10) {
            String line = currentLine.toString();
            currentLine = new StringBuilder();
            if(line.toUpperCase().startsWith("HOST: ")) {
              String host = line.substring(6);
              return host;
            }
          }
          else {
            currentLine.append(ch);
          }
        }
      }
      else {
        return null;
      }
    }
  }

  public void responseData(byte[] data)
  {
    try {
      _out.write(data);
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
      // FIXME - implement this
    }
  }

  public void closeConnection()
  {
    try {
      _out.close();
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
      // FIXME - implement this
    }
  }
}
