package com.lindautech.psx.orchestration;

import com.lindautech.psx.data.ValueOption;
import com.lindautech.psx.ui.EntryPanel;
import com.lindautech.psx.ui.PrimaryPanel;

import java.util.HashMap;

public class EntryManager {
  private HashMap<ValueOption, EntryPanel> activeValues;
  private PrimaryPanel primaryPanel;

  public EntryManager(PrimaryPanel primaryPanel) {
    activeValues = new HashMap<ValueOption, EntryPanel>();
    this.primaryPanel = primaryPanel;
  }

  public void registerEntry(EntryPanel entry) {
//    entry.getOptionComboBox().addItemListener();
    primaryPanel.add(entry);
  }
}
