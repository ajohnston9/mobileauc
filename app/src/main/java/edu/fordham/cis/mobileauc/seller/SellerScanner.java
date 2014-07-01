package edu.fordham.cis.mobileauc.seller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by Anthony on 7/1/14.
 */
public class SellerScanner implements Runnable{

    private BluetoothAdapter mAdapter;
    private int scanPeriod = 0;
    private Handler mHandler;

    private ArrayList<BluetoothDevice> mDevices;

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback(){

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord){

            new Thread (new Runnable() {

                @Override
                public void run(){

                    mDevices.add(device);
                }
            }).start();
        }
    };

    public SellerScanner(BluetoothAdapter bluetoothAdapter, int scanPeriod){

        mAdapter = bluetoothAdapter;
        this.scanPeriod = scanPeriod;
    }


    private void scanLeDevice(){

        mHandler.postDelayed(new Runnable() {
                
            @Override
            public void run() {

                mAdapter.stopLeScan(mLeScanCallback);
            };
        }, scanPeriod);

        mAdapter.startLeScan(mLeScanCallback);
    }


    @Override
    public void run() {
        scanLeDevice();
    }
}
