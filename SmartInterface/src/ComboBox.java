import javax.swing.*;

/**
 * Custom JComboBox that keeps track of which index it is a part of in order to
 * properly assign components.
 *
 * @author Eric Lindau
 */
class ComboBox extends JComboBox {

    // Index to be kept track of
    private int index;

    // Constructor
    ComboBox(String[] items, int index) {
        super(items);
        this.index = index;
    }

    //Getter
    int getIndex() {
        return this.index;
    }

}
