package com.noteflight.bypassproxy;

import java.io.*;
import java.net.*;
import java.util.*;

public class ProxyAgent
  implements IProxyConnectionEvents,
             IOriginConnectionEvents
{
  String proxyHostname;
  int proxyPort;
  String hostname;
  String localHostname;
  int localPort;
  ProxyConnection _proxyConnection;

  Map<String,OriginConnection> originByConnectionName = new HashMap<>();

  public ProxyAgent(String proxyHostname, int proxyPort, String hostname, String localHostname, int localPort)
  {
    this.proxyHostname = proxyHostname;
    this.proxyPort = proxyPort;
    this.hostname = hostname;
    this.localHostname = localHostname;
    this.localPort = localPort;
  }

  public void start()
  {
    _proxyConnection = new ProxyConnection(proxyHostname, proxyPort, hostname, this);
    _proxyConnection.start();
  }
  
  public void requestData(String connectionName, byte[] data)
  {
    OriginConnection c = null;
    synchronized(originByConnectionName) {
      c = originByConnectionName.get(connectionName);
    }
    // FIXME - handle null
    c.sendData(data);
  }
  
  public void openConnection(String connectionName)
  {
    try {
      Socket s = new Socket(localHostname, localPort);
      OriginConnection c = new OriginConnection(s, connectionName, this);
      synchronized(originByConnectionName) {
        originByConnectionName.put(connectionName, c);
      }
      c.start();
    }
    catch(IOException exc) {
      throw new RuntimeException(exc);
    }
  }
  
  public void closeConnection(String connectionName)
  {
    OriginConnection c = null;
    synchronized(originByConnectionName) {
      c = originByConnectionName.get(connectionName);
    }
    // FIXME - handle null
    c.closeConnection();
  }
  
  public void responseData(String connectionName, byte[] data)
  {
    _proxyConnection.sendPacket(new ResponseDataPacket(connectionName, data));
  }
  
  public void originConnectionClosed(String connectionName)
  {
    _proxyConnection.sendPacket(new CloseConnectionPacket(connectionName));
  }

  public static void main(String [] args)
  {
    String proxyHostname = args[0];
    int proxyPort = Integer.valueOf(args[1]);
    String hostname = args[2];
    String localHostname = args[3];
    int localPort = Integer.valueOf(args[4]);
    ProxyAgent pa = new ProxyAgent(proxyHostname, proxyPort, hostname, localHostname, localPort);
    pa.start();
  }
}
