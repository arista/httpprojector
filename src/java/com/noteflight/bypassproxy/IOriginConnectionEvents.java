package com.noteflight.bypassproxy;

public interface IOriginConnectionEvents
{
  public void responseData(String connectionName, byte[] data);
  public void originConnectionClosed(String connectionName);
}
