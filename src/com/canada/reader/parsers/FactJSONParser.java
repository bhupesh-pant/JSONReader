package com.canada.reader.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.canada.reader.model.Fact;
import com.canada.reader.model.FactGroup;

//Class to parse the JSON feed and retrieve all the objects

public class FactJSONParser {

	public static FactGroup parseFeed(String content){
		try {
			//FactGroup will have Main title and elements of rows
			FactGroup factGroup = new FactGroup();
			JSONObject obj = new JSONObject(content);

			// Retrieving main title of the JSON File
			factGroup.setGroupTitle(obj.getString("title"));

			// Retrieving items (Sub title, description, image) from the rows of the JSON File
			JSONArray rows = obj.getJSONArray("rows");

			List<Fact> factList = new ArrayList<Fact>();

			for (int i = 0; i < rows.length(); i++) {
				JSONObject obj1 = rows.getJSONObject(i);
				Fact fact = new Fact();
				fact.setTitle(obj1.getString("title"));
				fact.setDescription(obj1.getString("description"));
				fact.setImageHref(obj1.getString("imageHref"));
				factList.add(fact);
			}
			factGroup.setFactList(factList);
			return factGroup;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
