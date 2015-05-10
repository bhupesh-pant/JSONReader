package com.canada.reader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//Helper Class for HTTP get request and checking the connection
public class HttpGetRequest {

	int urlresponseCode = 999;

	public int getrepcode()
	{
		return urlresponseCode;
	}
	public  HttpURLConnection getURLresponse ( String urlpath){

		HttpURLConnection httpconnection = null;
		//	int urlresponseCode = 0;

		try {
			URL url = new URL(urlpath);
			httpconnection  = (HttpURLConnection) url.openConnection();
			httpconnection.setConnectTimeout(5000);
			urlresponseCode = httpconnection.getResponseCode();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpconnection;
	}

}

