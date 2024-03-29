package edu.fordham.cis.mobileauc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Observable;
import java.util.Observer;

import edu.fordham.cis.mobileauc.buyer.BuyerManager;
import edu.fordham.cis.mobileauc.seller.Message;
import edu.fordham.cis.mobileauc.seller.SellerManager;

/**
 * Gives the user a choice of being buyer or seller, and gets the application ready for its role
 * @author Andrew Johnston
 * @version 0.01
 */
public class MainActivity extends Activity implements Observer{

    // Widgets
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

    // Hold whether the user is seller or buyer
    private boolean mIsUserSeller = false;

    // Show status updates to user
    ProgressDialog pd;

    /**
     *Holds the price per Megabyte of data. Needs to be static so it can be referenced in View.OnClickListener()
     */
    public static int mPrice = 0;

    private final int    MAX_INTERVAL_SIZE = 29;
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

        mInterval1Text.setText("1min");
        mInterval2Text.setText("1min");

        pd = new ProgressDialog(MainActivity.this);

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
                mInterval1Text.setText((progress+1) + "min");
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
                mInterval2Text.setText((progress+1) + "min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPricePrompt.setText("Maximum Bid: ");

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
        //Not technically necessary, but we'll add hooks to Seller radio button as well
        mRadioSeller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    mPricePrompt.setText("Asking Price: ");
                    mIsUserSeller = true;
                }
                else {
                    mPricePrompt.setText("Maximum Bid: ");
                    mIsUserSeller = false;
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

                // This should get the price as an integer in pennies
                String priceStr = mPriceField.getText().toString();
                if(priceStr.isEmpty() || priceStr.equals("")){

                    Toast.makeText(getApplicationContext(), "Enter a valid price!", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    double priceDou = Double.parseDouble(priceStr) * 100;
                    int price = (int)priceDou;
                    Log.i(TAG, "User entered price: "+price+" pennies");
                }


                if (mIsUserSeller) {
                    Log.i(TAG, "User is set as Seller");
                    Toast.makeText(MainActivity.this, "WARNING: Not supported yet.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i(TAG, "User is set as Buyer");
                    Toast.makeText(MainActivity.this, "WARNING: Not supported yet.",
                            Toast.LENGTH_SHORT).show();
                }
                final int interval1 = mInterval1Seek.getProgress()+1;
                final int interval2 = mInterval2Seek.getProgress()+1;
                Time now = new Time();
                now.setToNow();
                int minute = now.minute;
                minute = minute % (interval1 + interval2);
                if (minute > interval1) { //If we're in the second interval
                    Toast.makeText(MainActivity.this,
                            "Sorry, you must be in the first interval", Toast.LENGTH_SHORT).show();
                    return;
                }

                //We're in the first interval now and all settings are set, Launch Progress Dialog
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {

                        pd.setTitle("Looking for Peers...");
                        pd.setMessage("Please wait while we find peers for auction");
                        pd.setIndeterminate(true); //We don't have definitive progress measurements
                        pd.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (mIsUserSeller) {

                            Time t = new Time();
                            t.setToNow();
                            int min = t.minute;
                            min = min % (interval1+interval2);

                            // Run scanner for rest of interval 1
                            SellerManager manager = new SellerManager(mAdapter, (interval1-min), MainActivity.this, MainActivity.this);
                            Thread sellerThread = new Thread(manager);
                            sellerThread.start();
                            //TODO: Cleanup work post-connection

                            // will this fix the error?
                            return (Void)null;
                        }
                        // If user is buyer
                        BuyerManager buyerManager = new BuyerManager(mAdapter);
                        Thread buyerThread = new Thread(buyerManager);
                        buyerThread.run();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v){

                        // Perhaps future use??
                    }

                };
                task.execute((Void[]) null); //Start with no args

                //Run another Progress Dialog while we transfer data
                //Show data in a Toast

            }
        });
    }

    /**
     * Meant to set the static price variable which I could access from the onClickListener
     * FIXME: Change to getPrice(), find a way for this to be called from submit button's onClick()
     */
    public void setPriceField() {
        String strPrice = mPriceField.getText().toString();
        double dblPrice = 0d; //Needed to prevent uninitialized error
        try {
            dblPrice = Double.parseDouble(strPrice);
        } catch (NumberFormatException e) {
            Log.d(TAG, "Tried to format null variable. Please check for null values before" +
                    "calling MainActivity.getPrice()");
        }
        mPrice = (int) (dblPrice*100);;
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

    @Override
    public void update(Observable observable, final Object o) {

        if(o instanceof Message){
            // Cancel ProgressDialog, bring up warning message
            Log.i(TAG, "Made it to update()");
            pd.cancel();

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Dialog to show user no buyers were found
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(((Message) o).getMsg());
                    builder.setTitle("Warning");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface d, int i){

                        }
                    });
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.show();
                }
            });

        }
    }
}
