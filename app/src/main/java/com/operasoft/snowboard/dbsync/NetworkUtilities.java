package com.operasoft.snowboard.dbsync;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.operasoft.snowboard.util.Session;


public class NetworkUtilities {
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_IMEI = "imei";
	public static final String PARAM_PIN = "pin";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_MODEL = "model";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_OFFSET = "offset";
	public static final String PARAM_OPTION = "options";
	public static final int    SYNC_LIMIT = 100; // Number of rowViews to retrieve at a time during periodic synchronization process
	
	public static final String PARAM_UPDATED = "timestamp";
	
	public static final int REGISTRATION_TIMEOUT = 30 * 1000;

	// NOTE: This value will be properly the first time Config.init() is called.
	public static String BASE_URL = "http://dev.snowman.operasoft.ca/api/";
	//public static String BASE_URL = "http://snowman.operasoft.ca/api/";
	public static String AUTH_URI = BASE_URL + "call";
	public static String AUTH_URI_SYNC = BASE_URL + "syncData";
	public static String AUTH_URI_IMEI_COM = BASE_URL + "getImeiCompanyData";
	public static String AUTH_BASE_DATA_URI = BASE_URL + "getBaseData";
	public static String AUTH_LOGIN = BASE_URL + "login";

	private static HttpClient mHttpClient;
	public static String mUserId = "4f8d4e0b-6620-4d1b-b6df-3115fa739a52";

	public static void setBASE_URL(String bASE_URL) {
		BASE_URL = bASE_URL;
		AUTH_URI = BASE_URL + "call";
		AUTH_URI_SYNC = BASE_URL + "syncData";
		AUTH_URI_IMEI_COM = BASE_URL + "getImeiCompanyData";
		AUTH_BASE_DATA_URI = BASE_URL + "getBaseData";
		AUTH_LOGIN = BASE_URL + "login";
	}

	public static void maybeCreateHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			ClientConnectionManager mgr = mHttpClient.getConnectionManager();
			mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
			HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
			ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		}
	}

	/**
	 * Make the server call
	 * 
	 * @param URI
	 *            -- URL where the server are staible.
	 * @param params
	 *            - records arrayList.
	 * @return return an String of the record.
	 */
	synchronized public static String getCurlResponse(String URI, List<NameValuePair> params) throws IOException, ClientProtocolException {
		HttpEntity entity = null;
		HttpResponse resp = null;
		String value = null;

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			// e1.printStackTrace();
		}
		Log.d("Server Call", "URI: " + URI + ", entity: " + EntityUtils.toString(entity));
		final HttpPost post = new HttpPost(URI);
		post.addHeader(entity.getContentType());
		post.setEntity(entity);
		maybeCreateHttpClient();
		try {
			resp = mHttpClient.execute(post);
			HttpEntity entityResponse = resp.getEntity();
			value = EntityUtils.toString(entityResponse);
			Session.networkState = "connected";
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// e.printStackTrace();
			Session.networkState = "disconnected";
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
			Session.networkState = "disconnected";
		}

		if (resp != null) {
			resp.getEntity().consumeContent();
		}

		return value;
	}
	
	/**
	 * Make the server call
	 * 
	 * @param URI
	 *            -- URL where the server are staible.
	 * @param params
	 *            - records arrayList. (including image Uri)
	 * @return return an String of the record.
	 */
	@SuppressLint("NewApi")
	synchronized public static String getCurlResponseForImage(String URI, List<NameValuePair> params)
			throws IOException, ClientProtocolException {
		HttpEntity entity = null;
		HttpResponse resp = null;
		String value = null;

		Bitmap bitmapOrg = BitmapFactory.decodeFile(params.get(params.size() - 1).getValue());
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
		byte[] ba = bao.toByteArray();
		String ba1 = Base64.encodeToString(ba, 0);
		params.add(new BasicNameValuePair("Worksheet[daily_photos]", ba1));

		try {
			entity = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e1) {
			// e1.printStackTrace();
		}

		Log.d("Server Call", "URI: " + URI + ", entity: " + EntityUtils.toString(entity));
		final HttpPost post = new HttpPost(URI);
		post.addHeader(entity.getContentType());
		post.setEntity(entity);
		maybeCreateHttpClient();
		try {
			resp = mHttpClient.execute(post);
			HttpEntity entityResponse = resp.getEntity();
			value = EntityUtils.toString(entityResponse);
			Session.networkState = "connected";
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// e.printStackTrace();
			Session.networkState = "disconnected";
			throw e;
		} catch (Exception e) {
			// e.printStackTrace();
			Session.networkState = "disconnected";
		}

		if (resp != null) {
			resp.getEntity().consumeContent();
		}

		return value;
	}

	/**
	 * Check whether or not we have network connectivity
	 */
	static public boolean isOnline(Context context) {
	    ConnectivityManager cm =
	        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	/*
	 * used to fetch pictures for work_order
	 */
	static public byte[] getPicture(String fileName){
	     try {
	             URL imageUrl = new URL(BASE_URL + fileName);
	             URLConnection ucon = imageUrl.openConnection();

	             InputStream is = ucon.getInputStream();
	             BufferedInputStream bis = new BufferedInputStream(is);

	             ByteArrayBuffer baf = new ByteArrayBuffer(5000);
	             int current = 0;
	             while ((current = bis.read()) != -1) {
	                     baf.append((byte) current);
	             }

	             return baf.toByteArray();
	     } catch (Exception e) {
	             Log.d("getWorkOrderPicture", "fileName=" + fileName + " Error: " + e.toString());
	     }
	     return null;
	}
}