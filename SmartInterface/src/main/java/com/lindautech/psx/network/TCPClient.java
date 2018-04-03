package com.lindautech.psx.network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

class TCPClient extends Thread implements NetworkClient {
  private Closeable socket;
  private BufferedReader buffer;
  private PrintWriter dataWriter;

  TCPClient(Closeable socket, BufferedReader buffer, PrintWriter dataWriter) {
    this.socket = socket;
    this.buffer = buffer;
    this.dataWriter = dataWriter;
  }

  @Override
  public void run() {
  }

  public void sendData(CharSequence data) {
    if (data != null && data.length() > 0) {
      dataWriter.println(data);
    }
  }

  public void close() {
    try {
      socket.close();
      buffer.close();
      dataWriter.close();
    } catch(IOException ioe) {
      // TODO: Handle this
      System.out.println();
    }
  }
}
