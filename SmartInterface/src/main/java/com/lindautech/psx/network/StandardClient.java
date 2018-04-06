package com.lindautech.psx.network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A network client used to send data through a socket. The client reads and discards responses
 * constantly to flush any buffers.
 *
 * <p>This class should be utilized as a thread to avoid blocking.
 */
class StandardClient extends Thread implements NetworkClient {
  /** Keeps track of if the client should keep reading from the socket. */
  private boolean reading;
  private Closeable socket;
  private BufferedReader inputBuffer;
  private PrintWriter dataWriter;

  /**
   * Constructs a new standard network client.
   *
   * @param socket the local TCP or UDP socket.
   * @param inputBuffer the data buffer associated with the socket for reading responses.
   * @param dataWriter the data writer associated with the socket for sending data.
   */
  StandardClient(Closeable socket, BufferedReader inputBuffer, PrintWriter dataWriter) {
    reading = true;
    this.socket = socket;
    this.inputBuffer = inputBuffer;
    this.dataWriter = dataWriter;
  }

  @Override
  public void run() {
    try {
      while (reading) {
        inputBuffer.readLine();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendData(CharSequence data) {
    if (data != null && data.length() > 0) {
      dataWriter.println(data);
    }
  }

  @Override
  public void close() {
    try {
      socket.close();
      inputBuffer.close();
      dataWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
