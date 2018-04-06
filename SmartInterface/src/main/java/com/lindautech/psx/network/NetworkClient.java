package com.lindautech.psx.network;

import java.io.Closeable;

/**
 * An object that sends network to an external source - likely a server.
 */
public interface NetworkClient extends Closeable {
  void sendData(CharSequence data);
}
