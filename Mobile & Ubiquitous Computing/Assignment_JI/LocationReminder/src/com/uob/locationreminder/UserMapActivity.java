package com.uob.locationreminder;

import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserMapActivity extends Activity {

	private GoogleMap mapView;

	private LocationUpdateResponseReceiver locationUpdateReceiver;
	private ProximityResponseReceiver proxReceiver;

	private Marker userLocationMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_map);
		setTitle(getString(R.string.app_full_name));

		initilizeMap();
		LocationsHelper.loadMarkers(mapView, this);

		Intent myIntent=new Intent(this,BackgroundLocationManager.class);        
		this.startService(myIntent);

		IntentFilter filter = new IntentFilter(Constants.LOC_CHANGE_ACTION);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		locationUpdateReceiver = new LocationUpdateResponseReceiver();
		registerReceiver(locationUpdateReceiver, filter);

		IntentFilter filterProx = new IntentFilter(Constants.LOC_PORX_ACTION);
		filterProx.addCategory(Intent.CATEGORY_DEFAULT);
		proxReceiver = new ProximityResponseReceiver();
		registerReceiver(proxReceiver, filterProx);

		try{
		mapView.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				if(marker.equals(userLocationMarker)==false)
					LocationsHelper.openMapsForTag(getApplicationContext(),userLocationMarker.getPosition(),marker.getTitle());
			}
		});
		}catch(Exception e){
			
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.user_map, menu);
		return true;
	}

	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(locationUpdateReceiver);
		unregisterReceiver(proxReceiver);
		Intent proxIntent = new Intent(Constants.TASK_STOP);  
		sendBroadcast(proxIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationsHelper.loadMarkers(mapView,this);
		
		if(userLocationMarker!=null)
			addUserLocation(userLocationMarker.getPosition());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.location_setting:
			openLocationList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void openLocationList(){
		Intent i = new Intent(UserMapActivity.this,PlacemarkListActivity.class);
		startActivity(i);
	}

	private void initilizeMap() {

		if (mapView == null) {
			mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mapView == null) {
				Toast.makeText(getApplicationContext(),R.string.map_error, Toast.LENGTH_SHORT).show();
			}
		}
	}

	// Handler for receiving location updates.
	public class LocationUpdateResponseReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getDoubleExtra(Constants.LAT_TAG, 0.0)==0.0 && intent.getDoubleExtra(Constants.LNG_TAG, 0.0)==0.0)
				return;
			

			LatLng position = new LatLng(intent.getDoubleExtra(Constants.LAT_TAG, 0.0), intent.getDoubleExtra(Constants.LNG_TAG, 0.0));
			addUserLocation(position);
			
		}
	}

	void addUserLocation(LatLng position ) {
		
		if(userLocationMarker!=null){
			userLocationMarker.remove();
		}

		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
		MarkerOptions marker = new MarkerOptions();
		marker.position(position);
		marker.title(getString(R.string.user_current_location));
		marker.icon(bitmapDescriptor);
		userLocationMarker = mapView.addMarker(marker);
		LocationsHelper.zoomToLocation(position, 12, mapView);
	}
	
	
	// Handler for receiving proximity updates.
	public class ProximityResponseReceiver extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				String[] nearbyKeys = intent.getStringArrayExtra(Constants.NEAR_BY_ITEMS);
				Map<String, Map<String, String>> fetchedLocations;
				fetchedLocations = (Map<String, Map<String, String>>)LocationsHelper.fetchObjectFromFile(Constants.DATA_FILENAME,getApplicationContext());
				String coordinatesString = fetchedLocations.get(nearbyKeys[0]).get(Constants.COORD_TAG);
				LatLng position = LocationsHelper.getPositionFromString(coordinatesString);
				Toast.makeText(getApplicationContext(), getString(R.string.placemark_notify), Toast.LENGTH_SHORT).show();
				LocationsHelper.zoomToLocation(position, 12, mapView);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
