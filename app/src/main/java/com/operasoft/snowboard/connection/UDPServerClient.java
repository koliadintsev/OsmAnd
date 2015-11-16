package com.operasoft.snowboard.connection;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.SQLException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.operasoft.snowboard.database.DataBaseHelper;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.queue.FileFifo;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.HandlerUtils;
import com.operasoft.snowboard.util.Session;

public class UDPServerClient {
	private DatagramSocket clientSocket;
	private String data;
	//private QueueFile sendq;
	private int qstate;
	private int last_seq;
	private int seq;
	final int QUEUE_WAIT=0;
	final int QUEUE_SEND=1;
	final int QUEUE_FAIL=2;
	File qfile;
	private String qfilename;
	private boolean spooler_on=false;
	
	public void init() {
		try {
			clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(4000);
			qstate=QUEUE_SEND;
			seq=0;
			last_seq=-1;
			qfilename=Environment.getExternalStorageDirectory().getPath()+"/gpsqfile.bin";
			qfile=new File(qfilename);
			//sendq = new QueueFile(qfile);
			startSpooler();
			
		} catch (Exception e) {

		}
	}
	
	private void startSpooler() {
		if(!spooler_on)
		new Thread() {
			@Override
			public void run() {
				String buffer;
				Boolean sent;
				FileFifo q;
				spooler_on=true;
				while (true) {
					try {
						buffer="";
						sent=false;
						switch(qstate)
						{
							case QUEUE_WAIT:
									Thread.sleep(15000);
									qstate=QUEUE_SEND;
									//We dont break here since we want WAIT to move on to SEND on its own
									//break;
							case QUEUE_SEND:
									q=new FileFifo(new File(qfilename));
									if(q.size()>0)
									{
										buffer=q.peek();
										if(buffer.length()>20)
										{
											if(buffer.substring(0, 2).equalsIgnoreCase("$$"))
											{
												last_seq=getseq(buffer);
												Log.i("XirgoCommand", "Sending"+buffer);
												sendRequest(buffer);
												sent=true;
											}else{
												// Bad message format
												Log.i("XirgoCommand", "Bad message format: "+buffer);
												q.remove();
											}
										}else{
											Log.i("XirgoCommand", "Bad message size: "+buffer);
											// Bad message size
											q.remove();
										}
										
									}else{
										sent=false;
										Thread.sleep(1000);
										
									}
									q.close();
									Thread.sleep(30);
									break;
							case QUEUE_FAIL:
									Thread.sleep(30000);
									qstate=QUEUE_SEND;
									break;
						}
						//Thread.sleep(2000);
						if(sent) receiveResponse();
						
					}catch(SocketTimeoutException e){
						qstate=QUEUE_SEND;
					}catch(PortUnreachableException e){
						qstate=QUEUE_WAIT;
					}catch(IllegalBlockingModeException e){
						qstate=QUEUE_FAIL;
					}catch(IOException e){
						qstate=QUEUE_FAIL;
					} catch (Exception e) {
						qstate=QUEUE_FAIL;
					}
						
						
						
						
				}
			}
		}.start();

	}

	private int getseq(String data){
		int seq=-1;
		String[] temp;
		try{
		temp=data.replace("$","").replace("#","").split(",");
		seq=Integer.parseInt(temp[temp.length-1].toString().trim());
		}catch(Exception e){}
		return seq;
		
	}
	
