package usbong.android.questionloader;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class NetUtil {

	private static DefaultHttpClient httpClient = new DefaultHttpClient();

	public static String postJsonDataToUrl(String url, String json) throws Exception
	{
		InputStream is = null;
		try {
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new StringEntity(json));
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
	        
	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is = httpEntity.getContent();           
	        InputStreamReader isr = new InputStreamReader(is);
	            
	        BufferedReader reader = new BufferedReader(isr);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        
	        return sb.toString();
	    }
		finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	
	public static String postFormDataToUrl(String url, String data) throws Exception
	{
		InputStream is = null;
		try {
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new StringEntity(data));
	        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
	        
	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is = httpEntity.getContent();           
	        InputStreamReader isr = new InputStreamReader(is);
	            
	        BufferedReader reader = new BufferedReader(isr);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        
	        return sb.toString();
	    }
		finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public static String readUrlContentAsString(String url) throws Exception
	{
		InputStream is = null;
		try
		{
			// Making HTTP request
	        HttpGet httpGet = new HttpGet(url);
	 
	        HttpResponse httpResponse = httpClient.execute(httpGet);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        is = httpEntity.getContent();           
	        InputStreamReader isr = new InputStreamReader(is);
	            
	        BufferedReader reader = new BufferedReader(isr);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        
	        return sb.toString();
		}
		finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
			}
		}		
	}
	
}
