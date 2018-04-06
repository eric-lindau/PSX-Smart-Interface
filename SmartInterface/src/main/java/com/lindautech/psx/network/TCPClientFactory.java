package com.lindautech.psx.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClientFactory {
  public StandardClient getClient(String address, int port) {
    try {
      Socket socket = new Socket(address, port);
      BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter dataWriter = new PrintWriter(socket.getOutputStream());
      StandardClient client = new StandardClient(socket, buffer, dataWriter);
      client.start();
      return client;
    } catch(IOException e) {
      // TODO: Document this
      return null;
    }
  }

}
