package com.lindautech.psx;

import javax.swing.*;

/**
 * Custom JCheckBox that keeps track of which index in the UI it is a part of in order to
 * properly invert components
 *
 * @author Eric Lindau
 */
class CheckBox extends JCheckBox {

    private int index;

    /**
     * CheckBox constructor.
     *
     * @param label the text next to the checkbox
     * @param index the place of the CheckBox in the UI
     */
    CheckBox(String label, int index) {
        super(label);
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
