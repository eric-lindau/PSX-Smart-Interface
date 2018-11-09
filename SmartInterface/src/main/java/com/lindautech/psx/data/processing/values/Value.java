package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.state.ChangeListener;

public interface Value extends ChangeListener {
  Object processed();
}
