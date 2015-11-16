package com.operasoft.snowboard.services;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;

import com.operasoft.snowboard.R;

public class ServiceMonitor extends Service {
	private NotificationManager mNM;
	private int NOTIFICATION = 10100; //Any unique number for this notification
    SharedPreferences mPrefs = null;
    SharedPreferences.Editor mEd= null; 
    private Map<String,ServiceHook> hooks;
    boolean heartbeat=false;
    Notification mNotification;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate()
	{
		hooks=new HashMap<String,ServiceHook>();
		showNotification();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		loadPlugins();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		return START_STICKY;
	}
	
	private void loadPlugins()
	{
			
	      	Context settingsContext = null;
	      	String classname;
	        try {
	            settingsContext = createPackageContext("com.operasoft.snowboard",MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
	        } catch (NameNotFoundException e) {e.printStackTrace();}

	        mPrefs = settingsContext.getSharedPreferences("com.operasoft.snowboard.plugins.start", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
	        mEd = mPrefs.edit();
	        
	        // If we dont even have the GPS service listed in there, we need to create a new prefs repo
	        if(mPrefs.getString("GPSService", "")=="")
	        {
	        	mEd.putString("GPSService", "com.operasoft.snowboard.services.GPSPlugin");
	        	mEd.putString("ACEService", "com.operasoft.ace.services.AcePlugin");
	        	mEd.commit();
	        }
	        
	        Map<String,?> services=mPrefs.getAll();
	        
	        
	        for(String key: services.keySet())
	        {
	        	classname=(String)services.get(key);
	        	ServiceStart(classname);
	        	
	        	
	        }
	        //Intent i=new Intent("com.operasoft.ace.services.AceService");
	        //i.setClassName("com.operasoft.ace.services", "com.operasoft.ace.services.AceMonitorService");
	        //i.setComponent(new ComponentName("com.operasoft.ace.services", "com.operasoft.ace.services.AceMonitorService"));
	        //boolean b=bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	        startHearBeat();
	        
	        
	}
	
	public void ServiceStart(String strClass)
	{
		try{
		//@SuppressWarnings("rawtypes")
		//Class mClass=Class.forName(strClass);
		Intent intSRV = new Intent(strClass);
		bindService(intSRV, mConnection, Context.BIND_AUTO_CREATE);
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			//LocalBinder binder = (LocalBinder) service;
			String strClass=className.getClassName();
			ServiceHook hook=new ServiceHook(strClass,service);
			hooks.put(strClass, hook);
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHH!");
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			hooks.get(className.getClassName()).Bound=false;
		}
	};

	
	private void showNotification() {
		mNM=(NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
	    CharSequence text = "Snowboard Service Monitor";

	    // Set the icon, scrolling text and timestamp
	    mNotification = new Notification(R.drawable.ic_xirgo_1, text, System.currentTimeMillis());

	    // We dont have a pending event
	    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
	    
	    mNotification.icon=R.drawable.ic_xirgo_2;
	    // Set the info for the views that show in the notification panel.
	    mNotification.setLatestEventInfo(this, "Monitor Has Started", text, contentIntent);
	    mNotification.flags |= Notification.FLAG_NO_CLEAR;
	    
	    // Send the notification.
	    mNM.notify(NOTIFICATION, mNotification);
	}

	@Override
	public void onDestroy(){
		mNM=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(NOTIFICATION); //The same unique notification number.			
	}
	
	
	private void startHearBeat() {
		if(!heartbeat)
		new Thread() {
			@Override
			public void run() {
				int[] icons=new int[3];
				icons[0]=R.drawable.ic_xirgo_1;
				icons[1]=R.drawable.ic_xirgo_2;
				icons[2]=R.drawable.ic_xirgo_3;
				int icon=0;
				heartbeat=true;
				while (true) {
					try {
					Thread.sleep(5000);
					
			        for(String key: hooks.keySet())
			        {
			        	if(!hooks.get(key).IsAlive())
			        	{
			        		//hooks.remove(key);
			        		System.out.println("Service failed : "+key+" ... restarting.");
			        		ServiceStart(key);
			        	}
			        	
			        	
			        }
					
					
					// **************************************
					// Check if services are alive here
					// **************************************
					
				    mNotification.icon=icons[icon];
				    mNM.notify(NOTIFICATION, mNotification);
				    icon++;
				    if(icon>2) icon=0;
					} catch (Exception e) {
					}
				}
			}
		}.start();

	}

}
