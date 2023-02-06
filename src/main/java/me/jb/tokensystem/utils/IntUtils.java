package me.jb.tokensystem.utils;

public class IntUtils {

    public static boolean isntStringInt(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }
}
