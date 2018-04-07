package com.lindautech.psx.orchestration;

import com.lindautech.psx.data.ValueOption;
import com.lindautech.psx.ui.EntryPanel;
import com.lindautech.psx.ui.PrimaryPanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

public class EntryManager {
  private HashMap<ValueOption, EntryPanel> activeValues;
  private PrimaryPanel primaryPanel;

  public EntryManager(PrimaryPanel primaryPanel) {
    activeValues = new HashMap<ValueOption, EntryPanel>();
    this.primaryPanel = primaryPanel;
  }

  /** Registers a new entry with the entry manager to be placed in the UI. */
  public void registerEntry(EntryPanel entry) {
    entry.getOptionComboBox().addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        // De-select item from the combo box (if it exists) that already has this item selected
        if (e.getStateChange() == ItemEvent.SELECTED && !e.getItem().equals("None")) {
          ValueOption selectedOption = (ValueOption) e.getItem();
          EntryPanel previousAssociatedEntry = activeValues.get(selectedOption);
          if (previousAssociatedEntry != null) {
            previousAssociatedEntry.getOptionComboBox().setSelectedIndex(0);
          }
        }
      }
    });
    primaryPanel.add(entry);
  }
}
