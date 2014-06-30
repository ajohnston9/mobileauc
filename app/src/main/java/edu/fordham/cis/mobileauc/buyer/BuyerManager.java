package edu.fordham.cis.mobileauc.buyer;

import android.bluetooth.BluetoothAdapter;

/**
 * Handles the creation of a buyer and the transport of data.
 * Created on 6/29/14.
 *
 * @author Andrew Johnston
 * @version 0.01
 */
public class BuyerManager {

    BluetoothAdapter mAdapter;

    public BuyerManager(BluetoothAdapter adapter) {
        mAdapter = adapter;
    }
}
