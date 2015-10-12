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
package fr.bmartel.android.bluetooth.dotti;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import fr.bmartel.android.bluetooth.ICharacteristicListener;
import fr.bmartel.android.bluetooth.IDeviceInitListener;
import fr.bmartel.android.bluetooth.connection.BluetoothDeviceAbstr;
import fr.bmartel.android.bluetooth.connection.IBluetoothDeviceConn;
import fr.bmartel.android.bluetooth.listener.IPushListener;
import fr.bmartel.android.utils.ByteUtils;

/**
 * Dotti Bluetooth device management
 *
 * @author Bertrand Martel
 */
public class DottiDevice extends BluetoothDeviceAbstr implements IDottiDevice {

	private String TAG = DottiDevice.this.getClass().getName();

	private String dotti_service="0000fff0-0000-1000-8000-00805f9b34fb";
	private String dotti_charac="0000fff3-0000-1000-8000-00805f9b34fb";
	private String dotti_charac2="0000fff4-0000-1000-8000-00805f9b34fb";

	private ArrayList<IDeviceInitListener> initListenerList = new ArrayList<>();

	private boolean init = false;

	/**
	 * @param conn
	 */
	@SuppressLint("NewApi")
	public DottiDevice(IBluetoothDeviceConn conn) {
		super(conn);
		setCharacteristicListener(new ICharacteristicListener() {

			@Override
			public void onCharacteristicReadReceived(BluetoothGattCharacteristic charac) {

				if (charac.getUuid().toString().equals(dotti_charac)) {

					System.out.println(ByteUtils.byteArrayToStringMessage("test", charac.getValue(), '|'));

				} else if (charac.getUuid().toString().equals(dotti_charac2)) {

					System.out.println(ByteUtils.byteArrayToStringMessage("test2", charac.getValue(), '|'));

				}
			}

			@Override
			public void onCharacteristicChangeReceived(BluetoothGattCharacteristic charac) {

				if (charac.getUuid().toString().equals(dotti_charac)) {

					System.out.println(ByteUtils.byteArrayToStringMessage("test", charac.getValue(), '|'));

				} else if (charac.getUuid().toString().equals(dotti_charac2)) {

					System.out.println(ByteUtils.byteArrayToStringMessage("test2", charac.getValue(), '|'));

				}
			}

			@Override
			public void onCharacteristicWriteReceived(BluetoothGattCharacteristic charac) {

				if (charac.getUuid().toString().equals(dotti_charac)) {

					System.out.println(ByteUtils.byteArrayToStringMessage("test", charac.getValue(), '|'));

				} else if (charac.getUuid().toString().equals(dotti_charac2)) {

					System.out.println(ByteUtils.byteArrayToStringMessage("test2", charac.getValue(), '|'));

				}
			}
		});
	}

	private int m1c(String color) {
		if (!color.startsWith("#")) {
			color = "#" + color;
		}
		return Color.parseColor(color);
	}

