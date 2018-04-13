package com.lindautech.psx.ui;

import com.lindautech.psx.data.input.InputOption;
import com.lindautech.psx.ui.AnalogEntryPanel;
import com.lindautech.psx.ui.DigitalEntryPanel;
import com.lindautech.psx.ui.EntryPanel;
import com.lindautech.psx.ui.PrimaryPanel;
import net.java.games.input.Component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

public class EntryManager {
  private HashMap<InputOption, EntryPanel> activeValues;
  private PrimaryPanel primaryPanel;
  private int currentId;

  public EntryManager(PrimaryPanel primaryPanel, InputOption[] options) {
    activeValues = new HashMap<InputOption, EntryPanel>();
    for (InputOption option : options) {
      activeValues.put(option, null);
    }
    this.primaryPanel = primaryPanel;
    currentId = 0;
  }

  /** Registers a new entry with the entry manager to be placed in the UI. */
  public void registerEntry(Component component) {
    // TODO: Refactor and perform Input swaps within Value objects
    EntryPanel entry;
    if (component.isAnalog()) {
      entry = new AnalogEntryPanel(component.getName(), currentId++);
    } else {
      entry = new DigitalEntryPanel(component.getName(), currentId++);
      entry.getOptionComboBox().addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            // TODO: Invert Input
            int i = 0;
          }
        }
      });
    }
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
