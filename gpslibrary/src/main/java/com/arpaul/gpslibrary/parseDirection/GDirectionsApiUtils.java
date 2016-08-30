/**<ul>
 * <li>GoogleMapSample</li>
 * <li>com.android2ee.formation.librairies.google.map.utils.direction</li>
 * <li>12 sept. 2013</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.arpaul.gpslibrary.parseDirection;


import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.param.gpsutilities.parseDirection.model.GDColor;
import com.param.gpsutilities.parseDirection.model.GDLegs;
import com.param.gpsutilities.parseDirection.model.GDPath;
import com.param.gpsutilities.parseDirection.model.GDPoint;
import com.param.gpsutilities.parseDirection.model.GDirection;
import com.param.gpsutilities.parseDirection.util.GDirectionData;
import com.param.gpsutilities.parseDirection.util.GDirectionMapsOptions;
import com.param.gpsutilities.parseDirection.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to make a layer over the Direction Api.
 *        To have a Google Direction and Draw it You just have to:
 *        <ul>
 *        <li>Implements the DCACallBack and its method public void onDirectionLoaded(GDirection
 *        direction)</li>
 *        <li>Then you call GDirectionApiUtils.getDirection(DCACallBack, start point, end point,
 *        GDirectionsApiUtils.MODE_***); When the GDirection is build, the DCACallBack is called
 *        giving you the GDirection you are waiting for.</li>
 *        <li>Then you can handle yourself the GDirection or you can call drawGDirection(GDirection
 *        direction, GoogleMap map)</li>
 *        </ul>
 *        d
 *        public class MainActivity extends ActionBarActivity implements DCACallBack {</br>
 *        private void getDirections(LatLng point) {</br>
 *        GDirectionsApiUtils.getDirection(this, mDeviceLatlong, point,
 *        GDirectionsApiUtils.MODE_WALKING);</br>
 *        }</br>
 *        public void onDirectionLoaded(GDirection direction) {</br>
 *        Log.e("MainActivity", "onDirectionLoaded : Draw GDirections Called with path " +
 *        direction);</br>
 *        // Display the direction or use the DirectionsApiUtils</br>
 *        GDirectionsApiUtils.drawGDirection(direction, mMap);</br>
 *        }
 */
public class GDirectionsApiUtils {
	
	/******************************************************************************************/
	/** Public Method **************************************************************************/
	/******************************************************************************************/

	/**
	 * Draw on the given map the given GDirection object
	 * 
	 * @param direction
	 *            The google direction to draw
	 * @param map
	 *            The map to draw on
	 */
	public static void drawGDirection(GDirection direction, GoogleMap map){
		drawGDirection(direction, map, null, false);
	}	
	

