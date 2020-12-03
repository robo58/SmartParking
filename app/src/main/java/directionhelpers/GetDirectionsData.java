package directionhelpers;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData extends AsyncTask<Object,String,String> {
    GoogleMap mMap;
    String googleDirectionsData;
    String url;
    String duration,distance;
    LatLng latLng;
    Marker marker;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url=(String)objects[1];
        latLng=(LatLng)objects[2];
        marker = (Marker)objects[3];
        try {
            googleDirectionsData = new FetchURL().downloadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        marker.hideInfoWindow();
        HashMap<String,String> directionsList=null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        duration = directionsList.get("duration");
        distance = directionsList.get("distance");
        marker.setTitle("Duration: " + duration);
        marker.setSnippet("Distance: " + distance);
        marker.showInfoWindow();
    }
}