	public void sendRequest(String data) throws Exception {
			FileFifo q=new FileFifo(new File(qfilename));
			InetAddress IPAddress = InetAddress.getByName(Config.getCollectorAddress());
			byte[] sendData = new byte[data.length()];
			sendData = data.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, IPAddress, Config.getCollectorPort());
			clientSocket.send(sendPacket);
			// System.out.println("Request Sent to server");
			// WHY WOULD WE READ AT THIS POINT ? 
			/*
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			clientSocket.receive(packet);
			*/
			q.close();
	}

	public String receiveResponse() throws Exception {
		String response = null;
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
		clientSocket.receive(receivePacket);
		response = new String(receivePacket.getData());
		if(getseq(response)==last_seq)
		{
			FileFifo q=new FileFifo(new File(qfilename));
			q.remove();
			qstate=QUEUE_SEND;
			q.close();
		}
		// Its a UDP Socket... no closing needed
		//clientSocket.close();
		return response;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String Exectue6xXirgoCommand(String imei, String command,
			String date, String time, String lat, String lng, String altitude,
			String speed, String direction, String sv, String hp, String bv,
			String cq, String mi, String gs, String gt, String ac, String dc,
			String ph, Context context, SharedPreferences SP)
			throws Exception {

		data="";
		if(imei!="")
		{
			SP = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = SP.edit();
			data = "$$" + imei + "," + command + "," + date + "," + time + ","
					+ lat + "," + lng + "," + altitude + "," + speed + ","
					+ direction + "," + sv + "," + hp + "," + bv + "," + cq + ","
					+ mi + "," + gs + "," + gt + "," + ac + "," + dc + "," + ph
					+ "," + seq + "##";
			FileFifo q=new FileFifo(new File(qfilename));
			q.add(data);
			q.close();
			seq++;
			if(seq>255) seq=0;
			
			try {
				//sendRequest(data);
				//editor.putString("last_xirgo_sent_time_succ", date + " " + time);
				//Log.i("XirgoCommand", "XirgoCommand sent to server successfully - "
				//		+ data);
			} catch (Exception e) {
				//editor.putString("last_xirgo_sent_time_fail", date + " " + time);
				//Log.e("XirgoCommand",
				//		" Xirgo Command Failed send data to Server - " + data);
			}
		}
////////// THIS BELOW SECTION LOOKS LIKE A MESS... WHAT IS THIS FOR ANYWAYS ?
/*
		editor.commit();
		String succ = SP.getString("last_xirgo_sent_time_succ", "");
		String fail = SP.getString("last_xirgo_sent_time_fail", "");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (!fail.equals("") && !succ.equals("")) {
			Date date2 = (Date) formatter.parse(fail);
			Date date1 = (Date) formatter.parse(succ);
			long seconds = date2.getTime() - date1.getTime();
			double diffMinuts = (double) seconds / (double) (1 * 60 * 1000);
			int FIFTEEN_MINUTES = 15;
			if (diffMinuts >= FIFTEEN_MINUTES) {
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				editor.putString("last_xirgo_sent_time_fail", "");
				editor.putString("last_xirgo_sent_time_succ", "");
				editor.putString("last_xirgo_sent_time_timeout", "yes");
				editor.putString("last_xirgo_sent_time_modified_time",
						df.format(new Date()));
				editor.commit();
			}
		} else if (!(SP.getString("last_xirgo_sent_time_timeout", "")
				.equals(""))) {
			LoginSession sessionLogin = new LoginSession();
			CommonUtils cU = new CommonUtils(context);
			sessionLogin.setImei(cU.getIMEI());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			String userId = cU.selectUserId(Utils.selectUserPin());
			String sessionid = Utils.selectSessionLoginId(userId);
			if (!sessionid.equals(""))
				sessionLogin.setId(sessionid);
*/
// TODO RENABLE THIS LATER
//			sessionLogin.setId(Utils.selectSessionLoginId(userId));
//			sessionLogin.setUserId(userId);
//			sessionLogin.setLatitude(Double.valueOf(lat));
//			sessionLogin.setLongitude(Double.valueOf(lng));
//			sessionLogin.setVehicleId(SP.getString("vehicleID", ""));
//			sessionLogin.setModifiedDateTime("");
//			SessionLoginDao sDao = new SessionLoginDao(context, sessionLogin);
//			if (!SP.getString("last_xirgo_sent_time_modified_time", "").equals(
//					"")) {
//				sessionLogin.setOperation("out");
//				sessionLogin.setModifiedDateTime(SP.getString(
//						"last_xirgo_sent_time_modified_time", ""));
//				sDao.updateSessionToServer();
//			}
//			sessionLogin.setDateTime(df.format(new Date()));
//			SessionLoginDao sDaoL = new SessionLoginDao(context, sessionLogin);
//			sessionLogin.setOperation("start");
//			if (!sessionid.equals(""))
//				sessionLogin.setModifiedDateTime(df.format(new Date()));
//			sDaoL.updateSessionToServer();
//			editor.putString("last_xirgo_sent_time_fail", "");
//			editor.putString("last_xirgo_sent_time_succ", "");
//			editor.putString("last_xirgo_sent_time_timeout", "");
//			editor.putString("last_xirgo_sent_time_modified_time", "");
//			editor.commit();
//		}
		return data;
	}

	
	public String Extended6xXirgoCommand(String imei, String command,
			String date, String time, String lat, String lng, String altitude,
			String speed, String direction, String sv, String hp, String bv,
			String cq, String mi, String gs, String gt, String ac, String dc,
			String ph, String adc, String inio, String outio, Context context, SharedPreferences SP)
			throws Exception {

		data="";
		if(imei!="")
		{
			SP = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = SP.edit();
			data = "$$" + imei + "," + command + "," + date + "," + time + ","
					+ lat + "," + lng + "," + altitude + "," + speed + ","
					+ direction + "," + sv + "," + hp + "," + bv + "," + cq + ","
					+ mi + "," + gs + "," + gt + "," + ac + "," + dc + "," + ph
					+ "," + adc + "," + inio + "," + outio + "," + seq + "##";
			FileFifo q=new FileFifo(new File(qfilename));
			q.add(data);
			q.close();
			seq++;
			if(seq>255) seq=0;
			
			try {
				//sendRequest(data);
				//editor.putString("last_xirgo_sent_time_succ", date + " " + time);
				//Log.i("XirgoCommand", "XirgoCommand sent to server successfully - "
				//		+ data);
			} catch (Exception e) {
				//editor.putString("last_xirgo_sent_time_fail", date + " " + time);
				//Log.e("XirgoCommand",
				//		" Xirgo Command Failed send data to Server - " + data);
			}
		}
		
		return data;
	}	
	
	
	public UDPServerClient() {
		init();
		// TODO Auto-generated constructor stub
	}

	public boolean Exectue6xXirgoCommandWithTestData() throws Exception {
		boolean sendSuccess = false;
		String dummydata = "$$355661030635551,6001,2012/03/28,12:00:00,28.612572,77.328481,5,25,-70,,,,,,,,,,,1##";
		try {
			init();
			sendRequest(dummydata);
			Log.i("XirgoCommand", "XirgoCommand sent to server successfully - "
					+ dummydata);
			sendSuccess = true;
		} catch (Exception e) {
			Log.e("XirgoCommand",
					" Xirgo Command Failed send data to Server - " + dummydata);
		}
		return sendSuccess;
	}

	public boolean Exectue6xXirgoCommandWithCommandData(String data)
			throws Exception {
		boolean sendSuccess = false;
		try {
			init();
			// insertXirgoCommandToBackupTable(data);
			sendRequest(data);
			Log.i("XirgoCommand", "XirgoCommand sent to server successfully - "
					+ data);
			sendSuccess = true;
		} catch (Exception e) {
			Log.e("XirgoCommand",
					" Xirgo Command Failed send data to Server - " + data);
			// No need to insert in DB as this is called only when command
			// loaded from DB
		}
		return sendSuccess;
	}

	
}
