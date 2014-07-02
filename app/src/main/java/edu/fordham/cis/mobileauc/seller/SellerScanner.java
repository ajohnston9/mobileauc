package edu.fordham.cis.mobileauc.seller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Scans for buyers using BluetoothLeScan and contains list of BluetoothDevices
 *
 * @version 0.01
 * @author Anthony Canicatti
 *
 */
public class SellerScanner implements Runnable{

    private final String TAG = "edu.fordham.cis.mobileauc.seller.SellerScanner";

    private BluetoothAdapter mAdapter;
    private int scanPeriod = 0;
    private volatile boolean finishedScanning = false;
    private volatile ArrayList<BluetoothDevice> mDevices;

    /**
     * Handles how each device is processed when it is found during scanning
     * Spawns a new thread to handle logging and adding to the ArrayList
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback(){

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord){

            new Thread (new Runnable() {

                @Override
                public void run(){

                    Log.i(TAG, "Device found:\nDevice Name: "+device.getName()+"\nDevice Address: "+device.getAddress()+"\n");
                    mDevices.add(device);
                }
            }).start();
        }
    };

    // Constructor -- get passed data
    public SellerScanner(BluetoothAdapter bluetoothAdapter, int scanPeriod){

        mAdapter = bluetoothAdapter;
        this.scanPeriod = scanPeriod;

        Log.i(TAG, "SellerScanner instantiated with scan period of " + (scanPeriod/1000) + " seconds");
    }

    // Begin scanning area
    private void scanLeDevice(){

        Log.i(TAG, "SellerScanner beginning scan for devices.");

        mAdapter.startLeScan(mLeScanCallback);

        // Set TimerTask for completion of LeScan
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "SellerScanner finished scan.");
                mAdapter.stopLeScan(mLeScanCallback);
                finishedScanning = true;
            }
        };
        timer.schedule(task, scanPeriod);

    }

    // Getters for data
    public boolean isFinished(){

        return finishedScanning;
    }

    public ArrayList<BluetoothDevice> getDevices(){
        return mDevices;
    }

    @Override
    public void run() {

        Log.i(TAG, "SellerScanner running!");
        scanLeDevice();
    }
}
