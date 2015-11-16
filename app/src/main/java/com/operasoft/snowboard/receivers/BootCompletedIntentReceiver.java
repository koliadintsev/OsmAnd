package com.operasoft.snowboard.receivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;

import com.operasoft.snowboard.database.DataBaseHelper;
import com.operasoft.snowboard.services.ServiceMonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) { 
			File outFile = new File(Environment.getExternalStorageDirectory().getPath()+"/snowman.trace");
			if (outFile.exists()) {
				outFile.delete();
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(outFile);
				String msg="I have been here";
				fos.write(msg.getBytes());
				fos.flush();
				fos.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Start service monitor
			
			   Intent pushIntent = new Intent(context, ServiceMonitor.class);  
			   context.startService(pushIntent);  
			   
			   
			   
			   
		}  

	}

}
