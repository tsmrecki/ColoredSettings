package hr.fer.ruazosa.coloredsettings;

import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
/**
 * This is the MainActivity
 * Defines the interface for home screen
 * @author Tomislav
 *
 */
public class MainActivity extends Activity {

	private CameraPreview mPreview;
	private DrawOnTop mDrawOnTop;
	private View main;
	private boolean flash = false;
	private boolean loaded = false;

	private WifiManager wifiManager;
	private AudioManager audioManager;
	private BluetoothAdapter bluetoothAdapter;
	private WindowManager.LayoutParams windowManager;
	private LocationManager locationManager;
	private SoundPool soundPool;
	private int soundID;
	private int soundFailID;
	private String PREFS_NAME = "colorset";

	/**
	 * Initializes the main activity view and the resources that are used
	 * in application
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		getWindow().setFlags(
				WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW,
				WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);

		// Create our Preview view and set it as the content of our activity.
		// Create our DrawOnTop view.
		mDrawOnTop = new DrawOnTop(this);
		mPreview = new CameraPreview(this, mDrawOnTop);
		// FrameLayout preview = (FrameLayout)
		// findViewById(R.id.camera_preview);
		// preview.addView(mPreview);

		main = getLayoutInflater().inflate(R.layout.activity_main, null, false);

		setContentView(main);
		addContentView(mPreview, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		addContentView(mDrawOnTop, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Load the sound
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		
		soundID = soundPool.load(this, R.raw.capture, 1);
		soundFailID = soundPool.load(this, R.raw.fail, 1);

		
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		windowManager = getWindow().getAttributes();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
		
		main.setOnClickListener(new OnClickListener() {

			/**
			 * Invoked when user touches the screen
			 * Gets RGB values from DrawOnTop class instance and calls
			 * method in Color class instance to define scanned color
			 * If recognized, calls method change() to change settings
			 */
			@Override
			public void onClick(View arg0) {

				Color c = new Color();
				c.red = mDrawOnTop.imageRedMean;
				c.blue = mDrawOnTop.imageBlueMean;
				c.green = mDrawOnTop.imageGreenMean;
				Context context = getApplicationContext();

				String cname = c.returnColor(flash);

				if (cname != "Not recognized")
					change(cname);
				else {
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context,
							"Not recognized, try again!", duration);
					toast.show();
					playSound(soundFailID);
				}
			}

		});
	}

	/**
	 * On activity pause
	 */
	@Override
	public void onPause() {
		super.onPause();
		flash = false;

	}

	/**
	 * On activity resume
	 */
	@Override
	public void onResume() {
		super.onResume();

		mDrawOnTop = new DrawOnTop(this);
		mPreview = new CameraPreview(this, mDrawOnTop);
		setContentView(main);
		addContentView(mPreview, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		addContentView(mDrawOnTop, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

	}

	/**
	 * Load options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/** 
	 * Invoked when the user selects an item from the Menu 
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getNumericShortcut()) {
		case '2': {
			Camera.Parameters params = mPreview.mCamera.getParameters();
			if (!flash) {
				flash = !flash;
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			} else {
				flash = !flash;
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			}
			mPreview.mCamera.setParameters(params);

		}
			break;
		case '4': {
			Intent intent = new Intent(
					"hr.fer.ruazosa.coloredsettings.COLORSET");
			intent.putExtra("color", "White");
			startActivity(intent);
		}
			break;
		case '5': {
			Intent intent = new Intent(
					"hr.fer.ruazosa.coloredsettings.COLORSET");
			intent.putExtra("color", "Red");
			startActivity(intent);
		}
			break;

		case '6': {
			Intent intent = new Intent(
					"hr.fer.ruazosa.coloredsettings.COLORSET");
			intent.putExtra("color", "Blue");
			startActivity(intent);
		}
			break;
		case '7': {
			Intent intent = new Intent(
					"hr.fer.ruazosa.coloredsettings.COLORSET");
			intent.putExtra("color", "Green");
			startActivity(intent);
		}
			break;
		case '8': {
			Intent intent = new Intent(
					"hr.fer.ruazosa.coloredsettings.COLORSET");
			intent.putExtra("color", "Yellow");
			startActivity(intent);
		}
			break;
		case '9': {
			Intent intent = new Intent(
					"hr.fer.ruazosa.coloredsettings.COLORSET");
			intent.putExtra("color", "Black");
			startActivity(intent);
		}
			break;
		}
		return true;
	}
	
	/**
	 * Reads settings from SharedPreferences and activates them.
	 * Calls third party applications if set by user in settings.
	 * @param color Defines which color is scanned
	 */
	public void change(String color) {
		String popup = color;

		SharedPreferences colorset = getSharedPreferences(PREFS_NAME, 0);

		boolean wifi = colorset.getBoolean(color + "-wifi", false);
		boolean bluetooth = colorset.getBoolean(color + "-bluetooth", false);
		boolean mplayer = colorset.getBoolean(color + "-mplayer", false);
		boolean sound = colorset.getBoolean(color + "-sound", false);
		boolean gps = colorset.getBoolean(color + "-gps", false);
		boolean auto = colorset.getBoolean(color + "-auto", false);
		Float bright = colorset.getFloat(color + "-barProgress", 0.5f);
		String dest = colorset.getString(color + "-gps-dest", "dest");
		boolean autoBright = colorset.getBoolean(color + "-autoBrightness", false);
		int brightness = (int) (bright * 255);

		playSound(soundID);

		if (wifiManager.isWifiEnabled()) {
			if (wifi == false) {

				wifiManager.setWifiEnabled(false);
				popup += "\nWiFi OFF";
			}
		} else {
			if (wifi == true) {

				wifiManager.setWifiEnabled(true);
				popup += "\nWiFi ON";
			}
		}

		if (bluetoothAdapter.isEnabled() && bluetoothAdapter != null) {
			if (bluetooth == false) {

				bluetoothAdapter.disable();
				popup += "\nBluetooth OFF";
			}
		} else {
			if (bluetooth == true) {
				bluetoothAdapter.enable();
				popup += "\nBluetooth ON";
			}
		}

		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			if (sound == false) {

				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				popup += "\nSound Silent";
			}
		} else {
			if (sound == true) {

				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				popup += "\nSound Normal";
			}
		}

		float b = -1;
		try {
			b = android.provider.Settings.System.getInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {

		}

		if (ContentResolver.getMasterSyncAutomatically()) {
			if (auto == false) {
				ContentResolver.setMasterSyncAutomatically(false);
				popup += "\nAutoSync OFF";
			}

		} else {
			if (auto == true) {
				ContentResolver.setMasterSyncAutomatically(true);
				popup += "\nAutoSync ON";
			}
		}

		if (autoBright)
		{
			android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
			windowManager.screenBrightness = -1;
			getWindow().setAttributes(windowManager);
			popup += "\nBrightness mode AUTO";
		}
		else if (b != brightness) {
			android.provider.Settings.System.putInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS,
					brightness);
			windowManager.screenBrightness = bright;
			getWindow().setAttributes(windowManager);
			popup += "\nBrightness " + bright * 100 + "%";
		}
	
		Intent intent = null;
		Intent intent2 = null;
		
		if (gps == true) {
			if (dest.equals("dest") || dest.equals(""))
				try {
					intent = new Intent(android.content.Intent.ACTION_VIEW,
							Uri.parse("geo:0,0"));
					
					intent2 = new Intent( 
				                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
				    
				} catch (Exception e) {
				}

			else {
				try {
					intent = new Intent(android.content.Intent.ACTION_VIEW,
							Uri.parse("geo:0,0?q=" + dest));
					intent2 = new Intent( 
			                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
				} catch (Exception e) {
				}
			}

			startActivity(intent);
			startActivity(intent2);
		}
		
		if (mplayer) {
			try {
				try {
					// 8 <= API < 15
					String action = (String) MediaStore.class.getDeclaredField(
							"INTENT_ACTION_MUSIC_PLAYER").get(null);
					Intent it = new Intent(action);
					startActivity(it);
				} catch (Exception e) {
					// 15 <= API
					String category = (String) Intent.class.getDeclaredField(
							"CATEGORY_APP_MUSIC").get(null);
					Method method = Intent.class.getMethod(
							"makeMainSelectorActivity", String.class,
							String.class);
					Intent it = (Intent) method.invoke(null,
							Intent.ACTION_MAIN, category);
					startActivity(it);
				}
			} catch (Exception e) {
				popup += "\nPlayer not found!";
			}
		}

		if (popup == color)
			popup += "\nNo changes!";

		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, popup, duration);
		toast.show();

	}

	/**
	 * Invoked when user scans a color
	 *  
	 * @param sound ID of a sound to play
	 */
	private void playSound(int sound) {

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Is the sound loaded already?
		if (loaded) {
			soundPool.play(sound, volume, volume, 1, 0, 1f);
		}

	}

}
