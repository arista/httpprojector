package com.noteflight.bypassproxy;

public interface IProxyConnectionEvents
{
  public void requestData(String connectionName, byte[] data);
  public void openConnection(String connectionName);
  public void closeConnection(String connectionName);
}
