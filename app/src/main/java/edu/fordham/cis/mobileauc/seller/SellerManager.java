package edu.fordham.cis.mobileauc.seller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Handles the setting up of the Seller Components
 * @author Andrew Johnston
 * @version 0.01
 */
public class SellerManager implements Runnable {

    private final String TAG = "edu.fordham.cis.mobileauc.seller.SellerManager";

    private BluetoothAdapter mAdapter;
    private volatile boolean terminate = false;
    private int scanPeriod;
    private Context context;

    private BluetoothGatt mBluetoothGatt;

    public SellerManager(BluetoothAdapter adapter, int scanPeriod, Context context) {

        mAdapter = adapter;
        this.scanPeriod = scanPeriod;
        this.context = context;

        Log.i(TAG, "SellerManager instantiated!");
    }

    public void terminate() {
        terminate = true;
    }

    @Override
    public void run() {
        while (!terminate) {
            //TODO: Set up a GATT Client Here
            //TODO: Connect to GATT Servers, Push Ask, Store Bid

            Log.i(TAG, "SellerManager running!");

            SellerScanner scanner = new SellerScanner(mAdapter, scanPeriod*60000);
            Thread scannerThread = new Thread(scanner);
            scannerThread.start();

            while(!scanner.isFinished()){
                try {
                    // Sleep until scanner has finished scanning
                    Thread.sleep(100l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<BluetoothDevice> mScannedDevices = scanner.getDevices();
            //If nothing was returned, we leave the method
            if (mScannedDevices == null) {
                Log.d(TAG, "No Buyers Found!");
                //Don't bother doing the other steps
                Toast.makeText(context, "Sorry, no Buyers found!", Toast.LENGTH_SHORT).show();
                return;
            }
            for(BluetoothDevice device : mScannedDevices){

                mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
            }
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

            // Start reading/writing characteristics from connected GATT
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){

            if(status == BluetoothGatt.GATT_SUCCESS){

                // Get data from BluetoothGattCharacteristic and manipulate it
            }
        }
    };

}
