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
package fr.bmartel.android.bluetooth.connection;

import android.bluetooth.BluetoothGatt;

import java.util.TimerTask;
import java.util.UUID;

import fr.bmartel.android.bluetooth.IBluetoothCustomManager;
import fr.bmartel.android.bluetooth.IDevice;
import fr.bmartel.android.bluetooth.listener.IPushListener;

/**
 * Generic template for bluetooth device gatt connection
 *
 * @author Bertrand Martel
 */
public interface IBluetoothDeviceConn {

    /**
     * retrieve bluetooth device address
     *
     * @return
     */
    public String getAddress();

    public String getDeviceName();

    public BluetoothGatt getBluetoothGatt();

    public boolean isConnected();

    /**
     * write to a characteristic
     * @param serviceSmartliteControlUUID
     * @param characteristicSmartliteSettingsUUID
     * @param value
     */
    public void writeCharacteristic(String serviceSmartliteControlUUID, String characteristicSmartliteSettingsUUID, byte[] value,IPushListener listener);

    /**
     * read from a characteristic
     * @param serviceName
     * @param characteristicName
     */
    public void readCharacteristic(String serviceName, String characteristicName);

    public void enableDisableNotification(UUID service, UUID charac, boolean enable);

    public void enableGattNotifications(String service, String charac);

    public IBluetoothCustomManager getManager();

    public IDevice getDevice();

    public void disconnect();
}
