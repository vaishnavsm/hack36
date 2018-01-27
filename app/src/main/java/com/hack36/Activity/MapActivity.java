package com.hack36.Activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hack36.Adapters.DirectionsJSONParser;
import com.hack36.Helpers.SharedPrefHelper;
import com.hack36.Models.Location;
import com.hack36.Models.MyDatabase;
import com.hack36.R;
import com.hack36.Utils.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng targetLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Constants.PUSH_NOTIFICATION already set false in MainActivity
        String pos = SharedPrefHelper.getInstance().getString(Constants.PUSH_MSG_LOCATION);
        try {
            JSONObject jsonObject = new JSONObject(pos);
            targetLocation = new LatLng(Double.valueOf(jsonObject.getString("latitude")),Double.valueOf(jsonObject.getString("longitude")));
            mapFragment.getMapAsync(this);
        }catch (Exception e){e.printStackTrace();}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 8));

        Location currPosition = MyDatabase.getInstance(getApplicationContext())
                .locationDao().getAllAfter(System.currentTimeMillis()/1000 - 60*60).get(0); // 1 hr old location

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/directions/json"+
                        "?origin="+currPosition.getLatitude()+","+currPosition.getLongitude()+
                        "&destination="+targetLocation.latitude+","+targetLocation.longitude+
                        "&key="+getResources().getString(R.string.google_maps_key))
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(),"Calling Maps failed",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                updateMap(response.body().string());
            }
        });
    }

    void updateMap(final String response){
        Handler mainHandler = new Handler(getMainLooper());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                new ParserTask().execute(response);
            }
        });
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.valueOf(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng targetLocation = new LatLng(lat, lng);

                    points.add(targetLocation);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}