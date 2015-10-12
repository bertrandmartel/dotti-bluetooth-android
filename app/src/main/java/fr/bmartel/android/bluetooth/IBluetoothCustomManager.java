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
package fr.bmartel.android.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

import fr.bmartel.android.bluetooth.connection.IBluetoothDeviceConn;
import fr.bmartel.android.bluetooth.listener.IPushListener;
import fr.bmartel.android.utils.ManualResetEvent;

/**
 * Generic interface for bluetooth custom manager
 *
 * @author  Bertrand Martel
 */
public interface IBluetoothCustomManager {

    public ManualResetEvent getEventManager();

    public void broadcastUpdate(String action);

    public void broadcastUpdateStringList(String action, ArrayList<String> strList);

    public void writeCharacteristic(String characUid, byte[] value, BluetoothGatt gatt,IPushListener listener);

    public void readCharacteristic(String characUid, BluetoothGatt gatt);

    public void writeDescriptor(String descriptorUid, BluetoothGatt gatt,byte[] value,String serviceUid,String characUid);

    public HashMap<String,IBluetoothDeviceConn> getConnectionList();
}
