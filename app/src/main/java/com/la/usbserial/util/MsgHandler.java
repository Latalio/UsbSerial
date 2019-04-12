package com.la.usbserial.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgHandler {
    private static final String TAG = MsgHandler.class.getSimpleName();

    public static String system(String msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[SYSTEM] " + df.format(new Date()) + "\n" + msg;
    }

    public static String error(String msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[ERROR] " + df.format(new Date()) + "\n" + msg;
    }

    public static String send(String msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[SEND] " + df.format(new Date()) + "\n" + msg;
    }

    public static String get(String msg) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[GET] " + df.format(new Date()) + "\n" + msg;
    }

    public static byte[] toByteArray(String msg) {
        msg = msg.toUpperCase();
        byte[] result = new byte[msg.length()/2];
        int i = 0;
        while (i < result.length){
            String subStr = msg.substring(2*i, 2*i+2);
            result[i] = (byte) Integer.parseInt(subStr, 16);
            i++;
        }
        Log.d(TAG, "Transfer result: " + MsgHandler.toHexString(result));
        return result;
    }

    public static String toHexString(byte[] byteArray) {
        StringBuilder result = new StringBuilder("0x");
        for (byte b:byteArray)
        {
            result.append(toHexString(b, true));
        }
        return result.toString();
    }

    public static String toHexString(byte b, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf = new char[2]; // We always want two digits.
        buf[0] = digits[(b >> 4) & 0xf];
        buf[1] = digits[b & 0xf];
        return new String(buf);
    }
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] UPPER_CASE_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };


}
