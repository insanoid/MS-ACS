package com.uob.locationreminder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddLocationActivity extends Activity {
	private GoogleMap mapView;
	private SeekBar radiusSeekBar;
	private EditText placeTitle;
	private LatLng currentLatLng;
	private Button saveButton;
	private TextView radiusValueText;
	private String title;


	Map<String, Map<String, String>> fetchedLocations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_location);
		Toast.makeText(getApplicationContext(),getString(R.string.add_placemark_hint), Toast.LENGTH_SHORT).show();

		radiusSeekBar = (SeekBar)findViewById(R.id.radiusSeekBar);
		placeTitle = (EditText)findViewById(R.id.placeNameEditText);
		saveButton = (Button)findViewById(R.id.addButton);
		radiusValueText = (TextView)findViewById(R.id.radiusValueText);

		initilizeMap();

		Intent myIntent = getIntent();
		title = myIntent.getStringExtra(Constants.NAME_TAG);

		updateUI();


		mapView.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				currentLatLng = point;
				mapView.clear();
				mapView.addMarker(new MarkerOptions().position(point));

			}
		});

		saveButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				if(title!=null){
					if(title.equalsIgnoreCase(placeTitle.getText().toString())){
						fetchedLocations.get(title).put(Constants.COORD_TAG, currentLatLng.latitude+","+currentLatLng.longitude);
						fetchedLocations.get(title).put(Constants.RADIUS_TAG, String.valueOf(radiusSeekBar.getProgress()));
					}else{
						fetchedLocations.remove(title);
						Map<String, String> newValue = new HashMap<String, String>();
						newValue.put(Constants.COORD_TAG, currentLatLng.latitude+","+currentLatLng.longitude);
						newValue.put(Constants.RADIUS_TAG, String.valueOf(radiusSeekBar.getProgress()));
						fetchedLocations.put(placeTitle.getText().toString(),newValue);
					}

					try {
						LocationsHelper.saveObjectToFile((Object)fetchedLocations, Constants.DATA_FILENAME,getApplicationContext());
					} catch (IOException e) {
						e.printStackTrace();
					}
					notifyService();
					finish();
				}else{

					if(currentLatLng !=null && placeTitle.getText().toString().length()>0){
						try {
							fetchedLocations = (Map<String, Map<String, String>>)LocationsHelper.fetchObjectFromFile( Constants.DATA_FILENAME,getApplicationContext());

							if(fetchedLocations==null){
								fetchedLocations = new HashMap<String, Map<String, String>>();
							}

							Map<String, String> newValue = new HashMap<String, String>();
							newValue.put(Constants.COORD_TAG, currentLatLng.latitude+","+currentLatLng.longitude);
							newValue.put(Constants.RADIUS_TAG, String.valueOf(radiusSeekBar.getProgress()));
							fetchedLocations.put(placeTitle.getText().toString(),newValue);
							LocationsHelper.saveObjectToFile((Object)fetchedLocations, Constants.DATA_FILENAME,getApplicationContext());
						} catch (Exception e) {
							e.printStackTrace();
						}
						notifyService();
						finish();
					}else{
						Toast.makeText(getApplicationContext(),getString(R.string.add_placemark_warning), Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		radiusSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
		{
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				// TODO Auto-generated method stub
				radiusValueText.setText(getString(R.string.monitoring_area)+String.valueOf(progress)+"m");
			}

			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub
			}
		});

	}

	void notifyService() {

		//		Intent currentIntent = new Intent(Constants.PROX_POINTS_EDIT); 
		//		sendBroadcast(currentIntent);

		Intent myIntent=new Intent(this,BackgroundLocationManager.class);        
		this.startService(myIntent);
	}

	@SuppressWarnings("unchecked")
	void updateUI() {
		if(title!=null){
			try {
				fetchedLocations = (Map<String, Map<String, String>>)LocationsHelper.fetchObjectFromFile(Constants.DATA_FILENAME,this);

				String coordinatesString = fetchedLocations.get(title).get(Constants.COORD_TAG);
				int radius = Integer.parseInt(fetchedLocations.get(title).get(Constants.RADIUS_TAG));
				LatLng position = LocationsHelper.getPositionFromString(coordinatesString);


				mapView.addMarker(new MarkerOptions().position(position));
				LocationsHelper.zoomToLocation(position, 12, mapView);
				currentLatLng = position;

				placeTitle.setText(title);
				radiusSeekBar.setProgress(radius);

				radiusValueText.setText(getString(R.string.monitoring_area)+String.valueOf(radius)+"m");
				saveButton.setText(getString(R.string.save));
				setTitle(getString(R.string.edit_poi));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			LocationsHelper.zoomToLocation(LocationsHelper.getLatestLocation(getApplicationContext()), 12, mapView);
			setTitle(getString(R.string.add_poi));
			radiusSeekBar.setProgress(25);
			radiusValueText.setText(getString(R.string.monitoring_area)+String.valueOf(25)+"m");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if(title!=null){
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.add_locations_menu, menu);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.delete_location:
			deleteCurrentItem();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("unchecked")
	void deleteCurrentItem() {
		try {
			fetchedLocations = (Map<String, Map<String, String>>)LocationsHelper.fetchObjectFromFile(Constants.DATA_FILENAME,getApplicationContext());
			fetchedLocations.remove(title);
			LocationsHelper.saveObjectToFile((Object)fetchedLocations, Constants.DATA_FILENAME,getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}

	private void initilizeMap() {
		if (mapView == null) {
			mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.addMapView)).getMap();

			// check if map is created successfully or not
			if (mapView == null) {
				Toast.makeText(getApplicationContext(),getString(R.string.map_error), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
