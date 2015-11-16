package com.operasoft.snowboard.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

public class ImageUtils {

	public static String encodeBase64(String filename){
		Bitmap bm = BitmapFactory.decodeFile(IMAGE_FILEPATH+filename);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); 
		byte[] byteArray = baos.toByteArray();
		
		String encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
		return encodedImage;
	}
	
	
	public final static String IMAGE_FILEPATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+File.separator;
	
}
