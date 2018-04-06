package com.lindautech.psx.data.input;

public interface DataSource {
  void refresh();
  void addListener(UpdateListener listener);
}
