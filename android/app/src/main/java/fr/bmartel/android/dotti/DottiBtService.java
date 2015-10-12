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

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.bmartel.android.bluetooth.BluetoothCustomManager;
import fr.bmartel.android.bluetooth.IBluetoothManagerEventListener;
import fr.bmartel.android.bluetooth.IScanListListener;
import fr.bmartel.android.bluetooth.connection.IBluetoothDeviceConn;
import fr.bmartel.android.bluetooth.shared.ISharedActivity;
import fr.bmartel.android.bluetooth.shared.StableArrayAdapter;

/**
 * Service persisting bluetooth connection
 *
 * @author Bertrand Martel
 */
public class DottiBtService extends Service implements ISharedActivity {

    /**
     * Service binder
     */
    private final IBinder mBinder = new LocalBinder();

    private BluetoothCustomManager btManager = null;

    private ArrayList<IScanListListener> scanListListeners = new ArrayList<>();

    /**
     * list view adapter attached to new device view
     */
    private StableArrayAdapter list_view_adapter = null;

    @Override
    public void onCreate() {
        //initiate bluetooth manager object used to manage all Android Bluetooth API
        btManager = new BluetoothCustomManager(this);

        //initialize bluetooth adapter
        btManager.init(this);

        btManager.initListAdapter(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Getter for list view adapter
     *
     * @return list view adapter
     */
    @Override
    public StableArrayAdapter getListViewAdapter() {
        return list_view_adapter;
    }

    public void setListViewAdapter(StableArrayAdapter adapter) {
        list_view_adapter = adapter;
    }

    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    public void addDeviceToList(BluetoothDevice device) {
        for (int i = 0; i < scanListListeners.size(); i++) {
            scanListListeners.get(i).onItemAddedInList(device);
        }
    }

    @Override
    public void notifyChangeInList() {
        for (int i = 0; i < scanListListeners.size(); i++) {
            scanListListeners.get(i).onNotifyChangeInList();
        }
    }

    public boolean isScanning() {
        return btManager.isScanning();
    }

    public void stopScan() {
        btManager.stopScan();
    }

    public void connect(String deviceAddress, Context context) {
        btManager.connect(deviceAddress, context);
    }

    public void scanLeDevice(boolean scan) {
        btManager.scanLeDevice(scan);
    }

    public void removeScanListeners(){
        scanListListeners.clear();
    }

    public void clearListAdapter() {
        btManager.clearListAdapter();
    }

    public void addEventListener(IBluetoothManagerEventListener listener) {
        btManager.addEventListener(listener);
    }

    public boolean disconnect(String deviceAddress) {
        return btManager.disconnect(deviceAddress);
    }

    public void disconnectall() {

        Iterator it = btManager.getConnectionList().entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,IBluetoothDeviceConn> pair = (Map.Entry) it.next();
            pair.getValue().disconnect();
        }
    }

    /*
     * LocalBInder that render public getService() for public access
     */
    public class LocalBinder extends Binder {
        public DottiBtService getService() {
            return DottiBtService.this;
        }
    }

    public void addScanListListener(IScanListListener listener) {
        scanListListeners.add(listener);
    }

    public HashMap<String,IBluetoothDeviceConn> getConnectionList()
    {
        return btManager.getConnectionList();
    }
}
