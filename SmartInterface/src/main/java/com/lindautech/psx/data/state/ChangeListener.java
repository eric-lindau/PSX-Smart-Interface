package com.lindautech.psx.data.state;

// TODO: Mention in explanation that change only propagates in one direction to avoid loops
public interface ChangeListener {
  void update();
}
