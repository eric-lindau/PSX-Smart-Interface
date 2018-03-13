package com.lindautech.psx.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient extends Thread implements NetworkClient {

  private Socket socket;
  private BufferedReader bufferedReader;
  private PrintWriter dataWriter;

  public TCPClient(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {

  }

  public void sendData(CharSequence data) {
  }

  public void close() {

  }

  public void destroyConnection() {
//    try {
//
//    } catch() {
//
//    }
  }

}
