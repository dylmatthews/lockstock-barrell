package Journey;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//this code was taken ands adapted from https://github.com/hiepxuan2008/GoogleMapDirectionSimple/tree/master/app/src/main/java/Modules

public class JourneyFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyCLj9vjpqR681nQdkqDPYtWZn_PII6CBb8"; //own api key
    private IJourneyFinderListener listener;
    private String origin;
    private String destination;

    //constructor
    public JourneyFinder(IJourneyFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        Log.i("POES", "execute:  snake");

    }



    //executes direction finder
    public JourneyFinder execute() throws UnsupportedEncodingException {

        listener.onDirectionFinderStart();
        Log.i("POES", "execute:  test");
       new DownloadRawData().execute(createUrl()); //calls downlaod raw data then executes create url
        return null;
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOri= URLEncoder.encode(origin, "utf-8"); //encodes string
        String urlDes = URLEncoder.encode(destination, "utf-8"); //encodes string

        return DIRECTION_URL_API + "origin=" + urlOri + "&destination=" + urlDes + "&key=" + GOOGLE_API_KEY;
        //returns the end of the url for the google maps url for response
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) { //in background
            String link = params[0]; //gets params [0]
            try {
                URL url = new URL(link);

                InputStream inputStream = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) { //builds string
                    buffer.append(line + "\n"); //appends the buffer
                }

                return buffer.toString();
                //returns json data

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) { //post excecute
            try {
                parseJSon(res);
                //sends the json data to method to read json
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
//https://developers.google.com/android/reference/com/google/android/gms/maps/model/Polyline
    //reading google maps api response
    //https://stackoverflow.com/questions/15924834/decoding-polyline-with-new-google-maps-api
//https://github.com/jd-alexander/Google-Directions-Android/blob/master/library/src/main/java/com/directions/route/GoogleParser.java
//https://gist.github.com/hallahan/7b4a3aebe4d7a0b41a5a

    private void parseJSon(String json) throws JSONException {
        if (json == null)
            return;

        List<Journey> journeys = new ArrayList<Journey>();
        JSONObject jsonData = new JSONObject(json);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) { //loop going up to end of json route
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Journey journey = new Journey();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray Legs = jsonRoute.getJSONArray("legs");
            JSONObject Leg = Legs.getJSONObject(0);
            JSONObject jsonEndLocation = Leg.getJSONObject("end_location");
            JSONObject jsonStartLocation = Leg.getJSONObject("start_location");

            journey.endAddress = Leg.getString("end_address");
            journey.startAddress = Leg.getString("start_address");
            journey.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            journey.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            journey.points = decodePolyLine(overview_polylineJson.getString("points"));

            journeys.add(journey);
        }

        listener.onDirectionFinderSuccess(journeys);
    }
//google's formula to decode coordinates to polyline
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
