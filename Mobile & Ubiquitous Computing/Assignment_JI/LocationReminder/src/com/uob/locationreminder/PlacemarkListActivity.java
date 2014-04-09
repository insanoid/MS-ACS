package com.uob.locationreminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.model.LatLng;

public class PlacemarkListActivity extends Activity {

	private ListView locationList;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.location_list);
		locationList = (ListView)findViewById(R.id.locationList);
		setTitle(getString(R.string.placemarks));

		loadListView();

	}

	@SuppressWarnings("unchecked")
	void loadListView() {

		Map<String, Map<String, String>> fetchedLocations;
		try {
			fetchedLocations = (Map<String, Map<String, String>>)LocationsHelper.fetchObjectFromFile(Constants.DATA_FILENAME,this);
			final String[] locations = new String[fetchedLocations.size()];
			List<Map<String, String>> locationInfo = new ArrayList<Map<String, String>>();
			int ptr = 0;
			for(String key: fetchedLocations.keySet()){
				locations[ptr++] = key;
				
				String coordinatesString = fetchedLocations.get(key).get(Constants.COORD_TAG);
				LatLng position = LocationsHelper.getPositionFromString(coordinatesString);
				Location place = new Location(key);
				place.setLatitude(position.latitude);
				place.setLongitude(position.longitude);
				float currentDistance = LocationsHelper.getLatestLocation(this).distanceTo(place);
				
				Map<String, String> curr = new HashMap<String, String>(2);
				curr.put(Constants.NAME_TAG, key);
				curr.put(Constants.COORD_TAG,(currentDistance>=1000?String.format("%.2fkm", currentDistance/1000f):String.format("%.2fm", currentDistance))+" "+getString(R.string.away));
				locationInfo.add(curr);
			}

			SimpleAdapter adapter = new SimpleAdapter(this, locationInfo,
					android.R.layout.simple_list_item_2, 
					new String[] {Constants.NAME_TAG, Constants.COORD_TAG}, 
					new int[] {android.R.id.text1, android.R.id.text2 });

			locationList.setAdapter(adapter);
			locationList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					showpopup(locations[arg2]);

				}

			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_setting:
			showpopup(null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void showpopup(String title){

		Intent i = new Intent(PlacemarkListActivity.this,AddLocationActivity.class);
		if(title!=null){
			i.putExtra(Constants.NAME_TAG, title);
		}
		startActivity(i);
	}




}
