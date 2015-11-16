package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestEventListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class PoiAlarmManager implements PointOfInterestEventListener, Runnable {

	private final PointOfInterestManager poiManager;
	AssetFileDescriptor afd;
	private final List<PointOfInterest> receivedSa = new ArrayList<PointOfInterest>();

	private MediaPlayer mediaPlayer = null;
	private boolean running = false;

	private final Context mContext;

	/**
	 * The singleton instance
	 */
	static private PoiAlarmManager instance_s;

	/**
	 * Singleton pattern. This makes sure we have only one instance of this class instantiated in the entire application.
	 */
	synchronized static public PoiAlarmManager getInstance(Context ctx) {
		if (instance_s == null) {
			instance_s = new PoiAlarmManager(ctx);
		}
		return instance_s;
	}

	private PoiAlarmManager(Context ctx) {
		mContext = ctx;
		poiManager = PointOfInterestManager.getInstance();
		poiManager.addPoiEventListener(this);
		initMediaPlayer();
	}

	private boolean initMediaPlayer() {
		try {
			afd = Session.MapAct.getAssets().openFd("sms_blackberry.mp3");
			mediaPlayer = new MediaPlayer();
			AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(false);
			}
			mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mediaPlayer.prepare();
		} catch (Exception e) {
			Log.e("PoiAlarmManager", "Failed to initialize media player", e);
			return false;
		}

		return true;
	}

	private void startPlayerThread() {
		if (!running) {
			new Thread(this).start();
		}
	}

	@Override
	public void poiAdded(PointOfInterest poi) {
		switch (poi.getStatus()) {
		case SERVICE_ACTIVITY_RECEIVED:
			addReceivedSa(poi, true);
		case MISSION_ENABLED:
		case MISSION_ACTIVE:
		case SERVICE_ACTIVITY_ACCEPTED:
		case SERVICE_ACTIVITY_IN_DIRECTION:
			Session.inStormMode = true;
			break;
		default:
			break;
		}
	}

	@Override
	public void poiModified(PointOfInterest poi) {
		switch (poi.getStatus()) {
		case SERVICE_ACTIVITY_RECEIVED:
			addReceivedSa(poi, true);
			Session.inStormMode = true;
			break;
		case MISSION_ENABLED:
		case MISSION_ACTIVE:
		case SERVICE_ACTIVITY_ACCEPTED:
		case SERVICE_ACTIVITY_IN_DIRECTION:
			Session.inStormMode = true;
		default:
			if (receivedSa.contains(poi)) {
				removeReceivedSa(poi);
			}
			break;
		}
	}

	@Override
	public void poiRemoved(PointOfInterest poi) {
		if (receivedSa.contains(poi)) {
			removeReceivedSa(poi);
		}
	}

	@Override
	public void onPoiListReloaded(Collection<PointOfInterest> activePois) {
		Session.inStormMode = false;

		clearReceivedSa();

		for (PointOfInterest poi : activePois) {
			switch (poi.getStatus()) {
			case SERVICE_ACTIVITY_RECEIVED:
				addReceivedSa(poi, false);
			case MISSION_ENABLED:
			case MISSION_ACTIVE:
			case SERVICE_ACTIVITY_ACCEPTED:
			case SERVICE_ACTIVITY_IN_DIRECTION:
				Session.inStormMode = true;
				break;
			default:
				break;
			}
		}

		if (!receivedSa.isEmpty()) {
			startPlayerThread();
		}
	}

	private void alertDriverOnSAReceived() {
		try {
			if (mediaPlayer == null) {
				if (!initMediaPlayer()) {
					return;
				}
			}
			mediaPlayer.start();
		} catch (Exception e) {
			Log.e("PoiAlarmManager", "Failed to play sound", e);
		}
	}

	@Override
	public void run() {
		running = true;
		while (isAlarmEnabled()) {
			try {
				synchronized (this) {
					wait(30000);
				}
				if (!receivedSa.isEmpty()) {
					alertDriverOnSAReceived();
				}
			} catch (InterruptedException e) {
			}
		}
		running = false;
	}

	synchronized private boolean isAlarmEnabled() {
		return !receivedSa.isEmpty();
	}

	synchronized private void addReceivedSa(PointOfInterest poi, boolean playNow) {
		// We only play a ring tone if the SA is assigned to the driver
		if (!poi.getCurrentServiceActivity().isMine()) {
			Log.i("PoiAlarmManager", "SA received is not mine");
			return;
		}

		if (playNow) {
			alertDriverOnSAReceived();
		}
		receivedSa.add(poi);
		if (!running) {
			startPlayerThread();
		}
	}

	synchronized private void removeReceivedSa(PointOfInterest poi) {
		receivedSa.remove(poi);
	}

	synchronized private void clearReceivedSa() {
		receivedSa.clear();
	}
}
