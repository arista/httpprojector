package com.noteflight.bypassproxy;

public interface IAgentConnectionEvents
{
  public void registerAgent(AgentConnection agent, String hostname);
  public void responseData(String connectionName, byte[] data);
  public void closeConnection(String connectionName);
}
