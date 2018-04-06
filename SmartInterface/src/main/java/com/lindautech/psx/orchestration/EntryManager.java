package com.lindautech.psx.orchestration;

import com.lindautech.psx.data.ValueOption;
import com.lindautech.psx.ui.EntryPanel;

import java.util.HashMap;

public class EntryManager {
  HashMap<ValueOption, EntryPanel> activeValues;

  public EntryManager() {
    activeValues = new HashMap<ValueOption, EntryPanel>();
  }

  public void registerEntry(EntryPanel entry) {
//    entry.getOptionComboBox().addItemListener();
  }
}
