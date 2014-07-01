package edu.fordham.cis.mobileauc.seller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;

/**
 * Handles the setting up of the Seller Components
 * @author Andrew Johnston
 * @version 0.01
 */
public class SellerManager implements Runnable {

    private BluetoothAdapter mAdapter;
    private volatile boolean terminate = false;
    private int scanPeriod;


    public SellerManager(BluetoothAdapter adapter, int scanPeriod) {

        mAdapter = adapter;
        this.scanPeriod = scanPeriod;
    }

    public void terminate() {
        terminate = true;
    }

    @Override
    public void run() {
        while (!terminate) {
            //TODO: Set up a GATT Client Here
            //TODO: Connect to GATT Servers, Push Ask, Store Bid

            SellerScanner scanner = new SellerScanner(mAdapter, scanPeriod*60000);
            Thread scannerThread = new Thread(scanner);
            scannerThread.start();

        }
    }


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if(status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED){

                // We are connected to GATT client??

                gatt.discoverServices();
            }
            else if(status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED){

                // We have disconnected from a GATT client??
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){

            // Start reading/writing characteristics from connected gatt
        }
    };

}
