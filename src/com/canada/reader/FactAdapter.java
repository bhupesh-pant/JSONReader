package com.canada.reader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.canada.reader.model.Fact;
import com.facts.reader.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FactAdapter extends ArrayAdapter<Fact> {
	private Context context;
	private List<Fact> factList;
	private LruCache<Integer, Bitmap> imageCache;   

	public FactAdapter(Context context, int resource, List<Fact> objects) {
		super(context, resource, objects);
		this.context = context;
		this.factList = objects;

		//Calculate the available device memory
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
		//calculate the Cache size;
		final int cacheSize = maxMemory/8;
		imageCache = new LruCache<>(cacheSize);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = 
				(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fact_layout, parent, false);

		Fact fact = factList.get(position);						

		//Display facts title name in the TextView widget
		//If item Sub title is null, set it Title unavailable otherwise set it with item title.

		TextView tv = (TextView) view.findViewById(R.id.sub_title);				
		if(fact.getTitle() == "null")
			tv.setText(context.getResources().getString(R.string.title_unavailable));
		else
			tv.setText(fact.getTitle()); 

		//Display description title name in the TextView widget
		//If item Description is null, set it Description unavailable otherwise set it with item description.

		tv = (TextView) view.findViewById(R.id.descriptionView);		
		if(fact.getDescription() == "null")
			tv.setText(context.getResources().getString(R.string.description_unavailable));
		else
			tv.setText(fact.getDescription());

		//Display Facts Images in ImageView widget
		ImageView image = (ImageView) view.findViewById(R.id.imageView);

		Bitmap bitmap = imageCache.get(fact.getProductId());

		//Bitmap bitmap; 
		if (bitmap  != null) {			
			image.setImageBitmap(fact.getBitmap());
		}
		else if(fact.getImageurlstatus()==false||fact.getImageHref()=="null"){
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image_available);
			image.setImageBitmap(bitmap);
		}
		else {
			FactAndView container = new FactAndView();
			container.fact = fact;
			container.view = view;
			ImageLoader loader = new ImageLoader();
			loader.execute(container);
		}
		return view;
	}

	class FactAndView {
		public Fact fact;
		public View view;
		public Bitmap bitmap;
	}

	private class ImageLoader extends AsyncTask<FactAndView, Void, FactAndView> {

		//This class is use for downloading the images 
		@Override
		protected FactAndView doInBackground(FactAndView... params) {
			FactAndView container = params[0];
			Fact fact = container.fact;		    
			Bitmap bitmap;
			try {
				String imageUrl =  fact.getImageHref();

				HttpGetRequest httprequest = new HttpGetRequest();

				HttpURLConnection httpconnection = httprequest.getURLresponse(imageUrl);
				int urlresponseCode = httprequest.getrepcode();

				Log.w("HttpRequest response code:", + urlresponseCode +" - "+ imageUrl);

				if(httpconnection!=null && (urlresponseCode == HttpURLConnection.HTTP_OK || urlresponseCode == HttpURLConnection.HTTP_ACCEPTED)) {

					InputStream in = httpconnection.getInputStream();
					bitmap = BitmapFactory.decodeStream(in);
					fact.setBitmap(bitmap);
					in.close();							
					httpconnection.disconnect();
				}
				else
				{	
					Log.e("httpconnection was null for url ", imageUrl);
					bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image_available);
					fact.setImageurlstatus(false);
				}

				container.bitmap = bitmap;
				return container;
			} 
			catch (java.net.SocketTimeoutException a) {
			}

			catch (java.io.IOException e) {
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(FactAndView result) {
			ImageView image = (ImageView) result.view.findViewById(R.id.imageView);
			image.setImageBitmap(result.bitmap);
			//result.fact.setBitmap(result.bitmap);
			imageCache.put(result.fact.getProductId(), result.bitmap);
		}
	}
}
