package com.canada.reader.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.util.Log;

import com.canada.reader.HttpGetRequest;

//Class for Reading the JSON URL 

public class FactJSONReader {

	public static String getData(String uri) {

		BufferedReader reader = null;

		try{
			HttpGetRequest httprequest = new HttpGetRequest();
			HttpURLConnection httpconnection = httprequest.getURLresponse(uri);
			int urlresponseCode = httprequest.getrepcode();

			Log.w("HttpRequest response code:", + urlresponseCode +" - "+ uri);

			//To check whether URL is working fine

			if(httpconnection!=null && (urlresponseCode == HttpURLConnection.HTTP_OK || urlresponseCode == HttpURLConnection.HTTP_ACCEPTED))
			{
				StringBuilder sb = new StringBuilder();

				reader = new BufferedReader(new InputStreamReader(httpconnection.getInputStream(),"iso-8859-1"),8);

				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				httpconnection.disconnect();
				return sb.toString();
			}
			else{
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}
}
