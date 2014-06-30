package edu.fordham.cis.mobileauc.seller;

import android.bluetooth.BluetoothAdapter;

/**
 * Handles the setting up of the Seller Components
 * @author Andrew Johnston
 * @version 0.01
 */
public class SellerManager implements Runnable {

    private BluetoothAdapter mAdapter;
    private volatile boolean terminate = false;


    public SellerManager(BluetoothAdapter adapter) {
        mAdapter = adapter;
    }

    public void terminate() {
        terminate = true;
    }

    @Override
    public void run() {
        while (!terminate) {
            //TODO: Set up a GATT Client Here
            //TODO: Scan for GATT Servers Here
            //TODO: Connect to GATT Servers, Push Ask, Store Bid
        }



    }




}
