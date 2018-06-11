package com.lindautech.psx.network;

import com.sun.istack.internal.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class NetworkClientFactory {
  @Nullable
  public NetworkClient getTCPClient(String address, int port) {
    try {
      Socket socket = new Socket(address, port);
      BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter dataWriter = new PrintWriter(socket.getOutputStream());
      IPClient client = new IPClient(socket, buffer, dataWriter);
      client.start();
      return client;
    } catch(IOException e) {
      return null;
    }
  }
}
