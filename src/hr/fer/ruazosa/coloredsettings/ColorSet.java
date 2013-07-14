package hr.fer.ruazosa.coloredsettings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Takes information from Toggle buttons from the interface
 * Saves settings into SharedPreferences
 * @author Damjan
 *
 */
public class ColorSet extends Activity implements View.OnClickListener {
	/**
	 * SharedPreferences name
	 */
	private static final String PREFS_NAME = "colorset";
	/**
	 * Color name
	 */
	String color;
	
	/**
	 * brightness value
	 */
	float barProgress = 0.5f;

	/**
	 * Reference to toggle button for WiFi
	 */
	ToggleButton tbW;
	/**
	 * Reference to toggle button for Sound
	 */
	ToggleButton tbS;
	/**
	 * Reference to toggle button for Media player
	 */
	ToggleButton tbMplayer;
	/**
	 * Reference to toggle button for Bluetooth
	 */
	ToggleButton tbBlue;
	/**
	 * Reference to toggle button for Map
	 */
	ToggleButton tbGps;
	/**
	 * Reference to toggle button for AutoSync
	 */
	ToggleButton tbAuto;
	
	/**
	 * Reference to toggle button for AutoBrightness
	 */
	ToggleButton autoBrightness;
	
	/**
	 * Reference to seekBar for brightness
	 */
	SeekBar seekBar;
	
	/**
	 * Reference to Save button
	 */
	Button save;
	
	boolean wifi = false;
	boolean sound = false;
	boolean mplayer = false;
	boolean bluetooth = false;
	boolean gps = false;
	boolean autoSync = false;
	boolean autoBright = false;
	
	EditText destination;
	
	/**
	 * On activity create, initializes listeners for toggle buttons on interface
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crna);

		color = getIntent().getStringExtra("color");
		
		TextView upperTextView = (TextView) findViewById(R.id.textView1);
		destination = (EditText) findViewById(R.id.destination);
		
		upperTextView.setText(color);

		tbW = (ToggleButton) findViewById(R.id.tb3);
		tbS = (ToggleButton) findViewById(R.id.tb4);
		tbMplayer = (ToggleButton) findViewById(R.id.tb5);
		tbBlue = (ToggleButton) findViewById(R.id.tb2);
		tbGps = (ToggleButton) findViewById(R.id.tb6);
		tbAuto = (ToggleButton) findViewById(R.id.tb7);
		autoBrightness = (ToggleButton) findViewById(R.id.autoBrightness);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		save = (Button) findViewById(R.id.button1);

		tbW.setOnClickListener(this);
		tbS.setOnClickListener(this);
		tbMplayer.setOnClickListener(this);
		tbBlue.setOnClickListener(this);
		tbGps.setOnClickListener(this);
		tbAuto.setOnClickListener(this);
		save.setOnClickListener(this);
		autoBrightness.setOnClickListener(this);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				barProgress = (float)progress/100;
				
			}
		});

		SharedPreferences colorset = getSharedPreferences(PREFS_NAME, 0);
		wifi = colorset.getBoolean(color + "-wifi", false);
		bluetooth = colorset.getBoolean(color + "-bluetooth", false);
		mplayer = colorset.getBoolean(color + "-mplayer", false);
		sound = colorset.getBoolean(color + "-sound", false);
		gps = colorset.getBoolean(color + "-gps", false);
		autoSync = colorset.getBoolean(color + "-auto", false);
		Float bright = colorset.getFloat(color + "-barProgress", 0.5f);
		String des = colorset.getString(color + "-gps-destination", "destination");
		autoBright = colorset.getBoolean(color + "-autoBrightness", false);
		
		tbW.setChecked(wifi);
		tbS.setChecked(sound);
		tbMplayer.setChecked(mplayer);
		tbBlue.setChecked(bluetooth);
		tbGps.setChecked(gps);
		tbAuto.setChecked(autoSync);
		autoBrightness.setChecked(autoBright);
		if (!autoBright)
		{
			seekBar.setProgress((int)(bright * 100));
		}
		else
			seekBar.setEnabled(false);
		
		if (!des.equals("destination")) destination.setText(des);
		
	}

	/**
	 * Invoked by user on Toggle button change
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tb3:
			if (tbW.isChecked())
				wifi = true;
			else
				wifi = false;
			break;

		case R.id.tb4:
			if (tbS.isChecked())
				sound = true;
			else
				sound = false;
			break;

		case R.id.tb5:
			if (tbMplayer.isChecked())
				mplayer = true;
			else
				mplayer = false;
			break;

		case R.id.tb2:
			if (tbBlue.isChecked())
				bluetooth = true;
			else
				bluetooth = false;
			break;

		case R.id.tb6:
			if (tbGps.isChecked())
				gps = true;
			else
				gps = false;
			break;

		case R.id.tb7:
			if (tbAuto.isChecked())
				autoSync = true;
			else
				autoSync = false;
			break;
		case R.id.autoBrightness:
			if (autoBrightness.isChecked())
			{
				autoBright = true;
				seekBar.setEnabled(false);
			}
			else
			{
				autoBright = false;
				seekBar.setEnabled(true);
			}
				
		case R.id.button1:
			String des = destination.getText().toString();
			SharedPreferences colorSet = this.getSharedPreferences(PREFS_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = colorSet.edit();
			editor.putBoolean(color + "-wifi", wifi);
			editor.putBoolean(color + "-sound", sound);
			editor.putBoolean(color + "-mplayer", mplayer);
			editor.putBoolean(color + "-bluetooth", bluetooth);
			editor.putBoolean(color + "-gps", gps);
			editor.putString(color + "-gps-destination", des);
			editor.putBoolean(color + "-auto", autoSync);
			editor.putFloat(color + "-barProgress", barProgress);
			editor.putBoolean(color + "-autoBrightness", autoBright);
			editor.commit();
			
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(getApplicationContext(),
			"Settings are saved!", duration);
			toast.show();
			
		}
	}
}