package com.lindautech.psx;

import javax.swing.*;

/**
 * Custom JComboBox that keeps track of which index in the UI it is a part of in order to
 * properly assign components.
 *
 * @author Eric Lindau
 */
class ComboBox extends JComboBox {

    private int index;

    /**
     * ComboBox constructor.
     *
     * @param items the text items that are listed in the dropdown box
     * @param index the place of the ComboBox in the UI
     */
    ComboBox(String[] items, int index) {
        super(items);
        this.index = index;
    }

    /**
     * Gets the index.
     *
     * @return the index
     */
    int getIndex() {
        return this.index;
    }

}
