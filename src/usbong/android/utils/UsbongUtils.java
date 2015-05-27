/*
 * Copyright 2012 Michael Syson
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package usbong.android.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class UsbongUtils {		
<<<<<<< HEAD
<<<<<<< HEAD
	public final static String APP_VERSION="May4,2015 (Google Play)";	
	public final static String API_KEY = "AIzaSyBh-kcAg1pmfCSQsbNqz4K4BxvBIgjOd90";
=======
	public final static String APP_VERSION="May24,2015v2 (Google Play)";	
=======
	public final static String APP_VERSION="May26,2015 (Google Play)";	
>>>>>>> 94457e6665fe4246f335c6c7a30326297d8a6890
	public final static String API_KEY = "AIzaSyB5mM_lk_bbdT5nUWQTO6S5FyZ9IgaxqXc";
>>>>>>> 089f312b1d1561b52775e892b29c2d2bf81d122e

	public final static String myPackageName="usbong.android.questionloader";

	public static int width;
	public static int height;
	public static int dpi;

	public final static int EMAIL_SENDING_SUCCESS=99;
	public static String defaultFeedbackMessage;
	
    public static String readTextFileInAssetsFolder(Activity a, String filename) {
		//READ A FILE
		//Reference: Jeffrey Jongko, Aug. 31, 2010
    	try {
//    		byte[] b = new byte[100];    		
    		AssetManager myAssetManager = a.getAssets();
    		InputStream is = myAssetManager.open(filename);

    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	String currLineString="";
        	String finalString="";

        	while((currLineString=br.readLine())!=null)
        	{
        		finalString = finalString + currLineString+"\n";
        	}	    
        	is.close();    		

        	return finalString;
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE in readTextFileInAssetsFolder(...).");
    		e.printStackTrace();
    	}    		
    	return null;
    }    
}