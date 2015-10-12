package fr.bmartel.android.bluetooth.shared;

/**
 * Filter action broadcasted from bluetooh custom service
 *
 * @author Bertrand Martel
 */
public class ActionFilterGatt {

    public final static String ACTION_GATT_CONNECTED           = "fr.bmartel.android.bluetooth.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED        = "fr.bmartel.android.bluetooth.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "fr.bmartel.android.bluetooth.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE           = "fr.bmartel.android.bluetooth.ACTION_DATA_AVAILABLE";

}
