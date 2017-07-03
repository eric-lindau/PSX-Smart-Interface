import javax.swing.*;

/**
 * @author Eric Lindau
 */
public class ComboBox extends JComboBox {

    private int index;

    public ComboBox(String[] items, int index) {
        super(items);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
