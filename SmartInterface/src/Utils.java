import net.java.games.input.Component;

/**
 * @author Eric Lindau
 */
class Utils {

    // Inflate value to working range (-999:999) and mod to prevent over 1000
    static int getAnalogValue(Component component) {
        return Math.round(component.getPollData() * 999) % 1000;
    }

    // Gracefully calculate combined value of two analog inputs to prevent conflicts
    static int combineAnalog(Component first, Component second) {
        //* Start at 0, add both values, then check bounds
        int ret = 0;
        if (first != null)
            ret += Utils.getAnalogValue(first);
        if (second != null)
            ret += Utils.getAnalogValue(second);
        if (ret < -999)
            ret = -999;
        else if (ret > 999)
            ret = 999;
        //*

        return ret;
    }

    // Check if non-analog component is "pushed" (buttons)
    static boolean isPushed(Component component) {
        return component != null && component.getPollData() > 0;
    }

}
