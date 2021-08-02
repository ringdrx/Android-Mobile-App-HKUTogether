package com.example.hkutogether;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.abs;

public class MainPage extends AppCompatActivity implements LocationListener {
    public static String un;
    public LocationManager locationManager;
    public static LatLng currentLocation;
    public static String currentAddress;
    public static double currentLatitude, currentLongitude;
    public static String currentfriend1, currentfriend2, currentfriend3;
    public String f1lat, f1lon, f2lat, f2lon,f3lat, f3lon;
    public static LatLng f1location, f2location, f3location;
    private Handler mHandler;
    public static boolean finishActivity=false;
    public boolean f1nearby = false;
    public boolean f2nearby = false;
    public boolean f3nearby = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Intent i = getIntent();
        un = i.getStringExtra("LoginUsername");
        Toast.makeText(MainPage.this,"Login successful. Hi, "+un+"!",Toast.LENGTH_SHORT).show();

        finishActivity=false;
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        while (true){
            if (ContextCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5,5,MainPage.this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5,5,MainPage.this);
                break;
            }
        }

        mHandler = new Handler();
        startRepeatingTask();

        //Googleplex: (37.421998,-122.084)
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (finishActivity) finish();
                final String urlFriendDetection = "https://i.cs.hku.hk/~rxduan/comp3330/project.php";
                AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                    boolean success;
                    String jsonString;

                    @Override
                    protected String doInBackground(String... arg0) {
                        success = true;
                        jsonString = getJsonPage(urlFriendDetection);
                        if (jsonString.equals("Fail to login"))
                            success = false;
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (success) {
                            ArrayList<String> usernames = new ArrayList<String>();
                            try {
                                JSONObject rootJSONObj = new JSONObject(jsonString);
                                JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                                for (int i=0; i<jsonArray.length(); i++) {
                                    usernames.add(jsonArray.getString(i));
                                }
                                for (int i=0; i<jsonArray.length(); i++) {
                                    if (usernames.get(i).equals(un)) {
                                        currentfriend1=rootJSONObj.optJSONArray("friend1").getString(i);
                                        currentfriend2=rootJSONObj.optJSONArray("friend2").getString(i);
                                        currentfriend3=rootJSONObj.optJSONArray("friend3").getString(i);
                                    }
                                }
                                if (currentfriend1.equals("*")) {
                                    f1lat="*";
                                    f1lon="*";
                                }
                                if (currentfriend2.equals("*")) {
                                    f2lat="*";
                                    f2lon="*";
                                }
                                if (currentfriend3.equals("*")) {
                                    f3lat="*";
                                    f3lon="*";
                                }
                                for (int i=0; i<jsonArray.length(); i++) {
                                    if (usernames.get(i).equals(currentfriend1)) {
                                        f1lat=rootJSONObj.optJSONArray("lat").getString(i);
                                        f1lon=rootJSONObj.optJSONArray("lon").getString(i);
                                    }
                                    if (usernames.get(i).equals(currentfriend2)) {
                                        f2lat=rootJSONObj.optJSONArray("lat").getString(i);
                                        f2lon=rootJSONObj.optJSONArray("lon").getString(i);
                                    }
                                    if (usernames.get(i).equals(currentfriend3)) {
                                        f3lat=rootJSONObj.optJSONArray("lat").getString(i);
                                        f3lon=rootJSONObj.optJSONArray("lon").getString(i);
                                    }
                                }
                                if (!f1lat.equals("*")) {
                                    f1location=new LatLng(Double.valueOf(f1lat),Double.valueOf(f1lon));
                                    if (!f1nearby) proximityAlert(Double.valueOf(f1lat), Double.valueOf(f1lon), 500);
                                } else {f1location=null;}
                                if (!f2lat.equals("*")) {
                                    f2location=new LatLng(Double.valueOf(f2lat),Double.valueOf(f2lon));
                                    if (!f2nearby) proximityAlert(Double.valueOf(f2lat), Double.valueOf(f2lon), 500);
                                } else {f2location=null;}
                                if (!f3lat.equals("*")) {
                                    f3location=new LatLng(Double.valueOf(f3lat),Double.valueOf(f3lon));
                                    if (!f3nearby) proximityAlert(Double.valueOf(f3lat), Double.valueOf(f3lon), 500);
                                } else {f3location=null;}
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.execute("");
            } finally {
                mHandler.postDelayed(mStatusChecker, 1000);
            }
        }
    };


    @Override
    public void onLocationChanged(@NonNull Location location) {
        final String url;
        if (location==null) {
            Toast.makeText(MainPage.this, "Location is null.",Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentLocation!=null && abs(currentLatitude-location.getLatitude())<0.001 && abs(currentLongitude-location.getLongitude())<0.001) return;
        try{
            currentLatitude=location.getLatitude();
            currentLongitude=location.getLongitude();
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                    +android.net.Uri.encode(un, "UTF-8")
                    +"&lat="
                    +android.net.Uri.encode(""+currentLatitude, "UTF-8")
                    +"&lon="
                    +android.net.Uri.encode(""+currentLongitude, "UTF-8");

            Toast.makeText(MainPage.this, "Location Changed!",Toast.LENGTH_SHORT).show();

            Geocoder geocoder = new Geocoder(MainPage.this, Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            currentAddress = addresses.get(0).getAddressLine(0);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainPage.this, "Failed to get new location.",Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            final ProgressDialog pdialog = new ProgressDialog(MainPage.this);
            pdialog.setCancelable(false);
            pdialog.setMessage("Connecting ...");
            pdialog.show();
            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                boolean success;
                String jsonString;
                @Override
                protected String doInBackground(String... arg0) {
                    success = true;

                    pdialog.setMessage("Uploading location...");
                    pdialog.show();

                    jsonString = getJsonPage(url);
                    if (jsonString.equals("Fail to login"))
                        success = false;
                    return null;
                }
                @Override
                protected void onPostExecute(String result) {
                    if (success) {
                        try {
                            JSONObject rootJSONObj = new JSONObject(jsonString);
                            JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                            Toast.makeText(MainPage.this, "Location uploaded.",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    pdialog.hide();
                }
            }.execute("");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainPage.this, "Failed to upload location.",Toast.LENGTH_SHORT).show();
        }
    }
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    public void onProviderEnabled(@NonNull String provider) { }
    public void onProviderDisabled(@NonNull String provider) { }

    public void proximityAlert(double latitude, double longitude, double radius){
        if (distance(currentLatitude,latitude,currentLongitude,longitude) < radius){
            if (f1lat.equals(""+latitude) && f1lon.equals(""+longitude)) f1nearby=true;
            if (f2lat.equals(""+latitude) && f2lon.equals(""+longitude)) f2nearby=true;
            if (f3lat.equals(""+latitude) && f3lon.equals(""+longitude)) f3nearby=true;
            Toast.makeText(MainPage.this, "Friends nearby!",Toast.LENGTH_SHORT).show();
            int reqCode = 1;
            Intent intent = new Intent(getApplicationContext(), MainPage.class);
            showNotification(this, "Friends Nearby!", "One of your friends is nearby now.", intent, reqCode);
        }
    }
    public double distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_accessibility_24)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
        Log.d("showNotification", "showNotification: " + reqCode);
    }

    public String getJsonPage(String url) {
        HttpURLConnection conn_object = null;
        final int HTML_BUFFER_SIZE = 2*1024*1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];

        try {
            URL url_object = new URL(url);
            conn_object = (HttpURLConnection) url_object.openConnection();
            conn_object.setInstanceFollowRedirects(true);
            BufferedReader reader_list = new BufferedReader(new InputStreamReader(conn_object.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader_list, htmlBuffer, HTML_BUFFER_SIZE);
            reader_list.close();
            return HTMLSource;
        } catch (Exception e) {
            System.out.println("Exception caught!");
            return "Fail to login";
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            if (conn_object != null) {
                conn_object.disconnect();
            }
        }
    }
    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }

    @Override
    protected void onDestroy(){
        stopRepeatingTask();
        final String urlondestroy = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                +android.net.Uri.encode(un, "UTF-8")
                +"&lat=*&lon=*";
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;
            @Override
            protected String doInBackground(String... arg0) {
                success = true;
                jsonString = getJsonPage(urlondestroy);
                if (jsonString.equals("Fail to login"))
                    success = false;
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                if (success) {
                    try {
                        JSONObject rootJSONObj = new JSONObject(jsonString);
                        JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute("");
        super.onDestroy();
    }


    void startRepeatingTask() {
        mStatusChecker.run();
    }
    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}