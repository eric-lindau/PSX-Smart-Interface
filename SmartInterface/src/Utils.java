import net.java.games.input.Component;

/**
 * Static utilities for calculation and logic.
 *
 * @author Eric Lindau
 */
class Utils {

    /**
     * Corrects a component's analog value to the default range in PSX of -999 to 999.
     *
     * @param component the component to be corrected
     * @return the component's corrected analog value
     */
    static int getAnalogValue(Component component) {
        if (component != null)
            return Math.round(component.getPollData() * 999);
        else
            return 0;
    }

    /**
     * Corrects a component's non-negative analog value to to the custom range specified by a modifier.
     *
     * @param component the component to be corrected
     * @param modifier the modifier by which to multiply the original analog value
     * @param centered whether or not the component should be centered if not assigned
     * @return the component's corrected analog value
     */
    static int getAnalogValue(Component component, int modifier, boolean centered) {
        if (component != null)
            return Math.round(component.getPollData() * modifier);
        else
            if (centered)
                return modifier / 2;
            else
                return 0;
    }

    /**
     * Determines the setting of a gain dial at which a component is set
     *
     * @param component the component to be analyzed
     * @return the setting number
     */
    static int getGainValue(Component component) {
        int value = getAnalogValue(component);
        if (value < 101)
            return -6;
        else if (value < 201)
            return -5;
        else if (value < 301)
            return -4;
        else if (value < 401)
            return -3;
        else if (value < 501)
            return -2;
        else if (value < 601)
            return -1;
        else if (value < 701)
            return 0;
        else if (value < 801)
            return 1;
        else if (value < 901)
            return 2;
        else if (value < 1000)
            return 3;
        else
            return 0;
    }

    /**
     * Calculates the combined value of two analog components to prevent conflicts.
     *
     * @param first the first analog component to be combined
     * @param second the first analog component to be combined
     * @return the result of combining the analog components' values
     */
    static int combineAnalog(Component first, Component second) {
        int ret = 0;
        if (first != null)
            ret += Utils.getAnalogValue(first);
        if (second != null)
            ret += Utils.getAnalogValue(second);
        if (ret < -999)
            ret = -999;
        else if (ret > 999)
            ret = 999;

        return ret;
    }

    /**
     * Determines if a non-analog component is pushed.
     *
     * @param component the component to be analyzed
     * @return true if the component is pushed and false if it is not
     */
    static boolean isPushed(Component component) {
        return component != null && component.getPollData() > 0;
    }

}
