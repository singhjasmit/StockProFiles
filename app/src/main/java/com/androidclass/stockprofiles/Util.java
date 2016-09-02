package com.androidclass.stockprofiles;

/**
 * Created by jsingh on 9/2/16.
 */
public class Util {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