	/**
	 * Draw on the given map the given GDirection object
	 * 
	 * @param direction
	 *            The google direction to draw
	 * @param map
	 *            The map to draw on
	 * @param mapsOptions
	 *            mapsOptions to draw on google maps
	 */
	static int legsIndex = 0;
	public static void drawGDirection(GDirection direction, GoogleMap map, GDirectionMapsOptions mapsOptions, boolean isShortestPath) {
		// The polylines option to create polyline
		PolylineOptions lineOptions = null;
		// index of GDPoint within the current GDPath
		int i = 0;
		// index of the current GDPath
		int pathIndex = 0;
		// index of the current GDLegs
		/*int legsIndex = 0;*/
		ArrayList<GDColor> colors = null;
		if (mapsOptions != null) {
			colors = mapsOptions.getColors();
		}
		
		IGDFormatter formatter = null;
		if (mapsOptions != null) {
			formatter = mapsOptions.getFormatter();
		}

		// Browse the directions' legs and then the leg's paths
		for (GDLegs legs : direction.getLegsList()) {
			for (GDPath path : legs.getPathsList()) {
				// Create the polyline
				if (mapsOptions != null) {
					lineOptions = mapsOptions.getPolylineOptions();
				} else {
					lineOptions = new PolylineOptions();
					// A 5 width Polyline please
					lineOptions.width(5);
					// color options (alternating green/blue path)
					if (legsIndex % 2 == 0) {
						lineOptions.color(Color.GREEN);
					} else {
						lineOptions.color(Color.BLUE);
					}
				}
				// manage indexes
				i = 0;
				pathIndex++;
				// browse the GDPoint that define the path
				for (GDPoint point : path.getPath()) {
					i++;
					// Add the point to the polyline
					lineOptions.add(point.getLatLng());
					// Mark the last GDPoint of the path with a HUE_AZURE marker
					if (i == path.getPath().size() - 1) {
						if(isShortestPath){
							// create marker
							Marker marker = map.addMarker(new MarkerOptions().position(point.getLatLng())
									.title(formatter != null ? formatter.getTitle(path) : "Step " + i)
									.snippet(formatter != null ? formatter.getSnippet(path) : "Step " + i)
									.icon(((colors != null && colors.size() > 0) ?
											BitmapDescriptorFactory.defaultMarker(colors.get(legsIndex % colors.size()).colorPin) :
											BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));

							// if we have a custom snippet call contents
							if (formatter != null && formatter.isInfoWindows()) {
								formatter.setContents(marker, direction, legs, path);
								marker.showInfoWindow();
							}
						}
					}
				}
			
				// Override polyline color 
				if (colors != null && colors.size() > 0) {
					if(isShortestPath)
						lineOptions.color(colors.get(0).colorLine);
					else
						lineOptions.color(colors.get(1).colorLine);
				}
				// Drawing polyline in the Google Map for the route
				map.addPolyline(lineOptions);
				
			}
			legsIndex++;
		}
	}

	public static int getShortestDirection(List<GDirection> directions){
		long shortestLeg = 999999999;
		int position = 0;
		for(int i = 0; i < directions.size(); i ++) {
			GDirection direction = directions.get(i);
			for (GDLegs legs : direction.getLegsList()) {
				if(shortestLeg > legs.getmDistance()){
					shortestLeg = legs.getmDistance();
					position = i;
				}
			}
		}
		return position;
	}
	
	public static void getDirection(DCACallBack callback, GDirectionData data) {
		GoogleDirectionAsyncRestCall async = new GoogleDirectionAsyncRestCall(callback);
		async.execute(data);
	}
	
	/******************************************************************************************/
	/** Private Method : The big dark gas factory **************************************************************************/
	/******************************************************************************************/

	/**
	 * @author Mathias Seguy (Android2EE)
	 * mode (defaults to driving) — Specifies the mode of transport to use when calculating directions. Valid values are specified in Travel Modes. If you set the mode to "transit" you must also specify either a departure_time or an arrival_time.
	 * waypoints — Specifies an array of waypoints. Waypoints alter a route by routing it through the specified location(s). A waypoint is specified as either a latitude/longitude coordinate or as an address which will be geocoded. Waypoints are only supported for driving, walking and bicycling directions. (For more information on waypoints, see Using Waypoints in Routes below.)
	 * alternatives — If set to true, specifies that the Directions service may provide more than one route alternative in the response. Note that providing route alternatives may increase the response time from the server.
	 * avoid — Indicates that the calculated route(s) should avoid the indicated features. This parameter supports the following arguments:
	 * tolls indicates that the calculated route should avoid toll roads/bridges.
	 * highways indicates that the calculated route should avoid highways.
	 * ferries indicates that the calculated route should avoid ferries.
	 * For more information see Route Restrictions below.
	 * language — The language in which to return results. See the list of supported domain languages. Note that we often update supported languages so this list may not be exhaustive. If language is not supplied, the service will attempt to use the native language of the domain from which the request is sent.
	 * units — Specifies the unit system to use when displaying results. Valid values are specified in Unit Systems below.
	 * region — The region code, specified as a ccTLD ("top-level domain") two-character value. (For more information see Region Biasing below.)
	 * departure_time specifies the desired time of departure as seconds since midnight, January 1, 1970 UTC. The departure time may be specified in two cases:
	 * For Transit Directions: One of departure_time or arrival_time must be specified when requesting directions.
	 * For Driving Directions: Google Maps API for Work customers can specify the departure_time to receive trip duration considering current traffic conditions. The departure_time must be set to within a few minutes of the current time.
	 * A special departure_time value "now" can also be used to automatically calculate the current departure time. Note that a numeric departure_time must be specified as an integer.
	 * arrival_time specifies the desired time of arrival for transit directions as seconds since midnight, January 1, 1970 UTC. One of departure_time or arrival_time must be specified when requesting transit directions. Note that arrival_time must be specified as an integer.
	 * Either the arrival_time or the departure_time parameter must be specified any time you request transit directions.
	 * 
	 * @goals
	 *        This class aims to make an async call to the server and retrieve the Json representing
	 *        the Direction
	 *        Then build the GDirection object
	 *        Then post it to the DCACallBack in the UI Thread
	 */
	public static final class GoogleDirectionAsyncRestCall extends AsyncTask<GDirectionData, String, List<GDirection>> {

		
		/**
		 * The CallBack which waiting for the GDirection object
		 */
		private DCACallBack callback;
		
		public GoogleDirectionAsyncRestCall(DCACallBack callback) {
			super();
			
			this.callback = callback;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected List<GDirection> doInBackground(GDirectionData... arg0) {
			// Do the rest http call
			String json = Util.getJSONDirection(arg0[0]);
			// Parse the element and return it
			return Util.parseJsonGDir(json);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(List<GDirection> result) {
			super.onPostExecute(result);
			// Just call the callback
			callback.onDirectionLoaded(result);
		}
	}
	
}
