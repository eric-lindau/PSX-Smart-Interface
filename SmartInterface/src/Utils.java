import net.java.games.input.Component;

import java.util.ArrayList;

/**
 * Static utilities for calculation and logic.
 *
 * @author Eric Lindau
 */
class Utils {

    // List to keep track of which components are inverted to apply multiplier
    static ArrayList<Component> inverted = new ArrayList();

    /**
     * Corrects a component's analog value to the default analog range in PSX.
     *
     * @param component the component to be corrected
     * @return the component's corrected analog value
     */
    static int getAnalogValue(Component component) {
        if (component != null)
            if (inverted.contains(component))
                return Math.round(component.getPollData() * 999) * -1;
            else
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
            if (inverted.contains(component))
                return Math.round(component.getPollData() * modifier) * -1;
            else
                return Math.round(component.getPollData() * modifier);
        else
            if (centered)
                return modifier / 2;
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
     * @return true if the component is pushed or false if it is not
     */
    static boolean isPushed(Component component) {
        return component != null && component.getPollData() > 0;
    }

    /**
     * Applies a deadzone to a value.
     *
     * @param original the original value
     * @param threshold the threshold in which the deadzone should occur
     * @return the value with an applied deadzone
     */
    static int deadzone(int original, int threshold) {
        if (Math.abs(original) <= threshold)
            return 0;
        else
            return original;
    }

    /**
     * Determines the setting of a gain dial at which a component is set.
     *
     * @param component the component to be analyzed
     * @return the setting number
     */
    static int getGainValue(Component component) {
        int value = getAnalogValue(component);
        if (value < -800)
            return -6;
        else if (value < -601)
            return -5;
        else if (value < -401)
            return -4;
        else if (value < -201)
            return -3;
        else if (value < 1)
            return -2;
        else if (value < 201)
            return -1;
        else if (value < 401)
            return 0;
        else if (value < 601)
            return 1;
        else if (value < 801)
            return 2;
        else if (value < 1000)
            return 3;
        else
            return 0;
    }

}
