package com.operasoft.snowboard.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.osmand.plus.OsmandSettings;
import net.osmand.plus.voice.CommandBuilder;
import net.osmand.plus.voice.CommandPlayerException;
import net.osmand.plus.voice.TTSCommandPlayerImpl;
import android.app.Activity;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TTSCommandStreetNamePlayerImpl extends TTSCommandPlayerImpl {

	public TTSCommandStreetNamePlayerImpl(Activity ctx, OsmandSettings settings, String voiceProvider) throws CommandPlayerException {
		super(ctx, settings, voiceProvider);
	}

	public void playCommands(CommandBuilder builder, Location... locations) {
		if (mTts != null) {
			final List<String> execute = builder.execute(); //list of strings, the speech text, play it
			StringBuilder bld = new StringBuilder();
			for (String s : execute) {
				bld.append(s).append(' ');
			}
			String[] names = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				names[i] = streetFinder.getStreetName(locations[i]);
				Log.d("RGC", "Street #" + i + " >> " + names[i]);
			}

			// if one is null wait 2 seconds and retry
			boolean retry = false;
			for (int i = 0; i < locations.length; i++) {
				if (names[i] == null) {
					retry = true;
				}
			}
			if (retry) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < locations.length; i++) {
					if (names[i] == null)
						names[i] = streetFinder.getStreetName(locations[i]);
				}
			}

			String tts = bld.toString();
			tts = getStreetMessage(tts, names);
			mTts.speak(tts, TextToSpeech.QUEUE_ADD, params);
			Log.d("RGC", "with voice : " + tts + " >> ");

		}
	}

	private static final String getOnSur(String msg) {
		final String LEFT = "left", RIGHT = "right", GAUCHE = "gauche", DROITE = "droite";
		final String ON = " on ";
		final String SUR = " sur ";
		if (msg.indexOf(LEFT) >= 0)
			return ON;
		else if (msg.indexOf(RIGHT) >= 0)
			return ON;
		else if (msg.indexOf(DROITE) >= 0)
			return SUR;
		else if (msg.indexOf(GAUCHE) >= 0)
			return SUR;
		else
			return null;
	}

	public static String getStreetMessage(String message, String... streets) {
		final String REGEX = "(left|right|gauche|droite)";
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(message);
		final String onSur = getOnSur(message);
		int start = 0;
		ArrayList<String> cuts = new ArrayList<String>();
		while (matcher.find()) {
			// System.out.println(args[0] + "\t >>>" + matcher.group() + "-> " + matcher.start() + ":" + matcher.end());
			cuts.add(message.substring(start, matcher.end()));
			start = matcher.end();
		}
		int streetidx = 0;
		StringBuilder streetmessage = new StringBuilder();
		for (String cut : cuts) {
			streetmessage.append(cut);
			if (streets.length > streetidx && streets[streetidx] != null) {
				streetmessage.append(onSur);
				Log.d("TTS", "idx" + streetidx + " length" + streets.length);
				streetmessage.append(streets[streetidx++]);
			}
		}
		return (streetmessage.toString());
	}

	public StreetFinder streetFinder;

	public interface StreetFinder {
		String getStreetName(Location location);
	}
}
