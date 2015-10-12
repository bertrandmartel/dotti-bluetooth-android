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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.larswerkman.holocolorpicker.ColorPicker;

import fr.bmartel.android.bluetooth.dotti.IDottiDevice;
import fr.bmartel.android.bluetooth.listener.IPushListener;

/**
 * Flower Power device description activity
 *
 * @author Bertrand Martel
 */
public class DottiDeviceActivity extends Activity implements ColorPicker.OnColorChangedListener,SeekBar.OnSeekBarChangeListener, View.OnClickListener {

	/**
	 * dialog shown when user tap on icon slot
	 */
	private Dialog dialogIconSelect = null;

	private String TAG = DottiDeviceActivity.this.getClass().getName();

	/**
	 * BLE wrapper service
	 */
	private DottiBtService currentService = null;

	/**
	 * onOff state for Dotti device
	 */
	private boolean state = false;

	/**
	 * define if onPause has been triggered to go to pixel picker or icon setting dialog
	 */
	private boolean goToPixelPicker= false;

	/**
	 * define if one command has already been sent (we block until command completion reached)
	 */
	private boolean waitingForResponse = false;

	/**
	 * Dotti device object we can use api from
	 */
	private IDottiDevice device = null;

	/**
	 * device address
	 */
	private String address = "";

	/**
	 *  progress bar
	 */
	private ProgressDialog progress;

	/**
	* icon id seletected 
	*/
	private int iconId = -1;

	/**
	* Missed call icon which will be printed pixel by pixel
	*/
	String[] missedCall = new String[] {
			"#000000","#000000","#000000","#000000","#000000","#000000","#000000","#000000",
			"#000000","#000000","#FF0000","#FF0000","#FF0000","#FF0000","#000000","#000000",
			"#000000","#FF0000","#FF0000","#FF0000","#FF0000","#FF0000","#FF0000","#000000",
			"#FF0000","#FF0000","#000000","#000000","#000000","#000000","#FF0000","#FF0000",
			"#FF0000","#FF0000","#000000","#FF0000","#FF0000","#000000","#FF0000","#FF0000",
			"#000000","#000000","#FF0000","#FFFFFF","#FFFFFF","#FF0000","#000000","#000000",
			"#000000","#FF0000","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FF0000","#000000",
			"#000000","#FF0000","#FF0000","#FF0000","#FF0000","#FF0000","#FF0000","#000000"
	};

	/**
	* in call icon which will be printed pixel by pixel
	*/
	String[] inCall = new String[]{
			"#000000","#000000","#000000","#000000","#000000","#000000","#000000","#000000",
			"#000000","#000000","#00FF00","#00FF00","#00FF00","#00FF00","#000000","#000000",
			"#000000","#00FF00","#00FF00","#00FF00","#00FF00","#00FF00","#00FF00","#000000",
			"#00FF00","#00FF00","#000000","#000000","#000000","#000000","#00FF00","#00FF00",
			"#00FF00","#00FF00","#000000","#00FF00","#00FF00","#000000","#00FF00","#00FF00",
			"#000000","#000000","#00FF00","#FFFFFF","#FFFFFF","#00FF00","#000000","#000000",
			"#000000","#00FF00","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#00FF00","#000000",
			"#000000","#00FF00","#00FF00","#00FF00","#00FF00","#00FF00","#00FF00","#000000"
	};

	/**
	* message icon which will be printed pixel by pixel
	*/
	String[] message = new String[]{
			"#000000","#000000","#000000","#000000","#000000","#000000","#000000","#000000",
			"#1300FF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#1300FF",
			"#1300FF","#1300FF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#1300FF","#1300FF",
			"#1300FF","#FFFFFF","#1300FF","#FFFFFF","#FFFFFF","#1300FF","#FFFFFF","#1300FF",
			"#1300FF","#FFFFFF","#FFFFFF","#1300FF","#1300FF","#FFFFFF","#FFFFFF","#1300FF",
			"#1300FF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#1300FF",
			"#1300FF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#FFFFFF","#1300FF",
			"#000000","#000000","#000000","#000000","#000000","#000000","#000000","#000000"
	};


	/**
	 * Called when user click on icon slot
	 *
	 * @param v
	 * 		icon button view
	 */
	public void pictureClick(View v){
		
		Log.i(TAG,"picture click");

		String ressouceNme=getResources().getResourceEntryName(v.getId());
		iconId = Integer.parseInt(ressouceNme.substring(10));

		goToPixelPicker = true;
		dialogIconSelect.show();
	}

