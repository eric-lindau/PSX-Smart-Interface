/**
 * Stores a value so that it can be compared with an incoming value to determine
 * if a value change has occurred.
 *
 * @author Eric Lindau
 */
class Value {

    private String str;
    private boolean hasChanged;

    /**
     * Value constructor.
     */
    Value() {
        this.hasChanged = false;
        this.str = "";
    }

    /**
     * Sets the current value.
     * @param str The value to be set.
     */
    void setStr(String str) {
        if (!this.str.equals(str))
            this.hasChanged = true;
        else
            this.hasChanged = false;
        this.str = str;
    }

    /**
     * Gets the current value.
     * @return The current value.
     */
    String getStr() {
        return this.str;
    }

    /**
     * Determines if the value has been changed.
     * @return A boolean that is true if the value has changed and false if it has not.
     */
    boolean hasChanged() {
        return this.hasChanged;
    }

}
