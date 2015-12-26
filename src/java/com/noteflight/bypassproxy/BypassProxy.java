package com.noteflight.bypassproxy;

import java.util.*;

public class BypassProxy
  implements IAgentConnectionEvents,
             IProxyConnections,
             IAgentConnections
{
  private int httpPort;
  private int agentPort;
  private int connectionNum;
  
  // The mapping from host to AgentConnection
  public Map<String,AgentConnection> agentsByHostname = new HashMap<>();
  // The mapping from connection name to incoming http connection
  public Map<String,ProxyHttpConnection> httpConnectionByName = new HashMap<>();
  
  public void registerAgent(AgentConnection agent, String hostname)
  {
    // FIXME - check for duplicates
    synchronized(this) {
      agentsByHostname.put(hostname, agent);
    }
  }
  
  public void responseData(String connectionName, byte[] data)
  {
    ProxyHttpConnection c = null;
    synchronized(this) {
      c = httpConnectionByName.get(connectionName);
    }
    // FIXME - check for null
    c.responseData(data);
  }
  
  public void closeConnection(String connectionName)
  {
    ProxyHttpConnection c = null;
    synchronized(this) {
      c = httpConnectionByName.get(connectionName);
      httpConnectionByName.remove(connectionName);
    }
    // FIXME - check for null
    c.closeConnection();
  }

  public synchronized String registerProxyConnection(ProxyHttpConnection connection)
  {
    String connectionName = generateConnectionName();
    httpConnectionByName.put(connectionName, connection);
    return connectionName;
  }

  public synchronized void removeProxyConnection(String connectionName)
  {
    httpConnectionByName.remove(connectionName);
  }

  private synchronized String generateConnectionName()
  {
    return "Connection-" + (connectionNum++);
  }

  public synchronized AgentConnection getAgent(String host)
  {
    return agentsByHostname.get(host);
  }
  
  public BypassProxy(int httpPort, int agentPort)
  {
    this.httpPort = httpPort;
    this.agentPort = agentPort;
  }

  public void start()
  {
    System.out.println("Listening for HTTP connections on port " + httpPort);
    System.out.println("Listening for agent connections on port " + agentPort);
    ProxyHttpServer ps = new ProxyHttpServer(httpPort, this, this);
    ps.start();
    AgentServer as = new AgentServer(agentPort, this);
    as.start();
  }

  public static void main(String [] args)
  {
    int httpPort = Integer.valueOf(args[0]);
    int agentPort = Integer.valueOf(args[1]);
    BypassProxy bp = new BypassProxy(httpPort, agentPort);
    bp.start();
  }
}
