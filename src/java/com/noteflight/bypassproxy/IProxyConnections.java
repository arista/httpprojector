package com.noteflight.bypassproxy;

public interface IProxyConnections
{
  public String registerProxyConnection(ProxyHttpConnection connection);
  public void removeProxyConnection(String connectionName);
}
