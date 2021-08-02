package com.example.hkutogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.loginusername);
        password = findViewById(R.id.loginpassword);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        Intent i = getIntent();
        String registeredUsername = i.getStringExtra("RegisteredUsername");
        username.setText(registeredUsername);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), Register.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String un = username.getText().toString();
                final String pwd = password.getText().toString();

                if (un.isEmpty()) Toast.makeText(MainActivity.this,"Please enter your username.",Toast.LENGTH_SHORT).show();
                else if (pwd.isEmpty()) Toast.makeText(MainActivity.this,"Please enter your password.",Toast.LENGTH_SHORT).show();
                else {
                    final ProgressDialog pdialog = new ProgressDialog(MainActivity.this);
                    pdialog.setCancelable(false);
                    pdialog.setMessage("Connecting ...");
                    pdialog.show();
                    final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php";
                    AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                        boolean success;
                        String jsonString;

                        @Override
                        protected String doInBackground(String... arg0) {
                            // TODO Auto-generated method stub
                            success = true;
                            pdialog.setMessage("Checking database...");
                            pdialog.show();
                            jsonString = getJsonPage(url);
                            if (jsonString.equals("Fail to login"))
                                success = false;
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            if (success) {
                                boolean loginSuccessful = false;
                                ArrayList<String> usernames = new ArrayList<String>();
                                ArrayList<String> passwords = new ArrayList<String>();
                                try {
                                    JSONObject rootJSONObj = new JSONObject(jsonString);
                                    JSONArray jsonArray1 = rootJSONObj.optJSONArray("username");
                                    JSONArray jsonArray2 = rootJSONObj.optJSONArray("password");
                                    for (int i=0; i<jsonArray1.length(); i++) {
                                        usernames.add(jsonArray1.getString(i));
                                        passwords.add(jsonArray2.getString(i));
                                    }
                                    for (int i=0; i<jsonArray1.length(); i++) {
                                        if (usernames.get(i).equals(un)){
                                            if (passwords.get(i).equals(pwd)){
                                                loginSuccessful=true;
                                                Intent intent = new Intent(getBaseContext(), MainPage.class);
                                                intent.putExtra("LoginUsername",un);
                                                startActivity(intent);
                                            }
                                            else Toast.makeText(MainActivity.this,"Incorrect password.",Toast.LENGTH_SHORT).show();
                                        }
                                        if (!loginSuccessful && (i==jsonArray1.length()-1)&&(!usernames.get(i).equals(un)))
                                            Toast.makeText(MainActivity.this,"Username does not exist.",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                alert( "Error", "Fail to connect" );
                            }
                            pdialog.hide();
                        }
                    }.execute("");
                }
            }
        });
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
    protected void alert(String title, String mymessage){
        new AlertDialog.Builder(this)
                .setMessage(mymessage)
                .setTitle(title)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){}
                        }
                )
                .show();
    }
}