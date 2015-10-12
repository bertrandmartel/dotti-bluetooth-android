package fr.bmartel.android.bluetooth.shared;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import fr.bmartel.android.dotti.R;

/**
 * Adapter for new device list
 *
 * @author  Bertrand Martel
 */
public class StableArrayAdapter extends ArrayAdapter<BluetoothDevice> {

    /**
     * list view item hash map
     */
    private HashMap<BluetoothDevice, Integer> mIdMap = new HashMap<BluetoothDevice, Integer>();

    /**
     * main view activity
     */
    private ISharedActivity sharedActivity = null;

    /**
     * inflater for extending list view
     */
    private LayoutInflater fInflater =null;

    /**
     *
     * Build adapter for list view items
     *
     * @param context
     *      Android context
     * @param textViewResourceId
     *      Layout id
     * @param objects
     *      objects to be put in list view
     * @param sharedActivity
     *      main activity view
     */
    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<BluetoothDevice> objects, ISharedActivity sharedActivity) {
        super(context, textViewResourceId, objects);

        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }

        this.sharedActivity=sharedActivity;

        fInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Set specific view items in list
     *
     * @param position
     *      item selected position
     * @param convertView
     *      view to be displayed
     * @param parent
     *      parent view
     * @return
     *      new view to be displayed
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        View lView = convertView;

        if (lView == null) {
            lView = fInflater.inflate(R.layout.new_device_layout, parent, false);
        }

        /*display bluetooth device name in list view*/
        TextView device_name = (TextView) lView.findViewById(R.id.device_name);

        device_name.setText(this.sharedActivity.getListViewAdapter().getItem(position).getName());

        return lView;
    }
}