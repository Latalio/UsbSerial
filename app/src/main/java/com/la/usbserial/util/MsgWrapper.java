package com.la.usbserial.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgWrapper {
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
}
