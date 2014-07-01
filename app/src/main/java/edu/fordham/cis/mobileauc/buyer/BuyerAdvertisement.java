package edu.fordham.cis.mobileauc.buyer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisementData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;

/**
 * Transmits Advertisements looking for Sellers
 * @author Andrew Johnston
 * @version 0.01
 */
public class BuyerAdvertisement implements Runnable {

    private BluetoothAdapter mAdapter;
    private BluetoothLeAdvertiser mAdvertiser;;
    private final String TAG = "edu.fordham.cis.mobileauc.buyer.BuyerAdvertisement";

    /**
     * Accepts BluetoothAdapter so the thread can be run independently
     * @param adapter The Initialized, running Bluetooth Adapter
     */
    public BuyerAdvertisement(BluetoothAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * Starts the thread, sends out Bluetooth Advertisements looking for buyers
     */
    @Override
    public void run() {
        mAdvertiser = mAdapter.getBluetoothLeAdvertiser();
        AdvertiseSettings settings = this.getAdvertisementSettings();
        mAdvertiser.startAdvertising(settings, this.getAdvertisementData(), new AdvertiseCallback() {
            @Override
            public void onSuccess(AdvertiseSettings advertiseSettings) {
                //Handle Success Here
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Advertisement failed with code " + i);
            }
        });




    }

    private AdvertiseSettings getAdvertisementSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        //Since we're running in background, we'll run in balanced mode
        //(few adverts, less battery power), but high power so the packets
        //will actually go somewhere
        //NOTE: Order of the next two lines is important, or BALANCED will overwrite
        //the high power setting
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        //This will allow other devices to query and actually connect
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_TYPE_CONNECTABLE);
        AdvertiseSettings settings = builder.build();
        return settings;
    }

    private AdvertisementData getAdvertisementData() {
        AdvertisementData.Builder builder = new AdvertisementData.Builder();
        builder.setIncludeTxPowerLevel(false);
        //TODO: Have GATT Server Started, Pass UUID to this class
        builder.setServiceUuids(new ArrayList<ParcelUuid>());
        AdvertisementData data = builder.build();
        return data;
    }
}
