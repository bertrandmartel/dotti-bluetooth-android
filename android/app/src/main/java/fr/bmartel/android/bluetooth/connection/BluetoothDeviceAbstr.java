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

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import fr.bmartel.android.bluetooth.ICharacteristicListener;
import fr.bmartel.android.bluetooth.IDevice;
import fr.bmartel.android.bluetooth.connection.IBluetoothDeviceConn;

/**
 * Bluetooth device implementation abstraction
 *
 * @author  Bertrand Martel
 */
public abstract class BluetoothDeviceAbstr implements IDevice {


    private ICharacteristicListener characteristicListener;

    /**
     * bluetooth device gatt connection management
     */
    protected IBluetoothDeviceConn conn = null;

    /**
     * Give bluetooth device connection to device implementation object
     * @param conn
     */
    public BluetoothDeviceAbstr(IBluetoothDeviceConn conn)
    {
        this.conn=conn;
    }

    /**
     * enable gatt notification for a specific service and a specific characteristic
     *
     * @param service
     * @param charac
     */
    public void enableNotification(String service,String charac)
    {
        conn.enableGattNotifications(service, charac);
        conn.enableDisableNotification(UUID.fromString(service), UUID.fromString(charac),true);
    }

    /**
     * getter for bluetooth connection
     * @return
     */
    public IBluetoothDeviceConn getConn()
    {
        return conn;
    }

    /**
     * notify characteristic read event
     *
     * @param characteristic
     *      Bluetooth characteristic
     */
    @Override
    public void notifyCharacteristicReadReceived(BluetoothGattCharacteristic characteristic)
    {
        characteristicListener.onCharacteristicReadReceived(characteristic);
    }

    @Override
    public void notifyCharacteristicWriteReceived(BluetoothGattCharacteristic characteristic)
    {
        characteristicListener.onCharacteristicWriteReceived(characteristic);
    }

    /**
     * notify characteritistic change event
     *
     * @param characteristic
     *      Bluetooth characteristic
     */
    @Override
    public void notifyCharacteristicChangeReceived(BluetoothGattCharacteristic characteristic)
    {
        characteristicListener.onCharacteristicChangeReceived(characteristic);
    }

    /**
     * getter for characteristic listener
     *
     * @return
     */
    public ICharacteristicListener getCharacteristicListener()
    {
        return characteristicListener;
    }

    /**
     * setter for characteristic listener
     *
     * @param listener
     */
    public void setCharacteristicListener(ICharacteristicListener listener)
    {
        characteristicListener=listener;
    }
}
