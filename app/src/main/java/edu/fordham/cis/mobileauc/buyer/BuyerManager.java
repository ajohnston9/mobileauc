package edu.fordham.cis.mobileauc.buyer;

import android.bluetooth.BluetoothAdapter;

/**
 * Handles the creation of a buyer and the transport of data.
 * Created on 6/29/14.
 *
 * @author Andrew Johnston
 * @version 0.01
 */
public class BuyerManager implements Runnable {

    BluetoothAdapter mAdapter;

    public BuyerManager(BluetoothAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void run() {
        //TODO: Set up a GATT Server Here
        //TODO: Respond to Scan Responses Requests
        //TODO: Accept Notifications of Ask Amount
        //TODO: Allow 'Read' of Bid Amount
        //TODO: Wait for Notification of Auction Success

    }
}
