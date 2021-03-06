package com.ticandroid.baley_labeye.utils;

import android.util.Log;

import java.text.ParseException;
import java.util.Arrays;

/**
 * Util to cast classes to another.
 *
 * @author Labeye
 */
public final class Caster {

    /**
     * Splittable element.
     **/
    private static final String SPLITTER = ",";

    /**
     * Not instanciable class.
     **/
    private Caster() {
    }

    private static final int NUMBER_OF_SPLITABLLE_REQUIRED = 2;

    /**
     * Parse the position as string to a array of double.
     *
     * @param position position to parse
     * @return position as double array
     */
    public static double[] positionToDoubleArray(String position) {

        try {

            if (null == position || position.trim().isEmpty()) {
                throw new NullPointerException("The string is empty");
            } else if (!position.contains(SPLITTER)) {
                throw new ParseException("Array doesn't contain the splitter museums", 0);
            } else if (position.split(SPLITTER).length != NUMBER_OF_SPLITABLLE_REQUIRED) {
                throw new ParseException("Array got too many splittable args", position.lastIndexOf(SPLITTER));
            } else {
                return Arrays.stream(position.split(SPLITTER)).mapToDouble(Double::parseDouble).toArray();
            }
        } catch (Exception e) {
            Log.e("Parser", String.format("Exception occured\nException : %s", e));
            return null;
        }
    }
}
