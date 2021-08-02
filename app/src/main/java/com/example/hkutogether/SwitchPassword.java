package com.example.hkutogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SwitchPassword extends AppCompatActivity {
    EditText oldpassword, newpassword1, newpassword2;
    Button changepwdconfirm, changepwdback, changepwdclear;
    String un = MainPage.un;
    String mypwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchpassword);

        Intent i = getIntent();

        oldpassword= findViewById(R.id.oldpassword);
        newpassword1= findViewById(R.id.newpassword1);
        newpassword2= findViewById(R.id.newpassword2);
        changepwdconfirm= findViewById(R.id.changepwdconfirm);
        changepwdclear= findViewById(R.id.changepwdclear);
        changepwdback= findViewById(R.id.changepwdback);

        final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php";
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;
            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
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
                                mypwd=passwords.get(i);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute("");


        changepwdconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old = oldpassword.getText().toString();
                final String new1 = newpassword1.getText().toString();
                final String new2 = newpassword2.getText().toString();
                if (!old.equals(mypwd)){ Toast.makeText(SwitchPassword.this,"Original password incorrect.",Toast.LENGTH_SHORT).show(); }
                else if (!new1.equals(new2)){ Toast.makeText(SwitchPassword.this,"Inconsistent passwords.",Toast.LENGTH_SHORT).show(); }
                else if (new1.isEmpty()){ Toast.makeText(SwitchPassword.this,"Choose a new password.",Toast.LENGTH_SHORT).show(); }
                else {
                    new AlertDialog.Builder(SwitchPassword.this)
                            .setTitle("Change Password")
                            .setMessage("Are you sure to switch your password?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String changepwdurl = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                                            +android.net.Uri.encode(MainPage.un, "UTF-8")
                                            +"&password="
                                            +android.net.Uri.encode(new1, "UTF-8");
                                    AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                                        boolean success;
                                        String jsonString;
                                        @Override
                                        protected String doInBackground(String... arg0) {
                                            success = true;
                                            jsonString = getJsonPage(changepwdurl);
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
                                                    Toast.makeText(SwitchPassword.this,"Password changed.",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }.execute("");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }

            }
        });
        changepwdclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newpassword1.setText("");
                newpassword2.setText("");
            }
        });
        changepwdback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
}
