package com.lindautech.psx.data.inputs;

public interface DataSource {
  void refresh();
  void addListener(UpdateListener listener);
}
