package com.lindautech.psx.orchestration;

import com.lindautech.psx.data.input.InputOption;
import com.lindautech.psx.ui.EntryPanel;
import com.lindautech.psx.ui.PrimaryPanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

public class EntryManager {
  private HashMap<InputOption, EntryPanel> activeValues;
  private PrimaryPanel primaryPanel;

  public EntryManager(PrimaryPanel primaryPanel, InputOption[] options) {
    activeValues = new HashMap<InputOption, EntryPanel>();
    for (InputOption option : options) {
      activeValues.put(option, null);
    }
    this.primaryPanel = primaryPanel;
  }

  /** Registers a new entry with the entry manager to be placed in the UI. */
  public void registerEntry(EntryPanel entry) {
    // TODO: Refactor and perform Input swaps within Value objects
    entry.getOptionComboBox().addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        // De-select item from the combo box (if it exists) that already has this item selected
        if (e.getStateChange() == ItemEvent.SELECTED && !e.getItem().equals("None")) {
          InputOption selectedOption = (InputOption) e.getItem();
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
