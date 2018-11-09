package com.lindautech.psx.data.processing.input;

// TODO: For maximum modularity, pass inputs as Pollables to thread in charge of polling,
// TODO: ... then pass Input to Values
public interface Pollable {
  void poll();
}
