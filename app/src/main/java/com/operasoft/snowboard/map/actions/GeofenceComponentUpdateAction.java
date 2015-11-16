package com.operasoft.snowboard.map.actions;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.R.color;
import com.operasoft.snowboard.database.Geofence;
import com.operasoft.snowboard.engine.GeofenceActionListener;
import com.operasoft.snowboard.engine.GeofenceManager;
import com.operasoft.snowboard.engine.GeofenceManager.Mode;

/**
 * This class is responsible for handling geofence entry/exit and update the 
 * GeofenceComponent on the Map based on the current data.
 * @author Christian
 *
 */
public class GeofenceComponentUpdateAction extends AbstractComponentUpdateAction implements GeofenceActionListener {

	static final private String TAG = "GeofenceMapActionHandler";
	
	private Activity activity;
	private GeofenceManager geofenceManager = GeofenceManager.getInstance();	
	
	private List<Geofence> activeGeofences = new ArrayList<Geofence>();
	private Geofence currentGeofence = null;

	private Animation blink;
	private MediaPlayer mediaPlayer;
	
	public GeofenceComponentUpdateAction(Activity activity) {
		this.activity = activity;
		blink = new AlphaAnimation(0.0f, 1.0f);
		blink.setDuration(250); //You can manage the time of the blink with this parameter
		blink.setStartOffset(20);
		blink.setRepeatMode(Animation.REVERSE);
		blink.setRepeatCount(Animation.INFINITE);		
		
		// Prepare the media player
		mediaPlayer = MediaPlayer.create(activity, R.raw.salt_over);
	}

	@Override
	public void geofenceEntered(Geofence geofence) {
		boolean updateComponent = false;
		
		if (currentGeofence == null) {
			currentGeofence = geofence;
			updateComponent = true;
		} else if (currentGeofence.getInputThreshold() < geofence.getInputThreshold()) {
			currentGeofence = geofence;
			updateComponent = true;
		}
		
		if (!activeGeofences.contains(geofence)) {
			activeGeofences.add(geofence);
		}
		
		if (updateComponent) {
			postComponentUpdate();
		}
	}

	@Override
	public void geofenceExited(Geofence geofence) {
		// Remove this geofence from our active list...
		activeGeofences.remove(geofence);
		
		// Check if we need to update the current geofence
		if ( (currentGeofence != null) && (currentGeofence.equals(geofence)) ) {
			if (activeGeofences.isEmpty()) {
				currentGeofence = null;
			} else {
				currentGeofence = activeGeofences.get(0);
				for (int i = 1; i < activeGeofences.size(); i++) {
					Geofence dto = activeGeofences.get(i);
					if (dto.getInputThreshold() > currentGeofence.getInputThreshold()) {
						currentGeofence = dto;
					}
				}
			}
	
			postComponentUpdate();
		}
	}
	
	@Override
	public void geofenceStatusUpdated(Geofence geofence) {
		postComponentUpdate();
	}
	
	/**
	 * This method runs in the UI thread to update the map_geofence_component.xml content
	 */
	@Override
	protected void updateComponent() {
		
		View component = activity.findViewById(R.id.geofenceComponent);
		if (component == null) {
			return;
		}
		
		// Check if we need to display the component or not
		if (geofenceManager.getMode() == Mode.DISABLED) {
			// We are not inside any Geofence
			component.setVisibility(View.GONE);
			return;
		}
		component.setVisibility(View.VISIBLE);

		// Get a copy of the currentGeofence object in case it is modified by
		// another thread
		Geofence geofence = currentGeofence;

		TextView name = (TextView) activity.findViewById(R.id.geofenceName);
		TextView current = (TextView) activity.findViewById(R.id.geofenceCurrent);
		TextView expected = (TextView) activity.findViewById(R.id.geofenceExpected);
		
		String material = geofenceManager.getAceMaterial();
		if (material == null) {
			material = "?";
		}
		int currentRate = geofenceManager.getAceRate();
		int expectedRate = 0;		
		
		if (geofence != null) {
			// We are current inside a geofence
			name.setText(material + " - " + geofence.getName());
			name.setTextColor(Color.BLACK);
			name.clearAnimation();

			expectedRate = (int) geofence.getInputThreshold();
			expected.setText(String.valueOf(expectedRate));
			expected.clearAnimation();
		} else {
			name.setText(material + " - ???");
			name.setTextColor(Color.BLACK);
			name.clearAnimation();
			
			expected.setText("???");
			expected.startAnimation(blink);
		}
		
		if (geofenceManager.getMode() == Mode.ACE_ERROR) {
			name.setText(material + " - ACE ERROR");
			name.setTextColor(Color.RED);
			name.startAnimation(blink);
		}		
		
		if (currentRate > expectedRate) {
			current.setTextColor(Color.RED);
		} else if (currentRate == expectedRate) {
			current.setTextColor(Color.BLACK);
		} else {
			current.setTextColor(Color.BLUE);
		}
		
		if (geofenceManager.getMode() == Mode.ACE_BLAST) {
			current.setText(String.valueOf(currentRate) + " - BLAST");
			current.startAnimation(blink);
		} else {
			current.setText(String.valueOf(currentRate));
			current.clearAnimation();
		}

		if ( (geofence != null) && (currentRate > expectedRate) && (geofenceManager.getMode() != Mode.ACE_BLAST) ) {
			playAlert();
		} else {
			stopAlert();
		}
	}
	
	private void playAlert() {
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
		}
	}
	
	private void stopAlert() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}
}
