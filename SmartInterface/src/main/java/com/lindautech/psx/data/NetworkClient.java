package com.lindautech.psx.data;

import java.io.Closeable;

/**
 * Handles
 */
public interface NetworkClient extends Closeable {

  public void sendData(CharSequence data);

}
