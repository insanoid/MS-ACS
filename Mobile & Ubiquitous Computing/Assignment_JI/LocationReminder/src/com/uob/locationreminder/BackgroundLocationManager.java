package com.uob.locationreminder;

import java.util.HashMap;
import java.util.Timer;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BackgroundLocationManager extends IntentService {

	LocationListener locationListner;
	LocationManager locationManager;
	Location currentLocation;
	PlaceMarkEditReciever placeMarkReciever;
	KillReciever killReciever;
	Timer timer;

	public BackgroundLocationManager(String name) {
		super(name);
	}

	public BackgroundLocationManager() {
		super("Location Monitor Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {


	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);
		setAlarmToRedoLoad();
		return Service.START_STICKY;
	}

	/* Reloading Background Service */
	private void setAlarmToRedoLoad() {
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + Constants.ALARM_RELOAD_TIME,makeSelfPendingIntent(getApplicationContext()));
	}

	private static PendingIntent makeSelfPendingIntent(Context context) {

		PendingIntent intent = PendingIntent.getService(context, 0, makeSelfIntent(context), 0);
		return intent;
	}

	private static Intent makeSelfIntent(Context context) {
		Intent intent = new Intent(context, BackgroundLocationManager.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setAction("action_fetch");
		return intent;
	}
	/**/

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(Constants.SERVICE_TAG,"Revived: Background Location Monitor");

		IntentFilter filter = new IntentFilter(Constants.PROX_POINTS_EDIT);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		placeMarkReciever = new PlaceMarkEditReciever();
		registerReceiver(placeMarkReciever, filter);

		IntentFilter filterStop = new IntentFilter(Constants.TASK_STOP);
		filterStop.addCategory(Intent.CATEGORY_DEFAULT);
		killReciever = new KillReciever();
		registerReceiver(killReciever, filter);

		boolean isGPSEnabled = false;
		boolean isNetworkEnabled = false;

		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListner = new MyLocationListener();
   
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(isGPSEnabled || isNetworkEnabled)
		{
			Location newLocation = LocationsHelper.getLatestLocation(this);

			try{
				if(LocationsHelper.isAnUpdateRequired(newLocation)){
					notifyLocationChange(newLocation);
					locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListner, null);

				}else{
					notifyLocationChange(newLocation, false, true);
				}
			}catch(Exception e){
				notifyLocationChange(newLocation, false, true);
			}

		}else{
			Toast.makeText(this, "Failed to access location service: No permission.", Toast.LENGTH_SHORT).show();
		}

	}

	public void onDestroy() {
		Log.i(Constants.SERVICE_TAG,"Destroyed: Background Location Monitor");
		removeAllRecievers();
	}

	void removeAllRecievers() {
		locationManager.removeUpdates(locationListner); 
		unregisterReceiver(placeMarkReciever);
		unregisterReceiver(killReciever);
	}

	public void onStart(Intent intent, int startid) {
		Log.i(Constants.SERVICE_TAG,"Started: Background Location Monitor");
	}

	public void notifyLocationChange(Location loc) {
		notifyLocationChange(loc, false);
	}

	public void notifyLocationChange(Location loc, Boolean isForceLoad) {
		notifyLocationChange(loc, isForceLoad, false);
	}

	public void notifyLocationChange(Location loc, Boolean isForceLoad, Boolean killAfterDone) {

		if(LocationsHelper.isBetterLocation(loc, currentLocation) || isForceLoad || currentLocation==null){
			Intent currentIntent = new Intent(Constants.LOC_CHANGE_ACTION);  
			currentIntent.putExtra(Constants.LAT_TAG, loc.getLatitude());
			currentIntent.putExtra(Constants.LNG_TAG, loc.getLongitude()); 
			sendBroadcast(currentIntent);

			HashMap<String, String> nearbyLocations = LocationsHelper.checkProximity(loc, getApplicationContext());

			if(nearbyLocations.size()>0){
				String[] places = new String[nearbyLocations.size()];
				int i = 0;
				for(String k:nearbyLocations.keySet()){
					places[i] = k+ " ("+String.format("%.2f",Double.parseDouble(nearbyLocations.get(k)))+"m)";
					i++;
				}
				LocationsHelper.createNotification(getString(R.string.app_full_name), places,  getResources().getDrawable(R.drawable.ic_map), 1, getApplicationContext());


				Intent proxIntent = new Intent(Constants.LOC_PORX_ACTION);  
				proxIntent.putExtra(Constants.NEAR_BY_ITEMS,nearbyLocations.keySet().toArray());
				sendBroadcast(proxIntent);
			}

			if(killAfterDone){
				//Toast.makeText(this, "Background Service: Done.", Toast.LENGTH_SHORT).show();
				stopService(new Intent(this, BackgroundLocationManager.class));
			}else{
				//Toast.makeText(this, "Background Service: Done.", Toast.LENGTH_SHORT).show();
				stopService(new Intent(this, BackgroundLocationManager.class));
			}

			currentLocation = loc;
		}


	}



	//	Handler to handle Location Listening.
	public class MyLocationListener implements LocationListener
	{

		public void onLocationChanged(Location loc)
		{
			if(loc!=null){
				Log.i(Constants.SERVICE_TAG,"NEW LOCACTION " +"LAT = " + loc.getLatitude() +"LNG = " + loc.getLongitude());

				if(LocationsHelper.isActivityOnTop(getApplicationContext())){
					notifyLocationChange(loc);
				}else{
					notifyLocationChange(loc,false,true);
				}

			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}


	}


	// Handler for receiving changes in points.
	public class PlaceMarkEditReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			notifyLocationChange(LocationsHelper.getLatestLocation(getApplicationContext()),true);

		}
	}

	// Handler for receiving changes in points.
	public class KillReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			stopService(new Intent(getApplicationContext(),BackgroundLocationManager.class));

		}
	}

	// Handler for receiving changes in points.
	public class PauseReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			locationManager.removeUpdates(locationListner);
			locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,Constants.kMinimumTime,Constants.kMinimumDistance,locationListner);
		}
	}

	// Handler for receiving changes in points.
	public class ResumeReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			locationManager.removeUpdates(locationListner);
			locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,Constants.kMinimumTime,Constants.kMinimumDistance,locationListner);
		}
	}


}
