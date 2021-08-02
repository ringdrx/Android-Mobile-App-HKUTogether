package com.example.hkutogether;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AddFriend extends AppCompatActivity {
    private TextView addFriendByUsername;
    private Button searchForNewFriend, addfriendback;
    public ArrayList<String> existedUsernames;
    private String currentfriend1, currentfriend2, currentfriend3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriend);

        addFriendByUsername=findViewById(R.id.addFriendByUsername);
        searchForNewFriend=findViewById(R.id.searchForNewFriend);
        addfriendback=findViewById(R.id.addfriendback);
        updateExistedUsernames();

        final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php";
        AsyncTask<String, Void, String> task1 = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;

            @Override
            protected String doInBackground(String... arg0) {
                success = true;
                jsonString = getJsonPage(url);
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
                        JSONArray jsonArray1 = rootJSONObj.optJSONArray("username");
                        for (int i=0; i<jsonArray1.length(); i++) {
                            usernames.add(jsonArray1.getString(i));
                        }
                        for (int i=0; i<jsonArray1.length(); i++) {
                            if (usernames.get(i).equals(MainPage.un)){
                                currentfriend1=rootJSONObj.optJSONArray("friend1").getString(i);
                                currentfriend2=rootJSONObj.optJSONArray("friend2").getString(i);
                                currentfriend3=rootJSONObj.optJSONArray("friend3").getString(i);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute("");

        addfriendback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchForNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newFriend = addFriendByUsername.getText().toString();
                if (MainPage.un.equals(newFriend)) { Toast.makeText(AddFriend.this, "Cannot add yourself as friend.",Toast.LENGTH_SHORT).show(); }
                else if (!existedUsernames.contains(newFriend)) { Toast.makeText(AddFriend.this, "User not found.",Toast.LENGTH_SHORT).show(); }
                else if (newFriend.equals(currentfriend1)||newFriend.equals(currentfriend2)||newFriend.equals(currentfriend3)){
                    Toast.makeText(AddFriend.this, "This user is already in your friend list.",Toast.LENGTH_SHORT).show();
                }
                else if (currentfriend1.equals("*")){
                    final String addfriendurl = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                            +android.net.Uri.encode(MainPage.un, "UTF-8")
                            +"&friend1="
                            +android.net.Uri.encode(newFriend, "UTF-8");
                    AsyncTask<String, Void, String> task2 = new AsyncTask<String, Void, String>() {
                        boolean success;
                        String jsonString;
                        @Override
                        protected String doInBackground(String... arg0) {
                            success = true;
                            jsonString = getJsonPage(addfriendurl);
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
                                    Toast.makeText(getBaseContext(),"Friend added.",Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.execute("");
                }
                else if (currentfriend2.equals("*")){
                    final String addfriendurl = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                            +android.net.Uri.encode(MainPage.un, "UTF-8")
                            +"&friend2="
                            +android.net.Uri.encode(newFriend, "UTF-8");
                    AsyncTask<String, Void, String> task2 = new AsyncTask<String, Void, String>() {
                        boolean success;
                        String jsonString;
                        @Override
                        protected String doInBackground(String... arg0) {
                            success = true;
                            jsonString = getJsonPage(addfriendurl);
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
                                    Toast.makeText(getBaseContext(),"Friend added.",Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.execute("");
                }
                else if (currentfriend3.equals("*")){
                    final String addfriendurl = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                            +android.net.Uri.encode(MainPage.un, "UTF-8")
                            +"&friend3="
                            +android.net.Uri.encode(newFriend, "UTF-8");
                    AsyncTask<String, Void, String> task2 = new AsyncTask<String, Void, String>() {
                        boolean success;
                        String jsonString;
                        @Override
                        protected String doInBackground(String... arg0) {
                            success = true;
                            jsonString = getJsonPage(addfriendurl);
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
                                    Toast.makeText(getBaseContext(),"Friend added.",Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.execute("");
                }
                else { Toast.makeText(AddFriend.this, "Your friend list is full.",Toast.LENGTH_SHORT).show(); }

            }
        });
    }
    public void updateExistedUsernames() {
        final ProgressDialog pdialog = new ProgressDialog(this);
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
                    ArrayList<String> usernames = new ArrayList<String>();
                    try {
                        JSONObject rootJSONObj = new JSONObject(jsonString);
                        JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                        for (int i=0; i<jsonArray.length(); i++) {
                            String username = jsonArray.getString(i);
                            usernames.add(username);
                        }
                        existedUsernames=usernames;
                        Toast.makeText(AddFriend.this,"Connection successful.",Toast.LENGTH_SHORT).show();
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
