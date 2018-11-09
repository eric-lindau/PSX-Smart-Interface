package com.lindautech.psx.data.processing;

interface RemoteVariable {
  boolean shouldUpdate();
  String compiled();
}
