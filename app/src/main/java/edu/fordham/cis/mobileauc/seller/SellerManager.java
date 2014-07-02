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
import java.util.Observable;
import java.util.Observer;

import edu.fordham.cis.mobileauc.MainActivity;

/**
 * Handles the setting up of the Seller Components
 * @author Andrew Johnston
 * @version 0.01
 */
public class SellerManager extends Observable implements Runnable {

    private final String TAG = "edu.fordham.cis.mobileauc.seller.SellerManager";

    private BluetoothAdapter mAdapter;
    private volatile boolean terminate = false;
    private int scanPeriod;
    private Context context;

    private BluetoothGatt mBluetoothGatt;

    // Constructor -- get passed data
    public SellerManager(BluetoothAdapter adapter, int scanPeriod, Context context, MainActivity m) {

        mAdapter = adapter;
        this.scanPeriod = scanPeriod;
        this.context = context;
        addObserver(m);
        Log.i(TAG, "SellerManager instantiated!");
    }

    // Will end the thread
    public void terminate() {
        terminate = true;
    }

    public boolean isRunning(){
        return !terminate;
    }

    @Override
    public void run() {
        while (!terminate) {
            //TODO: Set up a GATT Client Here
            //TODO: Connect to GATT Servers, Push Ask, Store Bid

            Log.i(TAG, "SellerManager running!");

            // Scan for nearby devices
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

                Message message = new Message("Sorry, no buyers found.");
                this.setChanged();
                this.notifyObservers(message);
                return;
            }
            for(BluetoothDevice device : mScannedDevices){

                mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
            }
        }
    }

    // Callback when device.connect() is called - either connection or disconnection
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        /**
         * Called whenever we connect or disconnect from a device.
         */
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //TODO: Do we need the super call?
            //super.onConnectionStateChange(gatt, status, newState);

            //When we're connected, make sure they are offering MobileAuc services
            //i.e. they're not some Fitbit/Pebble poseur
            if(status == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT Server on " + gatt.getDevice().getAddress());
                Log.d(TAG, "Attempting to start service discovery");
                mBluetoothGatt.discoverServices();
            }
            else if (status == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT Server");
            }
        }

        @Override
        /**
         * Runs whenever a new service on the device is found
         */
        public void onServicesDiscovered(BluetoothGatt gatt, int status){

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //TODO: Check if the server offers MobileAuc services
                //TODO: If so, fetch bid price and store BLE Address and Bid amt
            }
            else { //Some sort of error happened
                Log.e(TAG, "onServicesDiscovered() received status code " + status);
            }

        }

        @Override
        /**
         *Runs whenever we read a characteristic from a server
         *NOTE: Characteristic contains a value and (potentially?) multiple descriptors
         *TODO: Figure out what those are and retrieve the one for bids
         */
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){

            if(status == BluetoothGatt.GATT_SUCCESS){

                // Get data from BluetoothGattCharacteristic and manipulate it
            }
        }
    };

}
