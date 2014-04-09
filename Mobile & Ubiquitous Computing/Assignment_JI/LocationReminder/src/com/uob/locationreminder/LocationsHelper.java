package com.uob.locationreminder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationsHelper {

	public static void saveObjectToFile(Object obj, String filename, Context ctx)
			throws IOException {
		File file = new File(ctx.getFilesDir(), filename);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				fileOutputStream);

		objectOutputStream.writeObject(obj);
		objectOutputStream.close();
	}

	public static Object fetchObjectFromFile(String filename, Context ctx)
			throws IOException, ClassNotFoundException {
		File file = new File(ctx.getFilesDir(), filename);
		if (file.exists() == true) {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					fileInputStream);

			Object obj = objectInputStream.readObject();
			objectInputStream.close();

			return obj;
		} else {
			return null;
		}
	}

	
	public static boolean isAnUpdateRequired(Location location) {
		
		long timeDelta = new Date().getTime()- location.getTime();
		boolean required = timeDelta > Constants.kMinimumTime;
		return required;
	}
	
	//Google function for better location verification.
	public static boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > Constants.kMinimumTime;
		boolean isSignificantlyOlder = timeDelta < -Constants.kMinimumTime;
		;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > Constants.kMinimumDistance;
		boolean isMoreAccurate = accuracyDelta < Constants.kMinimumDistance;
		boolean isSignificantlyLessAccurate = accuracyDelta > 400;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	public static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public static void loadMarkers(GoogleMap mapView, Context ctx) {
		try {
			mapView.clear();
			@SuppressWarnings("unchecked")
			Map<String, Map<String, String>> fetchedLocations = (Map<String, Map<String, String>>) LocationsHelper
					.fetchObjectFromFile(Constants.DATA_FILENAME, ctx);
			Log.d("ERRRRRR", Arrays.deepToString(fetchedLocations.keySet().toArray()));
			for (String key : fetchedLocations.keySet()) {
				String coordinatesString = fetchedLocations.get(key).get(
						Constants.COORD_TAG);
				LatLng position = getPositionFromString(coordinatesString);
				addMarker(position, key, mapView);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void openMapsForTag(Context ctx, LatLng userLoc, String key) {
		try {

			@SuppressWarnings("unchecked")
			Map<String, Map<String, String>> fetchedLocations = (Map<String, Map<String, String>>) LocationsHelper
					.fetchObjectFromFile(Constants.DATA_FILENAME, ctx);
				String coordinatesString = fetchedLocations.get(key).get(Constants.COORD_TAG);
				
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					    Uri.parse("http://maps.google.com/maps?saddr="+String.valueOf(userLoc.latitude)+","+String.valueOf(userLoc.longitude)+"&daddr="+coordinatesString));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void addMarker(LatLng position, String title,GoogleMap mapView) {
		
		MarkerOptions marker = new MarkerOptions();
		marker.position(position);
		marker.title(title);
		mapView.addMarker(marker);
	}

	public static void zoomToLocation(LatLng position, int zoomLevel,
			GoogleMap mapView) {

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(position).zoom(zoomLevel).build();

		mapView.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

	}

	public static void zoomToLocation(Location position, int zoomLevel,
			GoogleMap mapView) {
		zoomToLocation(
				new LatLng(position.getLatitude(), position.getLongitude()),
				zoomLevel, mapView);

	}

	public static Location getLatestLocation(Context context) {
		
		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = manager.getBestProvider(criteria, true);
		Location bestLocation;
		if (provider != null)
			bestLocation = manager.getLastKnownLocation(provider);
		else
			bestLocation = null;
		Location latestLocation = getLatest(bestLocation,
				manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		latestLocation = getLatest(latestLocation,
				manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		latestLocation = getLatest(latestLocation,
				manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
		return latestLocation;
	}

	private static Location getLatest(final Location location1,
			final Location location2) {
		if (location1 == null)
			return location2;

		if (location2 == null)
			return location1;

		if (location2.getTime() > location1.getTime())
			return location2;
		else
			return location1;
	}

	public static HashMap<String, String> checkProximity(Location loc,
			Context ctx) {

		HashMap<String, String> returnValues = new HashMap<String, String>();

		try {
			@SuppressWarnings("unchecked")
			Map<String, Map<String, String>> fetchedLocations = (Map<String, Map<String, String>>) fetchObjectFromFile(
					Constants.DATA_FILENAME, ctx);

			for (String k : fetchedLocations.keySet()) {
				String coordinatesString = fetchedLocations.get(k).get(
						Constants.COORD_TAG);
				float radius = Integer.parseInt(fetchedLocations.get(k).get(
						Constants.RADIUS_TAG)) > 0 ? (float) Integer
						.parseInt(fetchedLocations.get(k).get(Constants.RADIUS_TAG)) : 100f;
				LatLng position = getPositionFromString(coordinatesString);

				Location place = new Location(k);
				place.setLatitude(position.latitude);
				place.setLongitude(position.longitude);
				float currentDistance = loc.distanceTo(place);

				if (currentDistance <= radius) {
					returnValues.put(k, String.valueOf(currentDistance));
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValues;
	}

	public static void createNotification(final String title,
			final String[] content, Drawable icon, int tag, final Context ctx) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(title);
		
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		
		inboxStyle.setBigContentTitle(title +" - "+ ctx.getString(R.string.placemarks));

		// Moves events into the big view
		for (int i = 0; i < content.length; i++) {
			inboxStyle.addLine(content[i]);
		}
		if (content.length >= 8) {
			inboxStyle.setSummaryText("" + (content.length - 7) +ctx.getString(R.string.more));
		}

		Intent intent = new Intent(ctx, UserMapActivity.class);
		PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent,
				Intent.FLAG_ACTIVITY_CLEAR_TASK);

		mBuilder.setTicker(content.length + ctx.getString(R.string.notification_header));
		mBuilder.setStyle(inboxStyle);
		mBuilder.setNumber(content.length);
		mBuilder.setContentIntent(pi);
		mBuilder.setAutoCancel(true);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setLargeIcon(drawableToBitmap(icon));
		NotificationManager mNotifyMgr = (NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(tag, mBuilder.build());

	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static LatLng getPositionFromString(String coordinatesString) {
		String[] coordinates = coordinatesString.split(",");
		double latitude = Double.parseDouble(coordinates[0]);
		double longitude = Double.parseDouble(coordinates[1]);
		LatLng position = new LatLng(latitude, longitude);
		return position;
	}
	
	public static Boolean isActivityOnTop(Context ctx) {


		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		boolean isActivityFound = false;

		if (services.get(0).topActivity.getPackageName().toString()
				.equalsIgnoreCase(ctx.getPackageName().toString())) {
			isActivityFound = true;
		}

		return isActivityFound;
	}
}
