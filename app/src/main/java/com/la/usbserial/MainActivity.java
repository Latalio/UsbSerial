package com.la.usbserial;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.la.usbserial.driver.CdcAcmSerialDriver;
import com.la.usbserial.driver.UsbSerialPort;
import com.la.usbserial.driver.UsbSerialProber;
import com.la.usbserial.util.HexDump;
import com.la.usbserial.util.MsgHandler;
import com.la.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    TextView msgText;
    TextView inputText;
    Button sendBtn;

    UsbManager mUsbManager;
    UsbDevice mUsbDevice;
    CdcAcmSerialDriver mSerialDriver;
    UsbSerialPort mPort;

    private final String TAG = MainActivity.class.getSimpleName();
    private final int TIMEOUT = 100;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgText = findViewById(R.id.msgText);
        inputText = findViewById(R.id.inputText);
        sendBtn = findViewById(R.id.sendBtn);


        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devList = mUsbManager.getDeviceList();
        mUsbDevice = (UsbDevice) devList.values().toArray()[0];
//        mUsbDevice = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.d(TAG, String.format(
                "VID: %d, PID: %d, VNAME: %s, PNAME: %s",
                mUsbDevice.getVendorId(),
                mUsbDevice.getProductId(),
                mUsbDevice.getManufacturerName(),
                mUsbDevice.getProductName()
        ));
        Log.d(TAG, "here");

//        mConnection = mUsbManager.openDevice(mUsbDevice);
//        if (mConnection == null) {
//            Log.d(TAG, "Connection built failed.");
//        }
//        Log.d(TAG, "Connection built succeed.");

        mSerialDriver = new CdcAcmSerialDriver(mUsbDevice);
        mPort = mSerialDriver.getPorts().get(0);
//        try {
//            mPort.open(mConnection);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "Port open succeed.");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputText.getText().toString();
                byte[] msgBytes = MsgHandler.toByteArray(msg);
                mSerialIoManager.writeAsync(msgBytes);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + mPort);
        if (mPort == null) {
            msgText.setText(MsgHandler.system("No serial device."));
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(mPort.getDriver().getDevice());
            if (connection == null) {
                msgText.setText(MsgHandler.system("Opening device failed"));
                return;
            }

            try {
                mPort.open(connection);
                mPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                msgText.setText(MsgHandler.error("Error opening device: " + e.getMessage()));
                try {
                    mPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mPort = null;
                return;
            }
            msgText.setText(
                    MsgHandler.system("Serial device: " + mPort.getClass().getSimpleName()));
        }
        onDeviceStateChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (mPort != null) {
            try {
                mPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            mPort = null;
        }
        finish();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        msgText.append(message);
//        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }


}
