package com.techjumper.corelib.utils.basic;

import java.text.DecimalFormat;

/**
 * @author MaTianyu
 * @date 2014-11-21
 */
public class NumberUtil {

    public static int convertToint(String intStr, int defValue) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return defValue;
    }

    public static long convertTolong(String longStr, long defValue) {
        try {
            return Long.parseLong(longStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return defValue;
    }

    public static float convertTofloat(String fStr, float defValue) {
        try {
            return Float.parseFloat(fStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return defValue;
    }

    public static double convertTodouble(String dStr, double defValue) {
        try {
            return Double.parseDouble(dStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return defValue;
    }


    public static Integer convertToInteger(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Long convertToLong(String longStr) {
        try {
            return Long.parseLong(longStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Float convertToFloat(String fStr) {
        try {
            return Float.parseFloat(fStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Double convertToDouble(String dStr) {
        try {
            return Double.parseDouble(dStr);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static String format(String pattern, float number) {
        String result = "";
        try {
            result = new DecimalFormat(pattern).format(number);
        } catch (Exception ignored) {
        }

        return result;
    }

    public static String format(String pattern, double number) {
        String result = "";
        try {
            result = new DecimalFormat(pattern).format(number);
        } catch (Exception ignored) {
        }

        return result;
    }

}
