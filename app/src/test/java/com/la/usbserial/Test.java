package com.la.usbserial;

import com.la.usbserial.util.MsgHandler;

public class Test {
    public static void main(String[] args) {
        String msg = "0a0b";
        byte[] a = MsgHandler.toByteArray(msg);
        for (int i=0;i<a.length;i++) {
            System.out.println(a[i]);
        }
    }
}