	@Override
	public void init() {

		System.out.println("initializing dotti");

		conn.enableDisableNotification(UUID.fromString(dotti_service), UUID.fromString(dotti_charac), true);
		conn.enableDisableNotification(UUID.fromString(dotti_service), UUID.fromString(dotti_charac2), true);

		//getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 4, (byte) 5, (byte) 0},null);

		//getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 8, (byte) 0, (byte) 0, (byte) 0},null);

		/*
		int[] test =new int[]{m1c("000000"), m1c("FFFFFF"), m1c("FFFFFF"), m1c("FF0F00"), m1c("FF0F00"), m1c("FF0F00"), m1c("FF0F00"), m1c("FF1F00"), m1c("FFB000"), m1c("020003"), m1c("020003"), m1c("020003"), m1c("020003"), m1c("020003"), m1c("FFB000"), m1c("FFB000"), m1c("FFFC00"), m1c("020003"), m1c("FFFC00"), m1c("FFFC00"), m1c("FFFC00"), m1c("020003"), m1c("020003"), m1c("FFFC00"), m1c("69FF00"), m1c("020003"), m1c("69FF00"), m1c("69FF00"), m1c("69FF00"), m1c("69FF00"), m1c("020003"), m1c("69FF00"), m1c("00FF85"), m1c("020003"), m1c("00FF85"), m1c("00FF85"), m1c("00FF85"), m1c("00FF85"), m1c("020003"), m1c("00FF85"), m1c("008CFF"), m1c("020003"), m1c("008CFF"), m1c("008CFF"), m1c("008CFF"), m1c("020003"), m1c("020003"), m1c("008CFF"), m1c("1C00FF"), m1c("020003"), m1c("020003"), m1c("020003"), m1c("020003"), m1c("020003"), m1c("1C00FF"), m1c("1C00FF"), m1c("E300FF"), m1c("E300FF"), m1c("E300FF"), m1c("E300FF"), m1c("E300FF"), m1c("E300FF"), m1c("E300FF"), m1c("E30000")};
		setIcon(6,test);

		setOnOff(false);
		*/
		/*
		for (int i = 0;i < 8;i++) {
			showIcon(i,null);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		for (int i = 0; i  < initListenerList.size();i++){
			initListenerList.get(i).onInit();
		}

	}

	/**
	 * Write color to pixel id
	 *
	 * @param pixelId
	 *      from 0 to 63
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void drawPixel(int pixelId,int red,int green,int blue,IPushListener listener) {

		if (pixelId>=0 && pixelId <=63) {

			System.out.println("drawing " + ByteUtils.byteArrayToStringMessage("",new byte[]{(byte) 7, (byte) 2, (byte) (pixelId + 1), (byte) red, (byte) green, (byte) blue},'|'));

			getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 7, (byte) 2, (byte) (pixelId + 1), (byte) red, (byte) green, (byte) blue},listener);
		}
		else{
			Log.e(TAG,"Error pixel id must be between 0 and 63");
		}
	}

	/**
	 * set icon with given identifier from 0 to 7
	 *
	 * @param iconId
	 * @param pixels
	 */
	public void setIcon(int iconId,int[] pixels,IPushListener listener){

		if (iconId>=0 && iconId<=7){

			for (int i = 0; i  < pixels.length;i++) {
				int n = pixels[i];
				drawPixel(i,(byte)Color.red(n), (byte)Color.green(n), (byte)Color.blue(n),listener);
			}

			if (iconId==0){
				getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 7, (byte)0, (byte)0},listener);
			}
			else{
				int id = 0b10000000 + (iconId<<4);
				System.out.println("ID => " + id);
				getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 7, (byte) 2, (byte)id},listener);
			}
		}
		else{
			Log.e(TAG,"Error animation id must be between 0 and 7");
		}
	}

	/**
	 * Save current led matrive as icon with iconId
	 *
	 * @param iconId
	 */
	public void saveCurrentIcon(int iconId,IPushListener listener){

		if (iconId>=0 && iconId<=7){

			if (iconId==0){
				getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 7, (byte)0, (byte)0},listener);
			}
			else{
				int id = 0b10000000 + (iconId<<4);
				System.out.println("ID => " + id);
				getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 7, (byte) 2, (byte)id},listener);
			}
		}
		else{
			Log.e(TAG,"Error animation id must be between 0 and 7");
		}
	}

	/**
	 * display icon with icon id
	 *
	 * @param iconId
	 */
	public void showIcon(int iconId,IPushListener listener){

		if (iconId>=0 && iconId<=7){

			if (iconId==0){
				getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 8, (byte)0, (byte)0},listener);
			}
			else{
				int id = 0b10000000 + (iconId<<4);
				System.out.println("ID => " + id);
				getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 8, (byte) 2, (byte)id},listener);
			}
		}
		else{
			Log.e(TAG,"Error animation id must be between 0 and 7");
		}
	}

	/**
	 * display animation picture by id (0 to 7)
	 *
	 * @param animationId
	 */
	public void showAnimationPicture(int animationId,IPushListener listener){

		if (animationId>=0 && animationId<=7){

			byte id = (byte) (animationId<<4);
			System.out.println("animation id => " + id);

			getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 8, (byte)1, id},listener);
		}
		else{
			Log.e(TAG,"Error animation id must be between 0 and 7");
		}
	}

	/**
	 * switch led state
	 *
	 * @param state
	 */
	@Override
	public void setOnOff(boolean state,IPushListener listener) {

		if (state)
			getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 1, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},listener);
		else
			getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 1, (byte) 0x00, (byte) 0x00, (byte) 0x00},listener);

	}

	@Override
	public void setRGBColor(int red, int green, int blue,IPushListener listener) {

		getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 1, (byte)red, (byte)green, (byte)blue},listener);

	}

	@Override
	public void setLuminosityForColor(int value,int red,int green,int blue,IPushListener listener) {

		if (value>=0 && value<=100) {

			value=100-value;

			getConn().writeCharacteristic(dotti_service, dotti_charac, new byte[]{(byte) 6, (byte) 1, (byte) ((1f-value/100f)* red), (byte) ((1f-value/100f)*green), (byte) ((1f-value/100f)*blue)},listener);

		}
		else{
			Log.e(TAG, "Error luminosity must be set between 0 and 100");
		}
	}

	@Override
	public boolean isInit() {
		return init;
	}

	@Override
	public void addInitListener(IDeviceInitListener listener) {
		initListenerList.add(listener);
	}
}