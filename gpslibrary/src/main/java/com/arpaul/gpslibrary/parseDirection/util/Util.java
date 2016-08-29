package com.param.gpsutilities.parseDirection.util;

import android.util.Log;

import com.example.libraryutilities.IOUtils;
import com.param.gpsutilities.fetchLocation.GPSLogutils;
import com.param.gpsutilities.parseDirection.model.GDirection;
import com.param.gpsutilities.parseDirection.parser.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @author florian
 * Class Util
 * contains all method useful for GDirectionApi
 * 
 */
public class Util {
	
	/**
	 * Simple Rest Call to the Google Direction WebService
	 * http://maps.googleapis.com/maps/api/directions/json?
	 * 
	 * @param data
	 *           data to parse
	 * @return The json returned by the webServer
	 */
	public static String getJSONDirection(GDirectionData data) {
		String url = "http://maps.googleapis.com/maps/api/directions/json?" + "origin=" + data.getStart().latitude + ","
				+ data.getStart().longitude + "&destination=" + data.getEnd().latitude + "," + data.getEnd().longitude
				+ "&sensor=false";
		
		
		/**
		 *  mode
		 */
		if (data.getMode() != null) {
			url += "&mode=" + data.getMode();
		}
		
		/**
		 *  waypoints
		 */
		if (data.getWaypoints() != null) {
			url += "&waypoints=" + data.getWaypoints();
		}
		
		/**
		 *  alternative
		 */
		url += "&alternatives=" + data.isAlternative();
		
		/**
		 *  avoid
		 */
		if (data.getAvoid() != null) {
			url += "&avoid=" + data.getAvoid();
		}
		
		/**
		 *  language
		 */
		if (data.getLanguage() != null) {
			url += "&language=" + data.getLanguage();
		}
		
		/**
		 *  units
		 */
		if (data.getUs() != null) {
			url += "&units=" + data.getUs();
		}
				
		/**
		 *  region
		 */
		if (data.getRegion() != null) {
			url += "&region=" + data.getRegion();
		}
		
		/**
		 *  units
		 */
		if (data.getDeparture_time() != null && data.getMode() != null && (data.getMode() == Mode.MODE_DRIVING || data.getMode() == Mode.MODE_TRANSIT)) {
			url += "&departure_time=" + data.getDeparture_time();
		}
				
		/**
		 *  region
		 */
		if (data.getArrival_time() != null && data.getMode() != null && data.getMode() == Mode.MODE_TRANSIT) {
			url += "&arrival_time=" + data.getArrival_time();
		}

		String responseBody = null;

		try {
			//responseBody = getHttpConnection(url,"GET");
			GPSLogutils.error("getDirection_url",url);
			HttpURLConnection connection = getHttpConnection(url, "GET");

			byte[] outputInBytes = url.getBytes("UTF-8");
			OutputStream os = connection.getOutputStream();
			os.write(outputInBytes);

			int responseCode = connection.getResponseCode();
			GPSLogutils.error("RESPONSE CODE", String.valueOf(responseCode));

			InputStream inputStream = new BufferedInputStream(connection.getInputStream());
			responseBody = IOUtils.convertStreamToString(inputStream);
			//GPSLogutils.error("RESPONSE", response);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*// The HTTP get method send to the URL
		HttpGet getMethod = new HttpGet(url);
		// The basic response handler
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// instantiate the http communication
		HttpClient client = new DefaultHttpClient();
		// Call the URL and get the response body
		try {
			responseBody = client.execute(getMethod, responseHandler);
		} catch (ClientProtocolException e) {
			Log.e(Util.class.getCanonicalName(), e.getMessage());
		} catch (IOException e) {
			Log.e(Util.class.getCanonicalName(), e.getMessage());
		}*/
		if (responseBody != null) {
			GPSLogutils.error(Util.class.getCanonicalName(), responseBody);
		}
		// parse the response body
		return responseBody;
	}

	public static HttpURLConnection getHttpConnection(String url, String type) throws IOException {
		URL uri;
		HttpURLConnection connection;
		uri = new URL(url);
		connection = (HttpURLConnection) uri.openConnection();
		connection.setRequestMethod(type); //type: POST, PUT, DELETE, GET
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setConnectTimeout(60000); //60 secs
		connection.setReadTimeout(60000); //60 secs
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json");
		return connection;
	}

	/**
	 * Parse the Json to build the GDirection object associated
	 * 
	 * @param json
	 *            The Json to parse
	 * @return The GDirection define by the JSon
	 */
	public static List<GDirection> parseJsonGDir(String json) {
		// JSon Object to parse
		JSONObject jObject;
		// The GDirection to return
		List<GDirection> directions = null;
		if (json != null) {
			try {
				// initialize the JSon
				jObject = new JSONObject(json);
				// initialize the parser
				DirectionsJSONParser parser = new DirectionsJSONParser();
				// Starts parsing data
				directions = parser.parse(jObject);
			} catch (Exception e) {
				Log.e(Util.class.getCanonicalName(), "Parsing JSon from GoogleDirection Api failed, see stack trace below:", e);
			}
		} else {
			directions = new ArrayList<GDirection>();
		}
		return directions;
	}
}