	/**
	 * Called when user click on pixel matrive
	 *
	 * @param v
	 * 		pixel button view
	 */
	public void pixelClick(View v) {

		String ressouceNme=getResources().getResourceEntryName(v.getId());

		int pixel = Integer.parseInt(ressouceNme.substring(6));

		Log.i(TAG, "click  pixel : " + pixel);

		if (device != null) {

			if (!waitingForResponse) {

				waitingForResponse = true;

				final Button buttonPick = (Button) findViewById(R.id.button_pick);
				ColorDrawable buttonColor = (ColorDrawable) buttonPick.getBackground();

				Button target = (Button) findViewById(v.getId());

				GradientDrawable gd = new GradientDrawable();
				gd.setColor(buttonColor.getColor());
				gd.setStroke(0, 0x00000000);
				gd.setCornerRadius(20);
				target.setBackgroundDrawable(gd);

				device.drawPixel(pixel, Color.red(buttonColor.getColor()), Color.green(buttonColor.getColor()), Color.blue(buttonColor.getColor()), new IPushListener() {
					@Override
					public void onPushFailure() {
						waitingForResponse = false;
					}

					@Override
					public void onPushSuccess() {
						waitingForResponse = false;
					}
				});
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dotti_device);

		Intent intent = getIntent();
		address = intent.getStringExtra("deviceAddr");
		String deviceName = intent.getStringExtra("deviceName");

		setTitle(deviceName.trim() + " [ " + address + " ] ");

		//init toggle button
		ToggleButton onOff =(ToggleButton) findViewById(R.id.ledButton);
		onOff.setOnClickListener(this);

		Button buttonPhoneRing = (Button) findViewById(R.id.buttonPhoneRing);

		buttonPhoneRing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printCustomIcon(inCall);
			}
		});

		Button buttonMissedCall = (Button) findViewById(R.id.buttonMissedCall);

		buttonMissedCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printCustomIcon(missedCall);
			}
		});

		Button buttonMessage = (Button) findViewById(R.id.buttonMessage);

		buttonMessage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				printCustomIcon(message);
			}
		});

		//init color picker
		ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
		picker.setOnColorChangedListener(this);
		picker.setShowOldCenterColor(false);

		dialogIconSelect = new Dialog(this);
		dialogIconSelect.setContentView(R.layout.icon_select);
		dialogIconSelect.setTitle("Icon settings");

		Button displayIcon = (Button) dialogIconSelect.findViewById(R.id.icon_display);
		Button saveIcon = (Button) dialogIconSelect.findViewById(R.id.icon_save);

		displayIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				goToPixelPicker = false;

				if (iconId!=-1) {

					waitingForResponse = true;
					device.showIcon(iconId, new IPushListener() {
						@Override
						public void onPushFailure() {
							waitingForResponse = false;
						}

						@Override
						public void onPushSuccess() {
							waitingForResponse = false;
						}
					});
				}
				else{
					Log.e(TAG,"Error icon id inconsistent");
				}
				dialogIconSelect.dismiss();

			}
		});

		saveIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				goToPixelPicker = false;

				if (iconId!=-1) {
					waitingForResponse = true;
					device.saveCurrentIcon(iconId, new IPushListener() {
						@Override
						public void onPushFailure() {
							waitingForResponse = false;
						}

						@Override
						public void onPushSuccess() {
							waitingForResponse = false;
						}
					});
				}
				else{
					Log.e(TAG,"Error icon id inconsistent");
				}
				dialogIconSelect.dismiss();
			}
		});

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.pixel_picker);
		dialog.setTitle("Pick a color");

		final Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToPixelPicker = false;
				dialog.dismiss();
			}
		});

		final Button buttonPick = (Button) findViewById(R.id.button_pick);
		buttonPick.setBackgroundColor(Color.parseColor("#FFFFFF"));

		buttonPick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToPixelPicker = true;
				dialog.show();
			}
		});

		ColorPicker pixelPicker = (ColorPicker) dialog.findViewById(R.id.pixelPicker);
		pixelPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
			@Override
			public void onColorChanged(final int i) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialogButton.setBackgroundColor(i);
						buttonPick.setBackgroundColor(i);
					}
				});
			}
		});

		//init seekbar
		SeekBar luminosityBar = (SeekBar) findViewById(R.id.intensity_bar);
		luminosityBar.setOnSeekBarChangeListener(this);

		Intent intentMain = new Intent(this, DottiBtService.class);

		// bind the service to current activity and create it if it didnt exist before
		startService(intentMain);
		bindService(intentMain, mServiceConnection, BIND_AUTO_CREATE);
	}

	/**
	 * Manage Bluetooth Service lifecycle
	 */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(final ComponentName name, IBinder service) {

			System.out.println("Connected to service");

			currentService = ((DottiBtService.LocalBinder) service).getService();

			if (currentService.getConnectionList().get(address) != null) {

				if (currentService.getConnectionList().get(address).getDevice() instanceof IDottiDevice) {

					device = (IDottiDevice) currentService.getConnectionList().get(address).getDevice();


				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	/**
	 * Set a color for each pixel in matrice (background of pixel button)
	 *
	 * @param color
	 */
	public void changeFullMatrice(final int color){
		
		for (int i = 0 ; i< 64;i++){

			final int resourceId = this.getResources().getIdentifier("button" + i, "id", this.getPackageName());

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Button pixel = (Button) findViewById(resourceId);
					GradientDrawable gd = new GradientDrawable();
					gd.setColor(color);
					gd.setStroke(0, 0x00000000);
					gd.setCornerRadius(20);
					pixel.setBackgroundDrawable(gd);
				}
			});
		}
	}

	/**
	 *  Send a matrice pixel to Bluetooth device
	 *
	 * @param pixels
	 */
	public void printCustomIcon(String[] pixels){

		if (device != null) {

			if (!waitingForResponse) {

				waitingForResponse = true;

				for (int i = 0; i < 63; i++) {

					int color = Color.parseColor(pixels[i]);

					device.drawPixel(i, Color.red(color), Color.green(color), Color.blue(color), new IPushListener() {
						@Override
						public void onPushFailure() {
						}

						@Override
						public void onPushSuccess() {
						}
					});
				}

				int color = Color.parseColor(pixels[63]);

				device.drawPixel(63, Color.red(color), Color.green(color), Color.blue(color), new IPushListener() {
					@Override
					public void onPushFailure() {
						waitingForResponse=false;
					}

					@Override
					public void onPushSuccess() {
						waitingForResponse=false;
					}
				});
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//unregister receiver on pause
		//unregisterReceiver(mGattUpdateReceiver);

		if (!goToPixelPicker) {
			try {
				// unregister receiver or you will have strong exception
				unbindService(mServiceConnection);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onColorChanged(final int i) {

		Log.i(TAG, "color changed : " +  Color.red(i) + " - " + Color.green(i) + " - " + Color.blue(i));
		if (device!=null){

			if (!waitingForResponse) {

				waitingForResponse=true;

				device.setRGBColor(Color.red(i), Color.green(i), Color.blue(i), new IPushListener() {
					@Override
					public void onPushFailure() {
						waitingForResponse = false;
					}

					@Override
					public void onPushSuccess() {

						changeFullMatrice(i);
						waitingForResponse = false;
					}
				});
			}
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

		Log.i(TAG,"intensity changed : " + progress);

		if (device!=null){

			if (!waitingForResponse) {

				waitingForResponse=true;

				final ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
				device.setLuminosityForColor(progress, Color.red(picker.getColor()), Color.green(picker.getColor()), Color.blue(picker.getColor()), new IPushListener() {
					@Override
					public void onPushFailure() {
						waitingForResponse = false;
					}

					@Override
					public void onPushSuccess() {

						int red = (int) ((1f-(100-progress)/100f)*Color.red(picker.getColor()));
						int green = (int) ((1f-(100-progress)/100f)*Color.green(picker.getColor()));
						int blue = (int) ((1f-(100-progress)/100f)*Color.blue(picker.getColor()));

						changeFullMatrice(Color.rgb(red, green, blue));

						waitingForResponse = false;

					}
				});

			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onClick(View v) {
		Log.i(TAG,"click on button");
		if (device!=null){

			if (!waitingForResponse) {

				waitingForResponse = true;
				device.setOnOff(!state, new IPushListener() {
					@Override
					public void onPushFailure() {
						waitingForResponse = false;
					}

					@Override
					public void onPushSuccess() {

						if (state){
							changeFullMatrice(Color.parseColor("#FFFFFF"));
						}
						else{
							changeFullMatrice(Color.parseColor("#000000"));
						}

						waitingForResponse = false;
					}
				});
				state = !state;

			}else{

				ToggleButton onOff =(ToggleButton) findViewById(R.id.ledButton);
				onOff.toggle();

			}
		}
	}
}
