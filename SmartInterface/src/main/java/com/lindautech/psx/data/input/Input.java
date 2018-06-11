package com.lindautech.psx.data.input;

/**
 * An object that serves as a source of data from a physical input, like an embedded controller.
 *
 * <p>Only children of this class should be instantiated.
 */
public interface Input {
  int pollData();
}
