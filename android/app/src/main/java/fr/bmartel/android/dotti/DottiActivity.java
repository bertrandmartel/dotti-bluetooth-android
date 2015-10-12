/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Bertrand Martel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.dotti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.bmartel.android.bluetooth.IBluetoothManagerEventListener;
import fr.bmartel.android.bluetooth.IScanListListener;
import fr.bmartel.android.bluetooth.shared.ActionFilterGatt;
import fr.bmartel.android.bluetooth.shared.StableArrayAdapter;

/**
 * Dotti device management main activity
 *
 * @author Bertrand Martel
 */
public class DottiActivity extends Activity {

    /**
     * debug tag
     */
    private String TAG = this.getClass().getName();

    private String deviceAddress = "";

    private ProgressDialog dialog = null;

    private boolean toSecondLevel = false;

    private boolean bound = false;

    /**
     * define if bluetooth is enabled on device
     */
    private final static int REQUEST_ENABLE_BT = 1;

    /**
     * Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * list of device to display
     */
    private ListView device_list_view = null;

    /**
     * current index of connecting device item in device list
     */
    private int list_item_position = 0;

    private DottiBtService currentService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dotti);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth Smart is not supported on your device", Toast.LENGTH_SHORT).show();
            finish();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final ProgressBar progress_bar = (ProgressBar) findViewById(R.id.scanningProgress);

        if (progress_bar != null)
            progress_bar.setEnabled(false);

        final Button button_stop_scanning = (Button) findViewById(R.id.stop_scanning_button);

        if (button_stop_scanning != null)
            button_stop_scanning.setEnabled(false);

        final TextView scanText = (TextView) findViewById(R.id.scanText);

        if (scanText != null)
            scanText.setText("");

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        button_stop_scanning.setEnabled(false);

        final Button button_find_accessory = (Button) findViewById(R.id.scanning_button);

        button_stop_scanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentService!=null && currentService.isScanning()) {

                    currentService.stopScan();

                    if (progress_bar != null) {
                        progress_bar.setEnabled(false);
                        progress_bar.setVisibility(View.GONE);
                    }

                    if (scanText != null)
                        scanText.setText("");

                    if (button_stop_scanning != null)
                        button_stop_scanning.setEnabled(false);
                }
            }
        });

        button_find_accessory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                triggerNewScan();
            }
        });

        if (mBluetoothAdapter.isEnabled()) {

            Intent intent = new Intent(this, DottiBtService.class);

            // bind the service to current activity and create it if it didnt exist before
            startService(intent);
            bound = bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_ENABLE_BT){

            if(mBluetoothAdapter.isEnabled()) {


                Intent intent = new Intent(this, DottiBtService.class);

                // bind the service to current activity and create it if it didnt exist before
                startService(intent);
                bound = bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

            } else {

                Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show();

            }

        }
    }


    /**
     * trigger a BLE scan
     */
    public void triggerNewScan() {

        Button button_stop_scanning = (Button) findViewById(R.id.stop_scanning_button);
        ProgressBar progress_bar = (ProgressBar) findViewById(R.id.scanningProgress);
        TextView scanText = (TextView) findViewById(R.id.scanText);

        if (button_stop_scanning != null && progress_bar != null && scanText != null) {
            if (currentService!=null && !currentService.isScanning()) {

                Toast.makeText(DottiActivity.this, "Looking for new accessories", Toast.LENGTH_SHORT).show();

                if (button_stop_scanning != null)
                    button_stop_scanning.setEnabled(true);

                if (progress_bar != null) {
                    progress_bar.setEnabled(true);
                    progress_bar.setVisibility(View.VISIBLE);
                }

                if (scanText != null)
                    scanText.setText("Scanning ...");

                //start scan so clear list view
                currentService.getListViewAdapter().clear();
                currentService.getListViewAdapter().notifyDataSetChanged();
                currentService.clearListAdapter();

                Log.i(TAG,"START SCAN");

                currentService.disconnectall();

                currentService.scanLeDevice(true);

            } else {
                Toast.makeText(DottiActivity.this, "Scanning already engaged...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //currentService.disconnect(deviceAddress);
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        toSecondLevel=false;
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!toSecondLevel) {

            if (device_list_view!=null) {
                device_list_view.setAdapter(null);
            }

            if (currentService!=null) {

                currentService.disconnectall();
                currentService.getListViewAdapter().clear();
                currentService.getListViewAdapter().notifyDataSetChanged();
                currentService.clearListAdapter();
            }
        }

        if (dialog!=null){
            dialog.cancel();
            dialog=null;
        }

        if (currentService!=null) {
            currentService.removeScanListeners();
            if (currentService.isScanning())
                currentService.stopScan();
        }

        try {
            if (bound) {
                // unregister receiver or you will have strong exception
                unbindService(mServiceConnection);
                bound = false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (ActionFilterGatt.ACTION_GATT_CONNECTED.equals(action)) {

                Log.i(TAG, "Device connected");

            } else if (ActionFilterGatt.ACTION_GATT_DISCONNECTED.equals(action)) {

                Log.i(TAG, "Device disconnected");

                if (device_list_view!=null && device_list_view.getChildAt(list_item_position)!=null) {
                    device_list_view.getChildAt(list_item_position).setBackgroundColor(Color.TRANSPARENT);
                }

                invalidateOptionsMenu();

                if (dialog!=null){

                    dialog.cancel();
                    dialog=null;

                }

            } else if (ActionFilterGatt.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                Log.i(TAG, "Device connected && service discovered");

                device_list_view.getChildAt(list_item_position).setBackgroundColor(Color.BLUE);
                invalidateOptionsMenu();

                //service has been discovered on device => you can address directly the device

                ArrayList<String> actionsStr = intent.getStringArrayListExtra("");
                if (actionsStr.size() > 0) {
                    try {
                        JSONObject mainObject = new JSONObject(actionsStr.get(0));
                        if (mainObject.has("address") && mainObject.has("deviceName") && mainObject.has("deviceName")) {

                            Log.i(TAG, "Setting for device = > " + mainObject.getString("address") + " - " + mainObject.getString("deviceName") + " - " + mainObject.getString("deviceName"));

                            if (dialog!=null){
                                dialog.cancel();
                                dialog=null;
                            }

                            Intent intentDevice = new Intent(DottiActivity.this, DottiDeviceActivity.class);
                            intentDevice.putExtra("deviceAddr", mainObject.getString("address"));
                            intentDevice.putExtra("deviceName", mainObject.getString("deviceName"));
                            toSecondLevel=true;
                            startActivity(intentDevice);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else if (ActionFilterGatt.ACTION_DATA_AVAILABLE.equals(action)) {

                if (intent.getStringArrayListExtra("STATUS") != null) {
                    ArrayList<String> values = intent.getStringArrayListExtra("STATUS");

                }
            }
        }
    };

    /**
     * Manage Bluetooth Service lifecycle
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.i(TAG, "Connected to service");

            currentService = ((DottiBtService.LocalBinder) service).getService();

            currentService.addScanListListener(new IScanListListener() {
                @Override
                public void onItemAddedInList(final BluetoothDevice device) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            currentService.getListViewAdapter().add(device);
                            currentService.getListViewAdapter().notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onNotifyChangeInList() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentService.getListViewAdapter().notifyDataSetChanged();
                        }
                    });
                }
            });
            currentService.addEventListener(new IBluetoothManagerEventListener() {

                @Override
                public void onBluetoothAdapterNotEnabled() {
                    //beware of Android SDK used on this Android device
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                @Override
                public void onEndOfScan() {

                    final Button button_stop_scanning = (Button) findViewById(R.id.stop_scanning_button);
                    final ProgressBar progress_bar = (ProgressBar) findViewById(R.id.scanningProgress);
                    final TextView scanText = (TextView) findViewById(R.id.scanText);

                    Toast.makeText(DottiActivity.this, "End of scanning...", Toast.LENGTH_SHORT).show();
                    if (button_stop_scanning != null)
                        button_stop_scanning.setEnabled(false);
                    if (progress_bar != null)
                        progress_bar.setEnabled(false);
                    if (scanText != null)
                        scanText.setText("");

                }

                @Override
                public void onStartOfScan() {

                }

            });

            device_list_view = (ListView) findViewById(R.id.listView);

            final ArrayList<BluetoothDevice> list = new ArrayList<>();

            currentService.setListViewAdapter(new StableArrayAdapter(DottiActivity.this, R.layout.new_device_layout, list, currentService));

            device_list_view.setAdapter(currentService.getListViewAdapter());

            device_list_view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // selected item
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            device_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {

                    final ProgressBar progress_bar = (ProgressBar) findViewById(R.id.scanningProgress);
                    final TextView scanText = (TextView) findViewById(R.id.scanText);

                    if (progress_bar != null) {
                        progress_bar.setEnabled(false);
                        progress_bar.setVisibility(View.GONE);
                    }

                    if (scanText != null)
                        scanText.setText("");

                    /*stop scanning*/
                    if (currentService.isScanning()) {

                        currentService.stopScan();
                    }

                    /*connect to bluetooth gatt server on the device*/
                    deviceAddress = currentService.getListViewAdapter().getItem(position).getAddress();

                    list_item_position = position;

                    if (!currentService.getConnectionList().containsKey(deviceAddress) ||
                            !currentService.getConnectionList().get(deviceAddress).isConnected()) {

                        dialog = ProgressDialog.show(DottiActivity.this, "", "Connecting ...", true);

                        currentService.connect(deviceAddress, DottiActivity.this);
                    } else {

                        Intent intentDevice = new Intent(DottiActivity.this, DottiDeviceActivity.class);
                        intentDevice.putExtra("deviceAddr", deviceAddress);
                        intentDevice.putExtra("deviceName", currentService.getConnectionList().get(deviceAddress).getDeviceName());
                        toSecondLevel = true;
                        startActivity(intentDevice);

                    }
                }
            });

            triggerNewScan();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    };

    /**
     * add filter to intent to receive notification from bluetooth service
     *
     * @return intent filter
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionFilterGatt.ACTION_GATT_CONNECTED);
        intentFilter.addAction(ActionFilterGatt.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ActionFilterGatt.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ActionFilterGatt.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
