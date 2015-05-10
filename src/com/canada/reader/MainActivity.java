package com.canada.reader;

import java.util.ArrayList;
import java.util.List;

import com.canada.reader.model.Fact;
import com.canada.reader.model.FactGroup;
import com.canada.reader.parsers.FactJSONParser;
import com.canada.reader.parsers.FactJSONReader;
import com.facts.reader.R;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//Main activity class for Reading and displaying the content from the JSON URL 

public class MainActivity extends ListActivity {

	TextView output;
	ProgressBar pb;
	List<JSONReader> JSONObjects;
	List<Fact> factList;
	FactGroup factGroup;

	//Main JSON Feed URL
	private static final String JSON_URL = "https://dl.dropboxusercontent.com/u/746330/facts.json";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);

		JSONObjects = new ArrayList<>();

		//Read the JSON URL and parse it if the network connection is available.
		if (isOnline()) {
			requestData(JSON_URL);
		} else {
			Toast.makeText(this, "Network isn't available",Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Handles Refresh button in the Menu
		if (item.getItemId() == R.id.action_refresh) {
			if (isOnline()) {
				requestData(JSON_URL);
			}
			else {
				Toast.makeText(this, "Network isn't available",Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

	//Execute the Main URL
	private void requestData(String uri) {
		JSONReader task = new JSONReader();
		task.execute(uri);
	}

	//Rendering all the items and displaying in the Layout
	protected void updateDisplay() {		
		//Use FactAdapter to display data		
		List<Fact> factList=factGroup.getFactList();
		//Setting the Main title from JSON URL in the Action Bar
		setTitle(factGroup.getGroupTitle());
		//Using the adapter for rows objects
		FactAdapter adapter = new FactAdapter(this, R.layout.fact_layout, factList);
		setListAdapter(adapter);
	}

	//Open the HTTP connection and checking the network availability
	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;	
		}
	}

	//Starting Async task
	private class JSONReader extends AsyncTask<String, String, FactGroup> {

		@Override
		protected void onPreExecute() {
			if (JSONObjects.size() == 0) {
				pb.setVisibility(View.VISIBLE);
			}
			JSONObjects.add(this);
		}

		@Override
		protected FactGroup doInBackground(String... params) {
			String content = FactJSONReader.getData(params[0]);			
			if (content == null) {
				return null;
			}
			factGroup  = FactJSONParser.parseFeed(content);			
			return  factGroup;
		}

		@Override
		protected void onPostExecute(FactGroup result) {			
			JSONObjects.remove(this);
			if (JSONObjects.size() == 0) {
				pb.setVisibility(View.INVISIBLE);
			}			
			if (result == null) {
				Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
				return;
			}
			updateDisplay();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

	}

}
