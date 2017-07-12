import net.java.games.input.Component;

/**
 * Static utilities for calculation and logic.
 *
 * @author Eric Lindau
 */
class Utils {

    /**
     * Corrects a component's analog value to the valid range in PSX of -999 to 999.
     * @param component The component to be corrected.
     * @return An integer from range -999 to 999 that is the component's corrected analog value.
     */
    static int getAnalogValue(Component component) {
        return Math.round(component.getPollData() * 999);
    }

    /**
     * Calculates the combined value of two analog components to prevent conflicts.
     * @param first The first analog component to be combined.
     * @param second The first analog component to be combined.
     * @return The result of combining the analog components' values.
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
     * @param component The component to be measured.
     * @return A boolean that is true if the component is pushed and false if it is not.
     */
    static boolean isPushed(Component component) {
        return component != null && component.getPollData() > 0;
    }

}
