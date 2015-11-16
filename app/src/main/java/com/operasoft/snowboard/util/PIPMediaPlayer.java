package com.operasoft.snowboard.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class PIPMediaPlayer {

	private final Context mContext;

	public PIPMediaPlayer(Context context) {
		mContext = context;
	}

	public void player() {
		final MediaPlayer mediaPlayer = new MediaPlayer();
		try {
			Thread alarmThread = new Thread() {
				@Override
				public void run() {
					try {
						synchronized (this) {
							Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
							if (alert == null) {
								alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
								if (alert == null)
									alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
							}

							mediaPlayer.setDataSource(mContext, alert);
							mediaPlayer.prepare();
							if (Session.getUserPin() != null)
								mediaPlayer.start();
							wait(4000);
							mediaPlayer.stop();
							mediaPlayer.release();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			alarmThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}