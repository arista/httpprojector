package com.noteflight.httpprojector;

import java.io.*;
import java.net.*;

public class ProjectedHttpConnection
  implements Runnable
{
  private Socket _socket;
  private InputStream _in;
  private OutputStream _out;
  
  private ByteArrayOutputStream _initialBuffer = new ByteArrayOutputStream();

  public ProjectedHttpConnection(Socket socket)
  {
    this._socket = socket;
  }

  public void start()
  {
    new Thread(this).start();
  }

  public void run()
  {
    System.out.println("Got connection from " + _socket.getInetAddress());
    try {
      try {
        _in = _socket.getInputStream();
        _out = _socket.getOutputStream();

        String host = readHost(_initialBuffer);
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
}
