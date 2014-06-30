package edu.fordham.cis.mobileauc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.fordham.cis.mobileauc.seller.SellerManager;

/**
 * Gives the user a choice of being buyer or seller, and gets the application ready for its role
 * @author Andrew Johnston
 * @version 0.01
 */
public class MainActivity extends Activity {

    private RadioButton      mRadioBuyer;
    private RadioButton      mRadioSeller;
    private EditText         mPriceField;
    private TextView         mPricePrompt;
    private TextView         mInterval1Text;
    private TextView         mInterval2Text;
    private SeekBar          mInterval1Seek;
    private SeekBar          mInterval2Seek;
    private Button           mSubmitButton;
    private BluetoothAdapter mAdapter;

    private boolean mIsUserSeller = false;

    private final int    MAX_INTERVAL_SIZE = 30;
    private final int    REQUEST_ENABLE_BT = 1;
    private final String TAG               = "edu.fordham.cis.mobileauc.MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRadioBuyer    = (RadioButton) findViewById(R.id.buyerButton);
        mRadioSeller   = (RadioButton) findViewById(R.id.sellerButton);
        mPriceField    = (EditText)    findViewById(R.id.sellerPrice);
        mPricePrompt   = (TextView)    findViewById(R.id.sellerPricePrompt);
        mInterval1Seek = (SeekBar)     findViewById(R.id.interval1Seek);
        mInterval2Seek = (SeekBar)     findViewById(R.id.interval2Seek);
        mInterval1Text = (TextView)    findViewById(R.id.interval1Text);
        mInterval2Text = (TextView)    findViewById(R.id.interval2Text);
        mSubmitButton  = (Button)      findViewById(R.id.submitButton);
        //Check for Bluetooth now so we can launch an intent should it need to be enabled
        BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bluetoothManager.getAdapter();
        if (mAdapter == null || !mAdapter.isEnabled()) { //If the Adapter isn't turned on
            Log.i(TAG, "Bluetooth doesn't appear to be on. Starting Bluetooth...");
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
        //SeekBars should have sane default values
        mInterval1Seek.setMax(MAX_INTERVAL_SIZE);
        mInterval2Seek.setMax(MAX_INTERVAL_SIZE);
        mInterval1Seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mInterval1Text.setText(progress + "min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mInterval2Seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mInterval2Text.setText(progress + "min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Default is the Buyer
        mRadioBuyer.setChecked(true);
        mRadioBuyer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    mPricePrompt.setText("Maximum Bid: ");
                    mIsUserSeller = false;
                }
                else { //Seller Checked
                    mPricePrompt.setText("Asking Price: ");
                    mIsUserSeller = true;
                }
            }
        });
        //We can only advertise during Interval 1, so fire off an error if we're not
        //in that interval. Otherwise, launch a progress dialog and a new thread for
        //Bluetooth Low Energy (yay!)
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If the user is a Seller, they must have a price
                if (mIsUserSeller) {
                    //Grab the price here
                    Toast.makeText(MainActivity.this, "Not supported yet.", Toast.LENGTH_SHORT).show();
                }
                int interval1 = mInterval1Seek.getProgress();
                int interval2 = mInterval2Seek.getProgress();
                Time now = new Time();
                now.setToNow();
                int minute = now.minute;
                minute = minute % (interval1 + interval2);
                if (minute > interval1) { //If we're in the first interval
                    Toast.makeText(MainActivity.this,
                            "Sorry, you must be in the first interval", Toast.LENGTH_SHORT).show();
                    return;
                }
                //We're in the first interval now and all settings are set, Launch Progress Dialog
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        ProgressDialog pd = new ProgressDialog(MainActivity.this);
                        pd.setTitle("Looking for Peers...");
                        pd.setMessage("Please wait while we find peers for auction");
                        pd.setIndeterminate(true); //We don't have definitive progress measurementss
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (mIsUserSeller) {
                            SellerManager manager = new SellerManager(mAdapter);
                            //TODO: Cleanup work post-connection
                        }
                        //TODO: Launch Buyer Thread
                        return null;
                    }
                };
                task.execute((Void[]) null); //Start with no args

                //Run another Progress Dialog while we transfer data
                //Show data in a Toast

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Bluetooth Successfully Started");
            }
            else { //If we didn't manage to start the antenna
                Log.e(TAG, "Bluetooth Unable to Start. Exiting.");
                System.exit(1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
