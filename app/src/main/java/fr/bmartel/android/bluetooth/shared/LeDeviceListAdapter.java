package fr.bmartel.android.bluetooth.shared;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Bluetooth devices list adapter
 */
public class LeDeviceListAdapter extends BaseAdapter {

    /**
     * list of bluetooth devices
     */
    private ArrayList<BluetoothDevice> mLeDevices;

    /**
     * main activity object
     */
    private ISharedActivity sharedActivity = null;


    /**
     * Build bluetooth device adapter
     *
     * @param sharedActivity
     *      main activity view
     */
    public LeDeviceListAdapter(ISharedActivity sharedActivity) {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        this.sharedActivity=sharedActivity;
    }

    /**
     * Add a bluetooth device to list view
     *
     * @param device
     */
    public void addDevice(BluetoothDevice device) {

        System.out.println("ADD DEVICE size => " + mLeDevices.size() );
        if(!mLeDevices.contains(device)) {

            Log.i(this.getClass().getName(), "New Bluetooth device found with name : " + device.getName());
            Log.i(this.getClass().getName(), "Device with address : " + device.getAddress());

            //filter only Dotti
            if (device.getName()!=null) {

                if (device.getName().startsWith("Dotti")) {

                    this.sharedActivity.addDeviceToList(device);

                    this.sharedActivity.notifyChangeInList();

                    mLeDevices.add(device);
                }
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }

    /**
     * Retrieve Bluetooth Device by position id
     *
     * @param position
     *      position id
     * @return
     *      Bluetooth device objet | null if not found
     */
    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    /**
     * Clear Bluetooth device list
     */
    public void clear() {
        System.out.println("IN CLEAR LE DEVICE LIST");
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}