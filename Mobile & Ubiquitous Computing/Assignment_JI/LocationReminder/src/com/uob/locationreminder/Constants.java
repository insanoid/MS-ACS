package com.uob.locationreminder;

public class Constants {

	
	public static String SERVICE_TAG = "BACKGROUND_SERVICE_LOCATION";
	public static String LOC_CHANGE_ACTION = "com.uob.locationreminder.LOCATION_CHANGED";
	public static String LOC_PORX_ACTION = "com.uob.locationreminder.PROXIMITY_ALERT";
	public static String PROX_POINTS_EDIT = "com.uob.locationreminder.POINTS_EDITED";
	
	public static String TASK_STOP = "STOP";
	public static String TASK_RESUME = "RESUME";
	public static String TASK_PAUSE = "PAUSE";
	
	public static long kMinimumTime = 40*1000L;
	public static long kPollingMinimumTime = 1*1000L;
	public static long ALARM_RELOAD_TIME = 25*1000L;
	public static long kMinimumDistance = 1;
	
	public static String DATA_FILENAME = "locations.dat";
	public static String COORD_TAG = "Coordinates";
	public static String NAME_TAG = "Name";
	public static String LAT_TAG = "Latitude";
	public static String LNG_TAG = "Longitude";
	public static String RADIUS_TAG = "Radius";
	
	public static String NEAR_BY_ITEMS = "Near_By";
	
}
