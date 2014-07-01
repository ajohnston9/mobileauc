package edu.fordham.cis.mobileauc.seller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Anthony on 7/1/14.
 */
public class SellerScanner implements Runnable{

    private final String TAG = "edu.fordham.cis.mobileauc.seller.SellerScanner";

    private BluetoothAdapter mAdapter;
    private int scanPeriod = 0;
    private Handler mHandler;
    private volatile boolean finishedScanning = false;
    private volatile ArrayList<BluetoothDevice> mDevices;

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

    public SellerScanner(BluetoothAdapter bluetoothAdapter, int scanPeriod){

        mAdapter = bluetoothAdapter;
        this.scanPeriod = scanPeriod;

        Log.i(TAG, "SellerScanner instantiated!");
    }


    private void scanLeDevice(){

        Log.i(TAG, "SellerScanner about to scan for devices.");
        mHandler.postDelayed(new Runnable() {
                
            @Override
            public void run() {

                Log.i(TAG, "SellerScanner finished scan.");
                mAdapter.stopLeScan(mLeScanCallback);
                finishedScanning = true;
            };
        }, scanPeriod);

        mAdapter.startLeScan(mLeScanCallback);
    }

    public boolean isFinished(){

        return finishedScanning;
    }

    public ArrayList<BluetoothDevice> getmDevices(){
        return mDevices;
    }

    @Override
    public void run() {

        Log.i(TAG, "SellerScanner running!");
        scanLeDevice();
    }
}
