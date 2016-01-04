package com.noteflight.httpprojector;

public class HttpProjectorServer
{
  public static void main(String [] args)
  {
    int port = Integer.valueOf(args[0]);
    System.out.println("Listening on port " + port);
    ProjectedHttpServer s = new ProjectedHttpServer(port);
    s.start();
  }
}
